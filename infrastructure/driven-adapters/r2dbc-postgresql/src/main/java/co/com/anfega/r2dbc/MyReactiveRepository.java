package co.com.anfega.r2dbc;

import co.com.anfega.r2dbc.entity.BootcamEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MyReactiveRepository extends ReactiveCrudRepository<BootcamEntity, Long>, ReactiveQueryByExampleExecutor<BootcamEntity> {

}
