package org.example.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class PetDTO {

    @NotBlank
    private final String name;

    @NotBlank
    private final String species;

    @Min(0)
    @Max(5)
    private final int hungerLevel;

    @Min(0)
    @Max(5)
    private final int happiness;

    public PetDTO(String name, String species, int hungerLevel, int happiness) {
        this.name = name;
        this.species = species;
        this.hungerLevel = hungerLevel;
        this.happiness = happiness;
    }

    public String getName() { return name; }
    public String getSpecies() { return species; }
    public int getHungerLevel() { return hungerLevel; }
    public int getHappiness() { return happiness; }

    // Builders
    public PetDTO withHungerLevel(int newLevel) {
        return new PetDTO(name, species, newLevel, happiness);
    }

    public PetDTO withHappiness(int newHappiness) {
        return new PetDTO(name, species, hungerLevel, newHappiness);
    }
}
