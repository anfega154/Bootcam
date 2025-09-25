package co.com.anfega.model.bootcam;

import co.com.anfega.model.ability.Ability;

import java.time.LocalDate;
import java.util.List;


public class Bootcam {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int durationWeeks;
    private List<Ability> abilities;

    public Bootcam() {

    }

    public Bootcam(String name, String description, LocalDate releaseDate, int durationWeeks, List<Ability> abilities) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.durationWeeks = durationWeeks;
        this.abilities = abilities;
    }

    public Bootcam(Long id, String name, String description, LocalDate releaseDate, int durationWeeks, List<Ability> abilities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.durationWeeks = durationWeeks;
        this.abilities = abilities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(int durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }
}
