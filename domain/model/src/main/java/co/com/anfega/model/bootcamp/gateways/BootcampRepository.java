package co.com.anfega.model.bootcamp.gateways;

import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<PageResponse<Bootcamp>> findAllPaginated(int page, int size, String sortBy, String direction, int totalElements);
    Mono<Bootcamp> findByName(String name);
}
