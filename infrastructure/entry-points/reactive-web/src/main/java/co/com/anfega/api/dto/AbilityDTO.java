package co.com.anfega.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbilityDTO {
    private Long id;
    private String name;
    private String description;
    private List<TechnologyDTO> technologies;
}
