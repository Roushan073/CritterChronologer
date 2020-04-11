package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeDTOtoEntity(employeeDTO);
        Long employeeId = employeeRepository.save(employee).getId();
        employeeDTO.setId(employeeId);
        return employeeDTO;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public EmployeeDTO getEmployeeById(Long employeeId) {
        return employeeEntityToDTO(employeeRepository.findEmployeeById(employeeId));
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        employee.setDaysAvailable(daysAvailable);
        employeeRepository.save(employee);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeReqDTO) {
        DayOfWeek dayOfWeek = employeeReqDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> employeeSkills = employeeReqDTO.getSkills();

        List<EmployeeDTO> employeeDTOS = new ArrayList<>();

        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee employee: allEmployees) {
            Set<EmployeeSkill> intersectionSkills = new HashSet<>();
            intersectionSkills.addAll(employeeSkills);
            intersectionSkills.retainAll(employee.getSkills());
            if(employee.getDaysAvailable().contains(dayOfWeek) && (intersectionSkills.size() == employeeSkills.size())) {
                employeeDTOS.add(employeeEntityToDTO(employee));
            }
        }

        return employeeDTOS;

    }

    private Employee employeeDTOtoEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    private EmployeeDTO employeeEntityToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }
}
