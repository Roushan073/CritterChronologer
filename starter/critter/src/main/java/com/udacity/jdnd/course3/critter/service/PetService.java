package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepository;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    CustomerRepository customerRepository;

    /**
     * Given a Pet DTO, create the Pet and return the created Pet details
     */
    public PetDTO savePet(PetDTO petDTO) {
        Pet pet = petDTOtoEntity(petDTO);

        // Get Customer by ownerId
        Customer customer = customerRepository.findCustomerById(petDTO.getOwnerId());

        // Save Pet
        pet.setUser(customer);
        Long petId = petRepository.save(pet).getId();

        pet.setId(petId);

        List<Pet> customerPets = Optional.ofNullable(customer.getPets()).orElse(Collections.emptyList());

        // Assign Pet to customer
        if(customerPets.size() > 0) {
            customer.getPets().add(pet);
        } else {
            List<Pet> pets = new ArrayList<>();
            pets.add(pet);
            customer.setPets(pets);
        }

        // Save Customer
        customerRepository.save(customer);

        petDTO.setId(petId);
        return petDTO;
    }

    /**
     * Find all existing Pets
     */
    public List<PetDTO> getPets() {
        List<Pet> pets = petRepository.findAll();
        List<PetDTO> petDTOS = new ArrayList<>();

        for (Pet pet: pets) {
            petDTOS.add(petEntityToDTO(pet));
        }

        return petDTOS;
    }

    /**
     * Find pet by petId
     */
    public PetDTO getPetById(Long petId) {
        Pet pet = petRepository.findPetById(petId);
        return petEntityToDTO(pet);
    }

    /**
     * Find pets by ownerId
     */
    public List<PetDTO> getPetsByOwner(Long ownerId) {
        Customer customer = customerRepository.findCustomerById(ownerId);
        List<PetDTO> petDTOS = new ArrayList<>();

        for (Pet pet: customer.getPets()) {
            petDTOS.add(petEntityToDTO(pet));
        }
        return petDTOS;
    }

    /**
     * Find all saved schedules for the pet
     */
    public List<ScheduleDTO> getScheduleForPet(long petId) {
        Pet pet = petRepository.findPetById(petId);
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: pet.getSchedules()) {
            scheduleDTOS.add(scheduleService.scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
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
         * User and assign them to Pet DTO
         */
        petDTO.setOwnerId(pet.getUser().getId());
        return petDTO;
    }
}
