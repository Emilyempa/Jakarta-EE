package org.example.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PetDTO(
        @NotBlank String name,
        @NotBlank String species,
        @Min(0) @Max(5) int hungerLevel,
        @Min(0) @Max(5) int happiness) {

    // Builders
    public PetDTO withHungerLevel(int newLevel) {
        int boundedLevel = Math.max(0, Math.min(5, newLevel));
        return new PetDTO(name, species, boundedLevel, happiness);
    }

    public PetDTO withHappiness(int newHappiness) {
        int boundedHappiness = Math.max(0, Math.min(5, newHappiness));
        return new PetDTO(name, species, hungerLevel, boundedHappiness);
    }
}
