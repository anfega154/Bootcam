package co.com.anfega.r2dbc;

import co.com.anfega.model.ability.Ability;
import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampRepository;
import co.com.anfega.model.common.PageResponse;
import co.com.anfega.model.common.PaginationHelper;
import co.com.anfega.r2dbc.entity.BootcamEntity;
import co.com.anfega.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

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
        return repository.findByName(bootcamp.getName())
                .flatMap(existing -> Mono.<Bootcamp>error(new IllegalStateException("El nombre ya existe")))
                .switchIfEmpty(Mono.defer(() -> {
                    BootcamEntity entity = new BootcamEntity();
                    entity.setName(bootcamp.getName());
                    entity.setDescription(bootcamp.getDescription());
                    entity.setReleaseDate(bootcamp.getReleaseDate());
                    entity.setDuration(bootcamp.getDuration());

                    return repository.save(entity)
                            .map(this::toBootcamp)
                            .onErrorResume(e -> Mono.error(new IllegalStateException("Error guardando bootcamp: " + e.getMessage())));
                }));
    }

    @Override
    public Mono<Bootcamp> findByName(String name) {
        return repository.findByName(name)
                .map(this::toBootcamp)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error buscando bootcamp por nombre: " + e.getMessage())));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error eliminando bootcamp: " + e.getMessage())));
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return repository.findById(id)
                .map(this::toBootcamp)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error buscando bootcamp por ID: " + e.getMessage())));
    }

    @Override
    public Flux<Bootcamp> findByIdIn(List<Long> ids) {
        return repository.findAllById(ids)
                .map(this::toBootcamp)
                .onErrorResume(e -> Mono.error(new IllegalStateException("Error buscando bootcamps por IDs: " + e.getMessage())));
    }

    @Override
    public Mono<PageResponse<Bootcamp>> findAllPaginated(int page, int size, String sortBy, String direction) {
        return repository.findAll()
                .map(this::toBootcamp)
                .collectList()
                .map(list -> paginateAndSortBootcamps(list, page, size, sortBy, direction));
    }

    private Bootcamp toBootcamp(BootcamEntity entity) {
        return new Bootcamp(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getReleaseDate(),
                entity.getDuration()
        );
    }

    private PageResponse<Bootcamp> paginateAndSortBootcamps(
            List<Bootcamp> list, int page, int size, String sortBy, String direction) {

        if (sortBy == null) sortBy = "";
        sortBy = sortBy.trim().toLowerCase();

        if ("abilities".equalsIgnoreCase(sortBy)) {
            List<Bootcamp> copy = new ArrayList<>(list == null ? Collections.emptyList() : list);

            Comparator<Bootcamp> cmp = Comparator.comparingInt(
                    a -> a.getAbilities() == null ? 0 : a.getAbilities().size()
            );

            if ("desc".equalsIgnoreCase(direction)) {
                cmp = cmp.reversed();
            }

            copy.sort(cmp);
            return PageResponse.of(copy, page, size);
        }
        return switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "name" -> PaginationHelper.paginateAndSort(list, page, size, direction, Bootcamp::getName);
            case "description" ->
                    PaginationHelper.paginateAndSort(list, page, size, direction, Bootcamp::getDescription);
            default -> PaginationHelper.paginateAndSort(list, page, size, direction, a -> String.valueOf(a.getId()));
        };
    }
}
