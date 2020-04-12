package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    PetService petService;

    // Saving a customer
    @Transactional
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerDTOtoEntity(customerDTO);
        Long customerId = customerRepository.save(customer).getId();
        customerDTO.setId(customerId);
        List<Long> petIds = new ArrayList<>();
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    // Get all customers
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> allCustomersDTO = new ArrayList<>();

        for(Customer customer: customerRepository.findAll()) {
            List<Long> petIds = new ArrayList<>();

            // To handle NullPointerException while running test suite
            List<Pet> customerPets = Optional.ofNullable(customer.getPets()).orElse(Collections.emptyList());
            for(Pet pet: customerPets) {
                petIds.add(pet.getId());
            }

            CustomerDTO customerDTO = customerEntityToDTO(customer);
            customerDTO.setPetIds(petIds);
            allCustomersDTO.add(customerDTO);
        }
        return allCustomersDTO;
    }

    // Get customer by id
    public CustomerDTO getCustomerById(Long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId);
        List<Long> petIds = new ArrayList<>();
        for(Pet pet: customer.getPets()) {
            petIds.add(pet.getId());
        }
        CustomerDTO customerDTO = customerEntityToDTO(customer);
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    public CustomerDTO getOwnerByPet(long petId) {
        PetDTO petDTO = petService.getPetById(petId);
        return getCustomerById(petDTO.getOwnerId());

    }

    public Customer customerDTOtoEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    public CustomerDTO customerEntityToDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

}
