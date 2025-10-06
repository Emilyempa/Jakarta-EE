package org.example.service;

import org.example.model.PetDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;

@ApplicationScoped
public class PetService {

    private static final Logger LOG = Logger.getLogger(PetService.class);

    private final ConcurrentHashMap<Long, PetDTO> pets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PetService(){
        LOG.info("Initializing PetService with sample pets");
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
        LOG.info("PetService initialized with " + pets.size() + " pets");
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

    public List<PetDTO> getPetsFiltered(String species, String sortBy, String order, int offset, int limit) {
        List<PetDTO> result = new ArrayList<>(pets.values());

        result = filterBySpecies(result, species);
        result = sortPets(result, sortBy, order);
        return paginate(result, offset, limit);
    }

    // Filter
    private List<PetDTO> filterBySpecies(List<PetDTO> pets, String species) {
        if (species == null || species.isBlank()) {
            return List.copyOf(pets);
        }

        return List.copyOf(
                pets.stream()
                        .filter(p -> p.species().equalsIgnoreCase(species))
                        .toList()
        );
    }

    // Sort
    private List<PetDTO> sortPets(List<PetDTO> pets, String sortBy, String order) {
        Comparator<PetDTO> comparator = switch (sortBy.toLowerCase()) {
            case "happiness" -> Comparator.comparingInt(PetDTO::happiness);
            case "hungerlevel" -> Comparator.comparingInt(PetDTO::hungerLevel);
            case "species" -> Comparator.comparing(PetDTO::species, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(PetDTO::name, String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return pets.stream()
                .sorted(comparator)
                .toList();
    }

    // Pagination
    private List<PetDTO> paginate(List<PetDTO> pets, int offset, int limit) {
        int fromIndex = Math.max(0, offset);
        int toIndex = Math.min(pets.size(), fromIndex + limit);

        if (fromIndex >= pets.size()) {
            return List.of();
        }

        return List.copyOf(pets.subList(fromIndex, toIndex));
    }
}