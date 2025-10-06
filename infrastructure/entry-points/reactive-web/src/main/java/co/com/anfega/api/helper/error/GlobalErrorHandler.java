package co.com.anfega.api.helper.error;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    private final List<ExceptionStrategy> strategies;

    public GlobalErrorHandler(List<ExceptionStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ExceptionStrategy strategy = strategies.stream()
                .filter(s -> s.supports(ex))
                .findFirst()
                .orElseThrow();

        HttpStatus status = strategy.getStatus(ex);
        String error = strategy.getError(ex);

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                OffsetDateTime.now(),
                status.value(),
                error,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(body.getBytes())));
    }
}
