package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
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
    CustomerRepository customerRepository;

    /**
     * Given a Pet, create the Pet and return the created Pet details
     */
    public long savePet(Pet pet, long ownerId) {

        // Get Customer by ownerId
        Customer customer = customerRepository.findCustomerById(ownerId);
        if(customer == null) {
            return 0;
        }

        // Save Pet
        pet.setCustomer(customer);
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

        return petId;
    }

    /**
     * Find all existing Pets
     */
    public List<Pet> getPets() {
        return petRepository.findAll();
    }

    /**
     * Find pet by petId
     */
    public Pet getPetById(Long petId) {
        return petRepository.findPetById(petId);
    }

    /**
     * Find pets by ownerId
     */
    public List<Pet> getPetsByOwner(Long ownerId) {
        Customer customer = customerRepository.findCustomerById(ownerId);

        return customer.getPets();
    }

    /**
     * Find all saved schedules for the pet
     */
    public List<Schedule> getScheduleForPet(long petId) {
        Pet pet = petRepository.findPetById(petId);
        if(pet == null) {
            return Collections.emptyList();
        }
        return pet.getSchedules();
    }
}
