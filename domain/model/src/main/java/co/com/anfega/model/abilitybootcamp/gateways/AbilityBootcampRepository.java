package co.com.anfega.model.abilitybootcamp.gateways;

import co.com.anfega.model.abilitybootcamp.AbilityBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AbilityBootcampRepository {
    Flux<AbilityBootcamp> save(Long bootcampId, List<Long> abilitiesId);
    Flux<AbilityBootcamp> findByIdBootcamp(Long bootcampId);
    Mono<Void> deleteByIdBootcamp(Long bootcampId);
}
