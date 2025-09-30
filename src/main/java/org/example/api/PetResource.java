package org.example.api;

import org.example.model.PetDTO;
import org.example.service.PetService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PetResource {

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
    public List<PetDTO> getAllPets() {
        return petService.getAllPets();
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
        PetDTO pet = petService.getPetById(id);
        if (pet == null) {
            throw new NotFoundException("Pet not found");
        }
        petService.feedPet(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/play")
    public Response playWithPet(@PathParam("id") Long id) {
        PetDTO pet = petService.getPetById(id);
        if (pet == null) {
            throw new NotFoundException("Pet not found");
        }
        petService.playWithPet(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response releasePet(@PathParam("id") Long id) {
        PetDTO pet = petService.getPetById(id);
        if (pet == null) {
            throw new NotFoundException("Pet not found");
        }
            petService.removePet(id);
        return Response.noContent().build();
    }
}
