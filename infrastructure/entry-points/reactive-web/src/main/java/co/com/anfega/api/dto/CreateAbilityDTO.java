package co.com.anfega.api.dto;

import co.com.anfega.model.technology.Technology;
import lombok.Data;

import java.util.List;

@Data
public class CreateAbilityDTO {
    private String name;
    private String description;
    private List<Technology> technologies;
}
