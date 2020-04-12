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
public class PetService {

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    CustomerRepository customerRepository;

    // PetDTO contains ownerId in the RequestBody
    // Customer with ownerId must exists
    @Transactional
    public PetDTO savePet(PetDTO petDTO) {
        // Extract Pet from PetDTO
        Pet pet = petDTOtoEntity(petDTO);

        // Get Customer by ownerId
        // Getting customer this way causing custmer.plants to empty (if initialised) as empty arratlist
        // or NullPointerException if not initialised empty
        // Customer customer = customerService.customerDTOtoEntity(customerService.getCustomerById(petDTO.getOwnerId()));
        Customer customer = customerRepository.findCustomerById(petDTO.getOwnerId());

        // Save Pet
        pet.setUser(customer);
        Long petId = petRepository.save(pet).getId(); // reset petId to NULL if not NULL

        /*
        `javax.persistence.EntityNotFoundException: Unable to find com.udacity.jdnd.course3.critter.entity.Pet with id 0`
            Getting this exception as the Pet we added in Customer does not had id
        */
        // Assign Pet the petId to avoid above exception
        pet.setId(petId);

        // List<Plant> plants = Optional.ofNullable(delivery.getPlants()).orElse(Collections.emptyList());
        List<Pet> customerPets = Optional.ofNullable(customer.getPets()).orElse(Collections.emptyList());
        // Set Pet to customer

        if(customerPets.size() > 0) {
            customer.getPets().add(pet);
        } else {
            List<Pet> pets = new ArrayList<>();
            pets.add(pet);
            customer.setPets(pets);
        }

        // Save Customer -> Update
        customerRepository.save(customer);

        petDTO.setId(petId);
        return petDTO;
    }

    // Get all pets
    public List<PetDTO> getPets() {
        List<Pet> pets = petRepository.findAll();
        List<PetDTO> petDTOS = new ArrayList<>();

        for (Pet pet: pets) {
            PetDTO petDTO = petEntityToDTO(pet);
            petDTO.setOwnerId(pet.getUser().getId());
            petDTOS.add(petDTO);
        }

        return petDTOS;
    }

    // Get pet by petId
    public PetDTO getPetById(Long petId) {
        Pet pet = petRepository.findPetById(petId);
        PetDTO petDTO = petEntityToDTO(pet);
        petDTO.setOwnerId(pet.getUser().getId());
        return petDTO;
    }

    // get pets for an owner by ownerId
    public List<PetDTO> getPetsByOwner(Long ownerId) {
        Customer customer = customerRepository.findCustomerById(ownerId);
        List<PetDTO> petDTOS = new ArrayList<>();

        for (Pet pet: customer.getPets()) {
            PetDTO petDTO = petEntityToDTO(pet);
            petDTO.setOwnerId(pet.getUser().getId());
            petDTOS.add(petDTO);
        }
        return petDTOS;
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        Pet pet = petRepository.findPetById(petId);
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: pet.getSchedules()) {
            scheduleDTOS.add(scheduleService.scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
    }

    public Pet petDTOtoEntity(PetDTO petDTO) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet);
        return pet;
    }

    public PetDTO petEntityToDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(pet, petDTO);
        return petDTO;
    }
}
