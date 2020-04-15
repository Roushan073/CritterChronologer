package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    /**
     * Given a Employee, create the Employee and return the Employee details
     */
    public long saveEmployee(Employee employee) {
        return employeeRepository.save(employee).getId();
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
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findEmployeeById(employeeId);
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
    public List<Employee> findEmployeesForService(LocalDate date, Set<EmployeeSkill> skills) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Set<EmployeeSkill> employeeSkills = skills;

        List<Employee> employeeAvailable = new ArrayList<>();

        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee employee: allEmployees) {
            Set<EmployeeSkill> intersectionSkills = new HashSet<>();
            intersectionSkills.addAll(employeeSkills);
            intersectionSkills.retainAll(employee.getSkills());

            // Check if employee is available on given days and posses certain skills
            if(employee.getDaysAvailable().contains(dayOfWeek) && (intersectionSkills.size() == employeeSkills.size())) {
                employeeAvailable.add(employee);
            }
        }

        return employeeAvailable;
    }

    /**
     * Find employees available for certain days
     */
    public List<Employee> findEmployeesForServiceByDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        List<Employee> employeeAvailable = new ArrayList<>();

        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee employee: allEmployees) {
            // Check if employee is available on given days and posses certain skills
            if(employee.getDaysAvailable().contains(dayOfWeek)) {
                employeeAvailable.add(employee);
            }
        }

        return employeeAvailable;
    }

    /**
     * Find employees suited for certain skills
     */
    public List<Employee> findEmployeesForServiceBySkills(Set<EmployeeSkill> skills) {
        Set<EmployeeSkill> employeeSkills = skills;
        List<Employee> employeeAvailable = new ArrayList<>();

        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee employee: allEmployees) {
            Set<EmployeeSkill> intersectionSkills = new HashSet<>();
            intersectionSkills.addAll(employeeSkills);
            intersectionSkills.retainAll(employee.getSkills());

            // Check if employee is available on given days and posses certain skills
            if(intersectionSkills.size() == employeeSkills.size()) {
                employeeAvailable.add(employee);
            }
        }

        return employeeAvailable;
    }

    /**
     * Find all saved schedules for the employee
     */
    public List<Schedule> getScheduleForEmployee(long employeeId) {
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        if(employee == null) {
            return Collections.emptyList();
        }
        return employee.getSchedules();
    }
}
