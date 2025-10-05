package co.com.anfega.api.service;

import co.com.anfega.api.dto.AbilityRequestDTO;
import co.com.anfega.api.dto.CreateBootcampDTO;
import co.com.anfega.api.helper.client.ApiResponse;
import co.com.anfega.api.helper.client.WebClientHelper;
import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

        return getAbilitiesByName(createBootcampDTO.getAbilities())
                .flatMap(abilities -> {
                    bootcamp.setAbilities(abilities);
                    return bootcampInputPort.save(bootcamp);
                });

    }

    public Mono<List<Bootcamp>> listBootcamps(int page, int size, String sortBy, String direction, int totalElements) {
        return bootcampInputPort.findAllPaginated(page, size, sortBy, direction, totalElements)
                .flatMap(pageResult -> {
                    List<String> abilityNames = pageResult.getContent().stream()
                            .flatMap(bootcamp -> bootcamp.getAbilities().stream())
                            .map(Ability::getName)
                            .distinct()
                            .toList();

                    return getAbilitiesByName(abilityNames)
                            .map(abilities -> enrichBootcamps(pageResult.getContent(), abilities));
                });
    }


    private List<Bootcamp> enrichBootcamps(List<Bootcamp> bootcamps, List<Ability> abilities) {
        bootcamps.forEach(bootcamp -> {
            List<Ability> enrichedAbilities = bootcamp.getAbilities().stream()
                    .map(ability -> abilities.stream()
                            .filter(existingAbility -> existingAbility.getName()
                                    .equalsIgnoreCase(ability.getName()))
                            .findFirst()
                            .map(match -> {
                                ability.setId(match.getId());
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


    @Cacheable(value = "abilitiesCache", unless = "#result == null")
    public Mono<List<Ability>> getAbilitiesByName(List<String> names) {
        AbilityRequestDTO request = new AbilityRequestDTO(names);
        if (names == null || names.isEmpty()) {
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
