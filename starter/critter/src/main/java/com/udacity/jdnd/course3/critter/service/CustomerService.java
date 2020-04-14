package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
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
    PetService petService;

    /**
     * Given a Customer, create the Customer and return the Customer details
     */
    public long saveCustomer(Customer customer) {
        return customerRepository.save(customer).getId();
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
        return getCustomerById(pet.getUser().getId());
    }

    /**
     * Find all saved schedules for any pets belonging to the owner
     */
    public List<Schedule> getScheduleForCustomer(long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId);
        List<Pet> customerPets = Optional.ofNullable(customer.getPets()).orElse(Collections.emptyList());
        List<Schedule> schedules = new ArrayList<>();

        for(Pet pet: customerPets) {
            schedules.addAll(petService.getScheduleForPet(pet.getId()));
        }

        return schedules;
    }

}
