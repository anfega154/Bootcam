package co.com.anfega.usecase.bootcam;


import co.com.anfega.model.bootcam.Bootcam;
import co.com.anfega.model.bootcam.gateways.BootcamInputPort;
import co.com.anfega.model.bootcam.gateways.BootcamRepository;
import reactor.core.publisher.Mono;

public class BootcamUseCase implements BootcamInputPort {

    private final BootcamRepository bootcamRepository;

    public BootcamUseCase(BootcamRepository bootcamRepository) {
        this.bootcamRepository = bootcamRepository;
    }


    @Override
    public Mono<Bootcam> save(Bootcam bootcam) {
        return null;
    }
}
