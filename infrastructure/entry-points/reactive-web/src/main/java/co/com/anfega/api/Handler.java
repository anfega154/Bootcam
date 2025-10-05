package co.com.anfega.api;

import co.com.anfega.api.dto.CreateBootcampDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.BootcampDTOMapper;
import co.com.anfega.api.service.BootcampService;
import co.com.anfega.model.technology.Technology;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler extends BaseHandler {

    private final BootcampService bootcampService;
    private final Validator validator;
    private final BootcampDTOMapper bootcampDTOMapper;

    public Mono<ServerResponse> listenSaveBootcamp(ServerRequest request) {
        return bodyToMonoValidated(validator, request, CreateBootcampDTO.class)
                .flatMap(bootcampService::save)
                .map(bootcampDTOMapper::toResponse)
                .flatMap(response -> created("Bootcamp creado con exito", response));
    }

    public Mono<ServerResponse> listenListBootcamps(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        int totalElements = Integer.parseInt(request.queryParam("totalElements").orElse("0"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("asc");

        return bootcampService.listBootcamps(page, size, sortBy, direction,totalElements)
                .flatMap(bootcamps -> ok(bootcamps.isEmpty() ? "No se encontraron bootcamps" : "Bootcamps encontrados", bootcamps));
    }

}
