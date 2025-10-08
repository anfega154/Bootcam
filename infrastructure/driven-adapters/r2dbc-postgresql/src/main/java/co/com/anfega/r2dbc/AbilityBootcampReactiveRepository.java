package co.com.anfega.r2dbc;

import co.com.anfega.r2dbc.entity.AbilityBootcampEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AbilityBootcampReactiveRepository extends ReactiveCrudRepository<AbilityBootcampEntity, Void>, ReactiveQueryByExampleExecutor<AbilityBootcampEntity> {
    Flux<AbilityBootcampEntity> findAllByBootcampId(Long bootcampId);
    Mono<Void> deleteAllByBootcampId(Long bootcampId);
}
