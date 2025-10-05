package co.com.anfega.api.helper.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
public abstract class BaseHandler {

    protected <T> Mono<ServerResponse> ok(String message, T body) {
        log.debug("Respuesta OK: {} - {}", message, body);
        LocalApiResponse<T> response = new LocalApiResponse<>(message, body);
        return ServerResponse.ok().bodyValue(response);
    }

    protected <T> Mono<ServerResponse> ok(String message) {
        LocalApiResponse<T> response = new LocalApiResponse<>(message);
        return ServerResponse.ok().bodyValue(response);
    }

    protected <T> Mono<ServerResponse> created(String message, T body) {
        log.debug("Recurso creado: {}", body);
        LocalApiResponse<T> response = new LocalApiResponse<>(message, body);
        return ServerResponse.status(HttpStatus.CREATED).bodyValue(response);
    }


    public <T> Mono<T> bodyToMonoValidated(Validator validator, ServerRequest request, Class<T> clazz) {
        return request.bodyToMono(clazz)
                .doOnNext(dto -> {
                    Set<ConstraintViolation<T>> errors = validator.validate(dto);
                    if (!errors.isEmpty()) {
                        throw new ConstraintViolationException(errors);
                    }
                });
    }
}
