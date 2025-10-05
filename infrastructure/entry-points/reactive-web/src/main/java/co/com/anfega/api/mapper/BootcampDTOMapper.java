package co.com.anfega.api.mapper;

import co.com.anfega.api.dto.BootcampDTO;
import co.com.anfega.model.bootcamp.Bootcamp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampDTOMapper {
    BootcampDTO toResponse(Bootcamp bootcamp);
}
