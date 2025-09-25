package co.com.anfega.model.bootcamp.gateways;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Mono;

public interface BootcampInputPort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<PageResponse<Ability>> findAllPaginated(int page, int size, String sortBy, String direction);

}
