package co.com.anfega.usecase.bootcamp;

import co.com.anfega.model.bootcamp.Bootcamp;
import co.com.anfega.model.bootcamp.gateways.BootcampRepository;
import co.com.anfega.model.common.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BootcampUseCaseTest {

    private BootcampRepository repository;
    private BootcampUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BootcampRepository.class);
        useCase = new BootcampUseCase(repository);
    }

    @Test
    void save_ShouldSave_WhenNameDoesNotExist() {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setName("Java");

        when(repository.findByName("Java")).thenReturn(Mono.empty());
        when(repository.save(bootcamp)).thenReturn(Mono.just(bootcamp));

        StepVerifier.create(useCase.save(bootcamp))
                .expectNext(bootcamp)
                .verifyComplete();

        verify(repository).findByName("Java");
        verify(repository).save(bootcamp);
    }

    @Test
    void save_ShouldError_WhenNameAlreadyExists() {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setName("Java");

        when(repository.findByName("Java")).thenReturn(Mono.just(bootcamp));

        StepVerifier.create(useCase.save(bootcamp))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().equals("El nombre ya existe"))
                .verify();

        verify(repository).findByName("Java");
        verify(repository, never()).save(any());
    }

    @Test
    void findAllPaginated_ShouldReturnPage_WhenRepositoryHasData() {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setName("Java");

        PageResponse<Bootcamp> response =
                new PageResponse<>(List.of(bootcamp), 0, 10, 1);

        when(repository.findAllPaginated(0, 10, "id", "asc", 1))
                .thenReturn(Mono.just(response));

        StepVerifier.create(useCase.findAllPaginated(0, 10, "id", "asc", 1))
                .expectNextMatches(r -> r.getContent().size() == 1 &&
                        r.getContent().get(0).getName().equals("Java"))
                .verifyComplete();

        verify(repository).findAllPaginated(0, 10, "id", "asc", 1);
    }

    @Test
    void findAllPaginated_ShouldReturnEmptyPage_WhenRepositoryIsEmpty() {
        when(repository.findAllPaginated(anyInt(), anyInt(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.findAllPaginated(0, 10, "id", "asc", 0))
                .expectNextMatches(r -> r.getContent().isEmpty() && r.getTotalElements() == 0)
                .verifyComplete();

        verify(repository).findAllPaginated(0, 10, "id", "asc", 0);
    }

    @Test
    void deleteById_ShouldCallRepository() {
        when(repository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteById(1L))
                .verifyComplete();

        verify(repository).deleteById(1L);
    }

    @Test
    void findById_ShouldReturnBootcamp_WhenExists() {
        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(1L);
        bootcamp.setName("Java");

        when(repository.findById(1L)).thenReturn(Mono.just(bootcamp));

        StepVerifier.create(useCase.findById(1L))
                .expectNext(bootcamp)
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findById(1L))
                .verifyComplete();

        verify(repository).findById(1L);
    }
}
