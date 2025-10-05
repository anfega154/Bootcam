package co.com.anfega.api.dto;


import co.com.anfega.model.ability.Ability;

import java.time.LocalDate;
import java.util.List;

public record BootcampDTO(Long id, String name, String description, LocalDate releaseDate, int duration, List<Ability> abilities) {
}
