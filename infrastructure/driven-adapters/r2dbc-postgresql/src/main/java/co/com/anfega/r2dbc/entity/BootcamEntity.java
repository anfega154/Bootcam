package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("bootcamp")
@Data
public class BootcamEntity {

    @Id
    private Long id;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;

    @Column("fecha_lanzamiento")
    private LocalDate releaseDate;

    @Column("duracion")
    private int duration;
}