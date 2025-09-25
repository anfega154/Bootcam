package co.com.anfega.usecase.bootcamp;


import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampInputPort;
import co.com.anfega.model.bootcamp.gateways.BootcampRepository;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Mono;

public class BootcampUseCase implements BootcampInputPort {

    private final BootcampRepository bootcamRepository;

    public BootcampUseCase(BootcampRepository bootcamRepository) {
        this.bootcamRepository = bootcamRepository;
    }


    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return null;
    }

    @Override
    public Mono<PageResponse<Ability>> findAllPaginated(int page, int size, String sortBy, String direction) {
        return null;
    }
}
