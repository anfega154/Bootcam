package co.com.anfega.api.helper.service;

import co.com.anfega.model.bootcamp.Bootcamp;
import lombok.Data;

@Data
public class SagaContext {
    private Bootcamp bootcamp;
    private boolean techDeleted;
    private boolean abilitiesDeleted;
    private boolean bootcampDeleted;
}
