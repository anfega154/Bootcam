package co.com.anfega.r2dbc;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.r2dbc.entity.BootcamEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Bootcamp,
    BootcamEntity,
    Long,
        MyReactiveRepository
> implements BootcampRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
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
