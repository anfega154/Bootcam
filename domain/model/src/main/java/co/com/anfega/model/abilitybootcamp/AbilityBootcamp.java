package co.com.anfega.model.abilitybootcamp;

public class AbilityBootcamp {
    private Long abilityId;
    private Long bootcampId;

    public AbilityBootcamp() {

    }

    public AbilityBootcamp(Long abilityId, Long bootcampId) {
        this.abilityId = abilityId;
        this.bootcampId = bootcampId;
    }

    public Long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Long abilityId) {
        this.abilityId = abilityId;
    }

    public Long getBootcampId() {
        return bootcampId;
    }

    public void setBootcampId(Long bootcampId) {
        this.bootcampId = bootcampId;
    }
}
