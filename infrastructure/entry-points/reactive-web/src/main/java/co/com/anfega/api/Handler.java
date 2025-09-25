package co.com.anfega.api;

import co.com.anfega.api.helper.api.BaseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler extends BaseHandler {

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(body -> ServerResponse.ok().bodyValue("Received body: " + body));
    }

}
