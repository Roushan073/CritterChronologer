package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    CustomerService customerService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        Customer customer = customerDTOtoEntity(customerDTO);
        List<Long> petIds = Optional.ofNullable(customerDTO.getPetIds()).orElse(Collections.emptyList());

        long customerId;

        if(petIds.size() > 0) {
            customerId = customerService.saveCustomerWithPet(customer, petIds);
            if(customerId == 0) {
                return null;
            }
        } else {
            customerId = customerService.saveCustomer(customer);
        }

        customerDTO.setId(customerId);
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<CustomerDTO> allCustomersDTO = new ArrayList<>();
        for(Customer customer: customerService.getAllCustomers()) {
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

    @PostMapping("/customer/{customerId}")
    public CustomerDTO getCustomer(@PathVariable long customerId) {
        Customer customer = customerService.getCustomerById(customerId);

        List<Long> petIds = new ArrayList<>();
        for(Pet pet: customer.getPets()) {
            petIds.add(pet.getId());
        }
        CustomerDTO customerDTO = customerEntityToDTO(customer);
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        Customer customer = customerService.getOwnerByPet(petId);

        List<Long> petIds = new ArrayList<>();
        for(Pet pet: customer.getPets()) {
            petIds.add(pet.getId());
        }
        CustomerDTO customerDTO = customerEntityToDTO(customer);
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeDTOtoEntity(employeeDTO);
        long employeeId = employeeService.saveEmployee(employee);
        employeeDTO.setId(employeeId);
        return employeeDTO;
    }

    @GetMapping("/employee")
    public List<EmployeeDTO> getAllEmployees(){
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        for(Employee employee: employeeService.getAllEmployees()) {
            employeeDTOS.add(employeeEntityToDTO(employee));
        }
        return employeeDTOS;
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        return employeeEntityToDTO(employeeService.getEmployeeById(employeeId));
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        employeeService.setAvailability(daysAvailable, employeeId);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        LocalDate date = employeeDTO.getDate();
        Set<EmployeeSkill> skills = employeeDTO.getSkills();
        List<Employee> availableEmployees = new ArrayList<>();

        if(date != null && skills != null) {
            availableEmployees.addAll(employeeService.findEmployeesForService(date, skills));
        } else if(date != null) {
            availableEmployees.addAll(employeeService.findEmployeesForServiceByDate(date));
        } else {
            availableEmployees.addAll(employeeService.findEmployeesForServiceBySkills(skills));
        }

        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        for(Employee employee: availableEmployees) {
            employeeDTOS.add(employeeEntityToDTO(employee));
        }
        return employeeDTOS;
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

    /**
     * Copy Employee DTO matching properties to Employee Entity
     */
    private Employee employeeDTOtoEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    /**
     * Copy Employee Entity matching properties to Employee DTO
     */
    private EmployeeDTO employeeEntityToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

}
