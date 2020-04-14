package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.entity.User;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    /**
     * Given a Schedule, create the schedule and return the created schedule details
     */
    public long createSchedule(Schedule schedule, List<Long> petIds, List<Long> employeeIds) {
        List<User> employees = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();

        // Find all pets from petId
        for(Long petId: petIds) {
            pets.add(petRepository.findPetById(petId));
        }

        // Find all employees from employeeId
        for(Long employeeId: employeeIds) {
            employees.add(employeeRepository.findEmployeeById(employeeId));
        }

        // Set pet and employee to schedule
        schedule.setPets(pets);
        schedule.setUsers(employees);

        Long scheduleId = scheduleRepository.save(schedule).getId();
        schedule.setId(scheduleId);

        // Updating schedule to pets
        for(Pet pet: pets) {
            List<Schedule> petSchedules = Optional.ofNullable(pet.getSchedules()).orElse(Collections.emptyList());
            if(petSchedules.size() > 0) {
                pet.getSchedules().add(schedule);
            } else {
                List<Schedule> schedules = new ArrayList<>();
                schedules.add(schedule);
                pet.setSchedules(schedules);
            }
            petRepository.save(pet);
        }

        // Updating schedule to Employees (Users)
        for(User employee: employees) {
            List<Schedule> empSchedules = Optional.ofNullable(employee.getSchedules()).orElse(Collections.emptyList());
            if(empSchedules.size() > 0) {
                employee.getSchedules().add(schedule);
            } else {
                List<Schedule> schedules = new ArrayList<>();
                schedules.add(schedule);
                employee.setSchedules(schedules);
            }
            userRepository.save(employee);
        }

        return scheduleId;
    }

    /**
     * Find all existing Schedules
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
}
