package co.com.anfega.api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

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

