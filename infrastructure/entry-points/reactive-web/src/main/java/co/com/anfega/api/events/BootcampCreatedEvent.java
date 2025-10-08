package co.com.anfega.api.events;

import co.com.anfega.api.dto.AbilityDTO;
import co.com.anfega.api.dto.TechnologyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BootcampCreatedEvent {
    private Long id;
    private String name;
    private String description;
    private String launchDate;
    private String duration;
    private List<AbilityDTO> abilities;
    private List<TechnologyDTO> technologies;
    private int capabilitiesCount;
    private int technologiesCount;
    private int participantsCount;

    public String toJson() throws BootcampEventSerializationException {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new BootcampEventSerializationException("Error serializando BootcampCreatedEvent", e);
        }
    }
}

