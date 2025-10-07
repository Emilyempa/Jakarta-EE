package org.example.api;

import org.example.model.PetDTO;
import org.example.service.PetService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PetResource {

    private static final Logger LOG = Logger.getLogger(PetResource.class);

    @Inject
    PetService petService;

    @POST
    public Response adoptPet(@Valid PetDTO pet) {
        Long id = petService.addPet(pet);
        return Response.status(Response.Status.CREATED)
                .entity(Map.of("id", id))
                .build();
    }

    @GET
    public Response getAllPets(
            @QueryParam("species") String species,
            @QueryParam("sortBy") @DefaultValue("name") String sortBy,
            @QueryParam("order") @DefaultValue("asc") String order,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        if (offset < 0) {
            throw new BadRequestException("Offset must be >= 0");
        }

        if (limit < 0) {
            throw new BadRequestException("Limit must be >= 0 (0 means no limit)");
        }

        List<String> allowedSortFields = List.of("name", "species", "hungerLevel", "happiness");
        boolean isValidSort = allowedSortFields.stream()
                .anyMatch(field -> field.equalsIgnoreCase(sortBy));

        if (!isValidSort) {
            throw new BadRequestException("Invalid sortBy. Allowed values: name, species, hungerLevel, happiness");
        }

        if (!(order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc"))) {
            throw new BadRequestException("Invalid order. Allowed values: asc, desc");
        }

        LOG.infof("Fetching pets with sortBy=%s, order=%s, offset=%d, limit=%d", sortBy, order, offset, limit);

        List<PetDTO> pets = petService.getPetsFiltered(species, sortBy, order, offset, limit);

        int total = (species == null || species.isBlank())
                ? petService.getTotalCount()
                : petService.getFilteredCount(species);

        return Response.ok(pets)
                .header("X-Total-Count", total)
                .header("X-Offset", offset)
                .header("X-Limit", limit)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getPet(@PathParam("id") Long id) {
        PetDTO pet = petService.getPetById(id);
        if (pet == null) {
            throw new NotFoundException("Pet not found");
        }
        return Response.ok(pet).build();
    }

    @PUT
    @Path("/{id}/feed")
    public Response feedPet(@PathParam("id") Long id) {
        if (!petService.feedPet(id)) {
            throw new NotFoundException("Pet not found");
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/play")
    public Response playWithPet(@PathParam("id") Long id) {
        if (!petService.playWithPet(id)) {
            throw new NotFoundException("Pet not found");
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response releasePet(@PathParam("id") Long id) {
        if (!petService.removePet(id)) {
            throw new NotFoundException("Pet not found");
        }
        return Response.noContent().build();
    }
}