package co.com.anfega.model.bootcamp.gateways;

import co.com.anfega.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampInputPort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
}
