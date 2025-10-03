package org.example.service;

import org.example.model.PetDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;

@ApplicationScoped
public class PetService {

    private final ConcurrentHashMap<Long, PetDTO> pets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PetService(){
        addPet(new PetDTO("Peggy", "Dog", 0, 5));
        addPet(new PetDTO("Bella", "Cat", 3, 3));
        addPet(new PetDTO("Ragnar", "Cat", 2, 2));
        addPet(new PetDTO("Ruth", "Dog", 1, 1));
        addPet(new PetDTO("Chloe", "Cat", 0, 0));
        addPet(new PetDTO("Luna", "Dog", 5, 5));
        addPet(new PetDTO("Signe", "Cat", 4, 4));
        addPet(new PetDTO("Kitty", "Cat", 3, 3));
        addPet(new PetDTO("Olly", "Dog", 2, 2));
        addPet(new PetDTO("Stubbur", "Dog", 1, 1));
        addPet(new PetDTO("Rosa", "Cat", 0, 5));
    }

    public List<PetDTO> getAllPets() {
        return List.copyOf(pets.values());
    }

    public PetDTO getPetById(Long id) {
        return pets.get(id);
    }

    public Long addPet(PetDTO pet) {
        Long id = idGenerator.getAndIncrement();
        pets.put(id, pet);
        return id;
    }

    public boolean feedPet(Long id) {
        return pets.computeIfPresent(id, (k, pet) ->
                pet.withHungerLevel(Math.max(0, pet.hungerLevel() - 1))
        ) != null;
    }

    public boolean playWithPet(Long id) {
        return pets.computeIfPresent(id, (k, pet) ->
                pet.withHappiness(Math.min(5, pet.happiness() + 1))
        ) != null;
    }

    public boolean removePet(Long id) {
        return pets.remove(id) != null;
    }
}
