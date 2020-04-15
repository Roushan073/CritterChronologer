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
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    PetService petService;

    /**
     * Given a Customer, create the Customer and return the Customer details
     */
    public long saveCustomer(Customer customer) {
        return customerRepository.save(customer).getId();
    }

    /**
     * Given a Customer with petId, create the Customer and return the Customer details
     */
    public long saveCustomerWithPet(Customer customer, List<Long> petIds) {
        // These pet must not be associated with any customer
        List<Pet> pets = new ArrayList<>();
        for(long petId: petIds) {
            Pet pet = petService.getPetById(petId);
            if(pet == null) {
                return 0;
            }
            pets.add(pet);
        }

        customer.setPets(pets);
        //long customerId = customerRepository.save(customer).getId();

        for(Pet pet: pets) {
            pet.setCustomer(customer);
            petRepository.save(pet);
        }

        long customerId = petService.getPetById(petIds.get(0)).getCustomer().getId();

        return customerId;
    }

    /**
     * Find all existing Customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Find customer by customerId
     */
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findCustomerById(customerId);
    }

    /**
     * Find customer by petId
     */
    public Customer getOwnerByPet(long petId) {
        Pet pet = petService.getPetById(petId);
        return getCustomerById(pet.getCustomer().getId());
    }

    /**
     * Find all saved schedules for any pets belonging to the owner
     */
    public List<Schedule> getScheduleForCustomer(long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId);
        if(customer == null) {
            return Collections.emptyList();
        }
        List<Pet> customerPets = Optional.ofNullable(customer.getPets()).orElse(Collections.emptyList());
        List<Schedule> schedules = new ArrayList<>();

        for(Pet pet: customerPets) {
            schedules.addAll(petService.getScheduleForPet(pet.getId()));
        }

        return schedules;
    }

}
