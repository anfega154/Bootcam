package co.com.anfega.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateBootcampDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @Size(min = 1, max = 4 , message = "Debe tener minimo 1 y maximo 4 habilidades")
    private List<Long> abilities;
}
