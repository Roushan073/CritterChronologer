package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    PetService petService;

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        Pet reqPet = petDTOtoEntity(petDTO);
        Long ownerId = petDTO.getOwnerId();
        Long petId = petService.savePet(reqPet, ownerId);
        petDTO.setId(petId);
        return petDTO;
    }

    @PostMapping("/{ownerId}")
    public PetDTO savePet(@RequestBody PetDTO petDTO, @PathVariable Long ownerId) {
        Pet reqPet = petDTOtoEntity(petDTO);
        Long petId = petService.savePet(reqPet, ownerId);
        petDTO.setId(petId);
        return petDTO;
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        return petEntityToDTO(petService.getPetById(petId));
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<PetDTO> petDTOS = new ArrayList<>();
        for (Pet pet: petService.getPets()) {
            petDTOS.add(petEntityToDTO(pet));
        }

        return petDTOS;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<PetDTO> petDTOS = new ArrayList<>();
        for (Pet pet: petService.getPetsByOwner(ownerId)) {
            petDTOS.add(petEntityToDTO(pet));
        }

        return petDTOS;
    }

    /**
     * Copy Pet DTO matching properties to Pet Entity
     */
    public Pet petDTOtoEntity(PetDTO petDTO) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet);
        return pet;
    }

    /**
     * Copy Pet Entity matching properties to Pet DTO
     */
    public PetDTO petEntityToDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(pet, petDTO);

        /**
         * As Pet DTO contains only Ids of Owner, we need to extract them from
         * Customer and assign them to Pet DTO
         */
        petDTO.setOwnerId(pet.getCustomer().getId());
        return petDTO;
    }
}
