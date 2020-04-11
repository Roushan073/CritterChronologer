package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    PetService petService;

    // In this post request we are getting ownerId (userId) as well as Pet details in the Request Body itself
    // Need to implement another method that will save pet by getting ownerId in PathVariable
    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        return petService.savePet(petDTO);
    }

    // In this post request we are getting ownerId in PathVariable and Pet details in Request Body
    @PostMapping("/{ownerId}")
    public PetDTO savePet(@RequestBody PetDTO petDTO, @PathVariable Long ownerId) {
        petDTO.setOwnerId(ownerId);
        return petService.savePet(petDTO);
    }

    // Get pet by petId
    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        return petService.getPetById(petId);
    }

    // Get all the pets
    @GetMapping
    public List<PetDTO> getPets(){
        return petService.getPets();
    }

    // get all pets for an owner by id
    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        return petService.getPetsByOwner(ownerId);
    }
}
