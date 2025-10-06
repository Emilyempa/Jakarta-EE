package org.example.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PetDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validPet() {
        PetDTO pet = new PetDTO("Bella", "Cat", 3, 4);
        Set<ConstraintViolation<PetDTO>> violations = validator.validate(pet);
        assertEquals(0, violations.size());
    }

    @Test
    void invalidNameBlank() {
        PetDTO pet = new PetDTO("", "Horse", 0, 0);
        Set<ConstraintViolation<PetDTO>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
        assertEquals("must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void invalidHungerLevelTooLow() {
        PetDTO pet = new PetDTO("Bella", "Cat", -1, 0);
        Set<ConstraintViolation<PetDTO>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
        assertEquals("must be greater than or equal to 0", violations.iterator().next().getMessage());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void invalidHappinessTooHigh() {
        PetDTO pet = new PetDTO("Bella", "Cat", 0, 6);
        Set<ConstraintViolation<PetDTO>> violations = validator.validate(pet);
        assertEquals(1, violations.size());
        assertEquals("must be less than or equal to 5", violations.iterator().next().getMessage());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void multipleValidationErrors() {
        PetDTO pet = new PetDTO("", "", -1, 10);
        Set<ConstraintViolation<PetDTO>> violations = validator.validate(pet);
        assertEquals(4, violations.size());
    }

    @Test
    void testHungerLevelMethod() {
        PetDTO original = new PetDTO("Bella", "Cat", 3, 4);
        PetDTO modified = original.withHungerLevel(1);

        assertEquals("Bella", modified.name());
        assertEquals("Cat", modified.species());
        assertEquals(1, modified.hungerLevel());
        assertEquals(4, modified.happiness());

        // Verify the original is unchanged (immutability)
        assertEquals(3, original.hungerLevel());
    }

    @Test
    void testHappinessMethod() {
        PetDTO original = new PetDTO("Max", "Dog", 2, 3);
        PetDTO modified = original.withHappiness(5);

        assertEquals("Max", modified.name());
        assertEquals("Dog", modified.species());
        assertEquals(2, modified.hungerLevel());
        assertEquals(5, modified.happiness());

        // Verify the original is unchanged (immutability)
        assertEquals(3, original.happiness());
    }
}