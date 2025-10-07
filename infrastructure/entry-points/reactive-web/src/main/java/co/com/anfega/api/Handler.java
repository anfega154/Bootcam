package co.com.anfega.api;

import co.com.anfega.api.dto.CreateBootcampDTO;
import co.com.anfega.api.dto.RequestByIdsDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.BootcampDTOMapper;
import co.com.anfega.api.service.BootcampService;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler extends BaseHandler {

    private final BootcampService bootcampService;
    private final Validator validator;
    private final BootcampDTOMapper bootcampDTOMapper;

    private static final String MSG_BOOTCAMP_CREATED = "Bootcamp creado con exito";
    private static final String MSG_BOOTCAMP_DELETED = "Bootcamp eliminado con exito";
    private static final String MSG_NO_BOOTCAMPS = "No se encontraron bootcamps";
    private static final String MSG_BOOTCAMPS_FOUND = "Bootcamps encontrados";

    public Mono<ServerResponse> listenSaveBootcamp(ServerRequest request) {
        return bodyToMonoValidated(validator, request, CreateBootcampDTO.class)
                .flatMap(bootcampService::save)
                .map(bootcampDTOMapper::toResponse)
                .flatMap(response -> created(MSG_BOOTCAMP_CREATED, response));
    }

    public Mono<ServerResponse> listenListBootcamps(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        int totalElements = Integer.parseInt(request.queryParam("totalElements").orElse("0"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("asc");

        return bootcampService.listBootcamps(page, size, sortBy, direction, totalElements)
                .flatMap(bootcamps -> ok(bootcamps.isEmpty() ? MSG_NO_BOOTCAMPS : MSG_BOOTCAMPS_FOUND, bootcamps));
    }

    public Mono<ServerResponse> listenDeleteBootcampById(ServerRequest request) {
        Long id = Long.parseLong(request.queryParam("id").orElse("0"));
        return bootcampService.deleteBootcamp(id)
                .then(ok(MSG_BOOTCAMP_DELETED));
    }

    public Mono<ServerResponse> listenGetBootcampByIds(ServerRequest request) {
        return bodyToMonoValidated(validator, request, RequestByIdsDTO.class)
                .flatMapMany(dto -> bootcampService.findByIdIn(dto.getIds()))
                .map(bootcampDTOMapper::toResponse)
                .collectList()
                .flatMap(bootcamps -> ok(bootcamps.isEmpty() ? MSG_NO_BOOTCAMPS : MSG_BOOTCAMPS_FOUND, bootcamps));
    }

}
