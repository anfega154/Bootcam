package co.com.anfega.usecase.bootcamp;


import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampInputPort;
import co.com.anfega.model.bootcamp.gateways.BootcampRepository;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public class BootcampUseCase implements BootcampInputPort {

    private final BootcampRepository bootcamRepository;

    public BootcampUseCase(BootcampRepository bootcamRepository) {
        this.bootcamRepository = bootcamRepository;
    }


    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return bootcamRepository.findByName(bootcamp.getName())
                .flatMap(existing -> Mono.<Bootcamp>error(new IllegalStateException("El nombre ya existe")))
                .switchIfEmpty(Mono.defer(() -> bootcamRepository.save(bootcamp)));

    }

    @Override
    public Mono<PageResponse<Bootcamp>> findAllPaginated(int page, int size, String sortBy, String direction, int totalElements) {
        return bootcamRepository.findAllPaginated(page, size, sortBy, direction, totalElements)
                .switchIfEmpty(Mono.just(new PageResponse<>(List.of(), page, size, 0)));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return bootcamRepository.deleteById(id);
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return bootcamRepository.findById(id);
    }
}
