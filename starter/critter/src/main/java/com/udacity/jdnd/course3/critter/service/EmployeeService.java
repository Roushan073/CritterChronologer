package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
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
@Transactional
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ScheduleService scheduleService;

    /**
     * Given a Employee DTO, create the Employee and return the Employee details
     */
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeDTOtoEntity(employeeDTO);
        Long employeeId = employeeRepository.save(employee).getId();
        employeeDTO.setId(employeeId);
        return employeeDTO;
    }

    /**
     * Find all existing Employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Find employee by employeeId
     */
    public EmployeeDTO getEmployeeById(Long employeeId) {
        return employeeEntityToDTO(employeeRepository.findEmployeeById(employeeId));
    }

    /**
     * Updating employee availability
     */
    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        employee.setDaysAvailable(daysAvailable);
        employeeRepository.save(employee);
    }

    /**
     * Find employees suited for certain services and available for certain days
     */
    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeReqDTO) {
        DayOfWeek dayOfWeek = employeeReqDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> employeeSkills = employeeReqDTO.getSkills();

        List<EmployeeDTO> employeeDTOS = new ArrayList<>();

        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee employee: allEmployees) {
            Set<EmployeeSkill> intersectionSkills = new HashSet<>();
            intersectionSkills.addAll(employeeSkills);
            intersectionSkills.retainAll(employee.getSkills());

            // Check if employee is available on given days and posses certain skills
            if(employee.getDaysAvailable().contains(dayOfWeek) && (intersectionSkills.size() == employeeSkills.size())) {
                employeeDTOS.add(employeeEntityToDTO(employee));
            }
        }

        return employeeDTOS;
    }

    /**
     * Find all saved schedules for the employee
     */
    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: employee.getSchedules()) {
            scheduleDTOS.add(scheduleService.scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
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
