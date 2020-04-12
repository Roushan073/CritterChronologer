package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
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
@Transactional
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PetService petService;

    /**
     * Given a Customer DTO, create the Customer and return the Customer details
     */
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerDTOtoEntity(customerDTO);
        Long customerId = customerRepository.save(customer).getId();
        customerDTO.setId(customerId);
        List<Long> petIds = new ArrayList<>();
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    /**
     * Find all existing Customers
     */
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> allCustomersDTO = new ArrayList<>();

        for(Customer customer: customerRepository.findAll()) {
            List<Long> petIds = new ArrayList<>();

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

    /**
     * Find customer by customerId
     */
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

    /**
     * Find customer by petId
     */
    public CustomerDTO getOwnerByPet(long petId) {
        PetDTO petDTO = petService.getPetById(petId);
        return getCustomerById(petDTO.getOwnerId());

    }

    /**
     * Find all saved schedules for any pets belonging to the owner
     */
    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId);
        List<Pet> customerPets = Optional.ofNullable(customer.getPets()).orElse(Collections.emptyList());
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();

        for(Pet pet: customerPets) {
            scheduleDTOS.addAll(petService.getScheduleForPet(pet.getId()));
        }

        return scheduleDTOS;
    }

    /**
     * Copy Customer DTO matching properties to Customer Entity
     */
    public Customer customerDTOtoEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    /**
     * Copy Customer Entity matching properties to Customer DTO
     */
    public CustomerDTO customerEntityToDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

}
