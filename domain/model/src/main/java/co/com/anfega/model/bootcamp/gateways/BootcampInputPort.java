package co.com.anfega.model.bootcamp.gateways;

import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.common.PageResponse;
import reactor.core.publisher.Mono;

public interface BootcampInputPort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<PageResponse<Bootcamp>> findAllPaginated(int page, int size, String sortBy, String direction, int totalElements);
    Mono<Void> deleteById(Long id);
    Mono<Bootcamp> findById(Long id);
}
