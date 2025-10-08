package co.com.anfega.r2dbc;

import co.com.anfega.model.abilitybootcamp.AbilityBootcamp;
import co.com.anfega.model.abilitybootcamp.gateways.AbilityBootcampRepository;
import co.com.anfega.r2dbc.entity.AbilityBootcampEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class AbilityBootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        AbilityBootcamp,
        AbilityBootcampEntity,
        Void,
        AbilityBootcampReactiveRepository
        > implements AbilityBootcampRepository {
    public AbilityBootcampReactiveRepositoryAdapter(AbilityBootcampReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, AbilityBootcamp.class));
    }

    @Override
    public Flux<AbilityBootcamp> save(Long bootcampId, List<Long> abilitiesId) {
        return Flux.fromIterable(abilitiesId)
                .flatMap(idAbility -> {
                    AbilityBootcampEntity entity = new AbilityBootcampEntity();
                    entity.setBootcampId(bootcampId);
                    entity.setAbilityId(idAbility);
                    return repository.save(entity);
                })
                .map(savedEntity -> mapper.map(savedEntity, AbilityBootcamp.class))
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error creando el registro: " + e.getMessage())));
    }

    @Override
    public Flux<AbilityBootcamp> findByIdBootcamp(Long bootcampId) {
        return repository.findAllByBootcampId(bootcampId)
                .map(entity -> new AbilityBootcamp(
                        entity.getAbilityId(),
                        entity.getBootcampId()
                ))
                .onErrorResume(e -> Flux.error(new IllegalStateException("Error buscando capacidades por bootcamp: " + e.getMessage())));
    }

    @Override
    public Mono<Void> deleteByIdBootcamp(Long bootcampId) {
        return repository.deleteAllByBootcampId(bootcampId)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error eliminando capacidades por bootcamp: " + e.getMessage())));
    }
}
