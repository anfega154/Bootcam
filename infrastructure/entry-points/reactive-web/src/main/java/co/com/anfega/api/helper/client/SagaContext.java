package co.com.anfega.api.helper.client;

import co.com.anfega.model.bootcamp.Bootcamp;

public class SagaContext {
    private Bootcamp bootcamp;
    private boolean techDeleted;
    private boolean abilitiesDeleted;
    private boolean bootcampDeleted;

    public Bootcamp getBootcamp() {
        return bootcamp;
    }

    public void setBootcamp(Bootcamp bootcamp) {
        this.bootcamp = bootcamp;
    }

    public boolean isTechDeleted() {
        return techDeleted;
    }

    public void setTechDeleted(boolean techDeleted) {
        this.techDeleted = techDeleted;
    }

    public boolean isAbilitiesDeleted() {
        return abilitiesDeleted;
    }

    public void setAbilitiesDeleted(boolean abilitiesDeleted) {
        this.abilitiesDeleted = abilitiesDeleted;
    }

    public boolean isBootcampDeleted() {
        return bootcampDeleted;
    }

    public void setBootcampDeleted(boolean bootcampDeleted) {
        this.bootcampDeleted = bootcampDeleted;
    }
}
