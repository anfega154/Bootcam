package co.com.anfega.r2dbc.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "capacidad_bootcamp")
public class AbilityBootcampEntity {
    @Column("capacidad_id")
    private Long abilityId;

    @Column("bootcamp_id")
    private Long bootcampId;
}
