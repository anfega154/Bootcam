package co.com.anfega.api.service;

import co.com.anfega.api.dto.*;
import co.com.anfega.api.events.BootcampCreatedEvent;
import co.com.anfega.api.events.BootcampEventPublisher;
import co.com.anfega.api.events.BootcampEventSerializationException;
import co.com.anfega.api.helper.service.SagaContext;
import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampInputPort;
import co.com.anfega.model.technology.Technology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.com.anfega.consumer.client.WebClientHelper;
import co.com.anfega.consumer.ApiResponse;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootcampService {

    private final BootcampInputPort bootcampInputPort;
    private final WebClientHelper webClientHelper;

    public Mono<Bootcamp> save(CreateBootcampDTO createBootcampDTO) {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setName(createBootcampDTO.getName());
        bootcamp.setDescription(createBootcampDTO.getDescription());
        bootcamp.setReleaseDate(createBootcampDTO.getReleaseDate());
        bootcamp.setDuration(createBootcampDTO.getDuration());

        return getAbilitiesByIds(createBootcampDTO.getAbilities())
                .map(abilities -> {
                    bootcamp.setAbilities(abilities);
                    List<Bootcamp> enriched = enrichBootcamps(List.of(bootcamp), abilities);
                    return enriched.getFirst();
                })
                .flatMap(bootcampInputPort::save);
    }


    public Mono<List<Bootcamp>> listBootcamps(int page, int size, String sortBy, String direction) {
        return bootcampInputPort.findAllPaginated(page, size, sortBy, direction)
                .flatMap(pageResult -> {
                    List<Long> abilityIds = pageResult.getContent().stream()
                            .flatMap(bootcamp -> bootcamp.getAbilities().stream())
                            .map(Ability::getId)
                            .distinct()
                            .toList();

                    return getAbilitiesByIds(abilityIds)
                            .map(abilities -> enrichBootcamps(pageResult.getContent(), abilities));
                });
    }

    public Mono<Void> deleteBootcamp(Long bootcampId) {
        SagaContext context = new SagaContext();

        return bootcampInputPort.findById(bootcampId)
                .switchIfEmpty(Mono.error(new RuntimeException("Bootcamp no encontrado")))
                .flatMap(bootcamp -> {
                    List<Long> abilityIds = bootcamp.getAbilities().stream()
                            .map(Ability::getId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    return getAbilitiesByIds(abilityIds)
                            .map(abilities -> {
                                List<Bootcamp> enriched = enrichBootcamps(List.of(bootcamp), abilities);
                                return enriched.getFirst();
                            });
                })
                .flatMap(bootcamp -> {
                    context.setBootcamp(bootcamp);

                    return bootcampInputPort.findAllPaginated(0, Integer.MAX_VALUE, null, null)
                            .flatMap(pageResponse -> {
                                List<Bootcamp> allBootcamps = pageResponse.getContent();

                                List<Bootcamp> otherBootcamps = allBootcamps.stream()
                                        .filter(b -> !b.getId().equals(bootcamp.getId()))
                                        .toList();

                                boolean hasSharedAbilities = bootcamp.getAbilities().stream()
                                        .anyMatch(ability ->
                                                otherBootcamps.stream()
                                                        .flatMap(b -> b.getAbilities().stream())
                                                        .anyMatch(otherAbility ->
                                                                Objects.equals(otherAbility.getId(), ability.getId()))
                                        );

                                if (hasSharedAbilities) {
                                    log.info("El bootcamp {} tiene capacidades compartidas. Se eliminará solo el bootcamp, sin afectar capacidades ni tecnologías.", bootcampId);
                                    return deleteBootcamp(context);
                                } else {
                                    log.info("El bootcamp {} tiene capacidades exclusivas. Se eliminarán capacidades y tecnologías asociadas.", bootcampId);
                                    return deleteTechnologies(context)
                                            .then(deleteAbilities(context))
                                            .then(deleteBootcamp(context))
                                            .doOnSuccess(v -> log.info("Saga completada exitosamente para bootcamp {}", bootcampId))
                                            .onErrorResume(e -> {
                                                log.error("Error en la saga de eliminación: {}", e.getMessage(), e);
                                                return compensate(context, e);
                                            });
                                }
                            });
                });
    }


    public Flux<Bootcamp> findByIdIn(List<Long> ids) {
        return bootcampInputPort.findByIdIn(ids)
                .collectList()
                .flatMapMany(bootcamps -> {
                    List<Long> abilityIds = bootcamps.stream()
                            .flatMap(b -> b.getAbilities().stream())
                            .map(Ability::getId)
                            .distinct()
                            .toList();

                    return getAbilitiesByIds(abilityIds)
                            .map(abilities -> enrichBootcamps(bootcamps, abilities))
                            .flatMapMany(Flux::fromIterable);
                });
    }

    private Mono<Void> deleteBootcamp(SagaContext context) {
        return bootcampInputPort.deleteById(context.getBootcamp().getId())
                .doOnSuccess(v -> {
                    log.info("Bootcamp {} eliminado correctamente", context.getBootcamp().getId());
                    context.setBootcampDeleted(true);
                });
    }


    private Mono<Void> deleteTechnologies(SagaContext context) {
        List<Long> techIds = context.getBootcamp().getAbilities().stream()
                .flatMap(a -> a.getTechnologies().stream())
                .map(Technology::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (techIds.isEmpty()) {
            log.info("Bootcamp {} no tiene tecnologías asociadas", context.getBootcamp().getId());
            return Mono.empty();
        }

        RequestByIdsDTO deleteIdsDTO = new RequestByIdsDTO();
        deleteIdsDTO.setIds(techIds);

        return webClientHelper.delete(
                        "http://localhost:8088/api/v1/tecnologias",
                        null,
                        deleteIdsDTO,
                        new ParameterizedTypeReference<ApiResponse<Void>>() {
                        }
                )
                .doOnSuccess(v -> {
                    log.info("Tecnologías eliminadas correctamente: {}", techIds);
                    context.setTechDeleted(true);
                }).then();
    }

    private Mono<Void> deleteAbilities(SagaContext context) {
        List<Long> abilityIds = context.getBootcamp().getAbilities().stream()
                .map(Ability::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (abilityIds.isEmpty()) {
            log.info("Bootcamp {} no tiene capacidades asociadas", context.getBootcamp().getId());
            return Mono.empty();
        }

        RequestByIdsDTO deleteIdsDTO = new RequestByIdsDTO();
        deleteIdsDTO.setIds(abilityIds);

        return webClientHelper.delete(
                        "http://localhost:8089/api/v1/capacidades",
                        null,
                        deleteIdsDTO,
                        new ParameterizedTypeReference<ApiResponse<Void>>() {
                        }
                )
                .doOnSuccess(v -> {
                    log.info("Capacidades eliminadas correctamente: {}", abilityIds);
                    context.setAbilitiesDeleted(true);
                }).then();
    }

    private Mono<Void> compensate(SagaContext context, Throwable error) {
        log.warn("Iniciando compensación por error: {}", error.getMessage());

        Bootcamp bootcamp = context.getBootcamp();

        List<CreateTechnologyDTO> techDTOs = bootcamp.getAbilities().stream()
                .flatMap(a -> a.getTechnologies().stream())
                .map(t -> {
                    CreateTechnologyDTO dto = new CreateTechnologyDTO();
                    dto.setName(t.getName());
                    dto.setDescription(t.getDescription());
                    return dto;
                })
                .toList();

        List<CreateAbilityDTO> abilityDTOs = bootcamp.getAbilities().stream()
                .map(a -> {
                    CreateAbilityDTO dto = new CreateAbilityDTO();
                    dto.setName(a.getName());
                    dto.setDescription(a.getDescription());
                    dto.setTechnologies(a.getTechnologies());
                    return dto;
                })
                .toList();

        Mono<Void> restoreTech = Mono.empty();
        Mono<Void> restoreAbilities = Mono.empty();

        if (context.isAbilitiesDeleted()) {
            log.info("Compensando capacidades eliminadas...");
            restoreAbilities = webClientHelper.post(
                    "http://localhost:8089/api/v1/capacidades",
                    null,
                    abilityDTOs,
                    new ParameterizedTypeReference<ApiResponse<List<Ability>>>() {
                    }
            ).then();
        }

        if (context.isTechDeleted()) {
            log.info("Compensando tecnologías eliminadas...");
            restoreTech = webClientHelper.post(
                    "http://localhost:8088/api/v1/tecnologias",
                    null,
                    techDTOs,
                    new ParameterizedTypeReference<ApiResponse<List<Technology>>>() {
                    }
            ).then();
        }

        return restoreAbilities.then(restoreTech)
                .doOnTerminate(() -> log.info("Compensación finalizada para bootcamp {}", bootcamp.getId()));
    }

    private List<Bootcamp> enrichBootcamps(List<Bootcamp> bootcamps, List<Ability> abilities) {
        bootcamps.forEach(bootcamp -> {
            List<Ability> enrichedAbilities = bootcamp.getAbilities().stream()
                    .map(ability -> abilities.stream()
                            .filter(existingAbility -> existingAbility.getId()
                                    .equals(ability.getId()))
                            .findFirst()
                            .map(match -> {
                                ability.setId(match.getId());
                                ability.setName(match.getName());
                                ability.setDescription(match.getDescription());
                                ability.setTechnologies(match.getTechnologies());
                                return ability;
                            })
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            bootcamp.setAbilities(enrichedAbilities);
        });
        return bootcamps;
    }


    private Mono<List<Ability>> getAbilitiesByIds(List<Long> ids) {
        AbilityRequestDTO request = new AbilityRequestDTO(ids);
        if (ids == null || ids.isEmpty()) {
            return Mono.just(List.of());
        }
        return webClientHelper.post(
                        "http://localhost:8089/api/v1/capacidades/all",
                        null,
                        request,
                        new ParameterizedTypeReference<ApiResponse<List<Ability>>>() {
                        }
                )
                .doOnSuccess(response -> log.info("Capacidades obtenidas: {}", response))
                .map(ApiResponse::getContent)
                .onErrorResume(e -> {
                    log.error("Error al obtener capacidades: {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("No se pudieron obtener las capacidades, valide la lista de capacidades", e));
                });
    }

}
