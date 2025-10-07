package co.com.anfega.api;

import co.com.anfega.api.config.BootcampPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final BootcampPath bootcampPath;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(bootcampPath.getBootcamp()), handler::listenSaveBootcamp)
                .andRoute(GET(bootcampPath.getBootcamp()), handler::listenListBootcamps)
                .andRoute(DELETE(bootcampPath.getBootcamp()), handler::listenDeleteBootcampById)
                .andRoute(POST(bootcampPath.getBootcampValidate()), handler::listenGetBootcampByIds);
    }
}
