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
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
                    String abilitiesStr = String.join(",", bootcamp.getAbilities().stream()
                            .map(Ability::getName)
                            .toList());
                    entity.setAbilities(abilitiesStr);

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
    public Mono<PageResponse<Bootcamp>> findAllPaginated(int page, int size, String sortBy, String direction, int totalElements) {
        return repository.findAll()
                .map(this::toBootcamp)
                .collectList()
                .map(list -> paginateAndSortBootcamps(list, page, size, sortBy, direction, totalElements));
    }

    private Bootcamp toBootcamp(BootcamEntity entity) {
        List<Ability> abilities = (entity.getAbilities() != null && !entity.getAbilities().isEmpty()) ?
                Arrays.stream(entity.getAbilities().split(","))
                        .map(name -> new Ability(name, null, null))
                        .toList() : Collections.emptyList();
        return new Bootcamp(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getReleaseDate(),
                entity.getDuration(),
                abilities
        );
    }

    private PageResponse<Bootcamp> paginateAndSortBootcamps(
            List<Bootcamp> list, int page, int size, String sortBy, String direction, int totalElements) {
        if ("abilities".equalsIgnoreCase(sortBy)) {
            list = list.stream()
                    .filter(a -> a.getAbilities().size() == totalElements)
                    .collect(Collectors.toCollection(ArrayList::new));
            return PaginationHelper.paginateAndSort(list, page, size, direction, a -> a.getAbilities().size());
        }
        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "name":
                return PaginationHelper.paginateAndSort(list, page, size, direction, Bootcamp::getName);
            case "description":
                return PaginationHelper.paginateAndSort(list, page, size, direction, Bootcamp::getDescription);
            default:
                return PaginationHelper.paginateAndSort(list, page, size, direction, a -> String.valueOf(a.getId()));
        }
    }
}
