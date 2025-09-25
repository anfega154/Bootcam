package co.com.anfega.model.bootcamp.gateways;

import co.com.anfega.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
}
