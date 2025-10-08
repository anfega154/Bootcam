package co.com.anfega.usecase.bootcamp;


import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.abilitybootcamp.gateways.AbilityBootcampRepository;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampInputPort;
import co.com.anfega.model.bootcamp.gateways.BootcampRepository;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class BootcampUseCase implements BootcampInputPort {

    private final BootcampRepository bootcamRepository;
    private final AbilityBootcampRepository abilityBootcampRepository;

    public BootcampUseCase(BootcampRepository bootcamRepository, AbilityBootcampRepository abilityBootcampRepository) {
        this.bootcamRepository = bootcamRepository;
        this.abilityBootcampRepository = abilityBootcampRepository;
    }


    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return bootcamRepository.findByName(bootcamp.getName())
                .flatMap(existing -> Mono.<Bootcamp>error(new IllegalStateException("El nombre ya existe")))
                .switchIfEmpty(Mono.defer(() -> bootcamRepository.save(bootcamp)))
                .flatMap(savedBootcamp ->
                        abilityBootcampRepository.save(
                                        savedBootcamp.getId(),
                                        bootcamp.getAbilities().stream().map(Ability::getId).toList()
                                )
                                .collectList()
                                .thenReturn(savedBootcamp)
                );

    }

    @Override
    public Mono<PageResponse<Bootcamp>> findAllPaginated(int page, int size, String sortBy, String direction) {
        return bootcamRepository.findAllPaginated(page, size, sortBy, direction)
                .flatMap(pageResponse -> {
                    List<Bootcamp> bootcamps = pageResponse.getContent();
                    return Flux.fromIterable(bootcamps)
                            .flatMap(bootcamp -> abilityBootcampRepository.findByIdBootcamp(bootcamp.getId())
                                    .collectList()
                                    .map(abilityBootcamps -> {
                                        List<Ability> abilities = abilityBootcamps.stream()
                                                .map(abilityBootcamp -> {
                                                    Ability ability = new Ability();
                                                    ability.setId(abilityBootcamp.getAbilityId());
                                                    return ability;
                                                })
                                                .toList();
                                        bootcamp.setAbilities(abilities);
                                        return bootcamp;
                                    })
                            )
                            .collectList()
                            .map(updatedBootcamps -> new PageResponse<>(
                                            updatedBootcamps,
                                            pageResponse.getPage(),
                                            pageResponse.getSize(),
                                            pageResponse.getTotalElements()
                                    )
                            );
                })
                .switchIfEmpty(Mono.error(new IllegalStateException("No hay bootcamps registrados")));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return bootcamRepository.deleteById(id)
                .then(abilityBootcampRepository.deleteByIdBootcamp(id));
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return bootcamRepository.findById(id)
                .flatMap(bootcamp ->
                        abilityBootcampRepository.findByIdBootcamp(bootcamp.getId())
                                .collectList()
                                .map(abilityBootcamps -> {
                                    List<Ability> abilities = abilityBootcamps.stream()
                                            .map(abilityBootcamp -> {
                                                Ability ability = new Ability();
                                                ability.setId(abilityBootcamp.getAbilityId());
                                                return ability;
                                            })
                                            .toList();
                                    bootcamp.setAbilities(abilities);
                                    return bootcamp;
                                })
                );
    }

    @Override
    public Flux<Bootcamp> findByIdIn(List<Long> ids) {
        return bootcamRepository.findByIdIn(ids)
                .flatMap(bootcamp ->
                        abilityBootcampRepository.findByIdBootcamp(bootcamp.getId())
                                .collectList()
                                .map(abilityBootcamps -> {
                                    List<Ability> abilities = abilityBootcamps.stream()
                                            .map(abilityBootcamp -> {
                                                Ability ability = new Ability();
                                                ability.setId(abilityBootcamp.getAbilityId());
                                                return ability;
                                            })
                                            .toList();
                                    bootcamp.setAbilities(abilities);
                                    return bootcamp;
                                })
                );
    }
}
