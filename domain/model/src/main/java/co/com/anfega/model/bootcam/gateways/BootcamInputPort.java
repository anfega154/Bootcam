package co.com.anfega.model.bootcam.gateways;

import co.com.anfega.model.bootcam.Bootcam;
import reactor.core.publisher.Mono;

public interface BootcamInputPort {
    Mono<Bootcam> save(Bootcam bootcam);
}
