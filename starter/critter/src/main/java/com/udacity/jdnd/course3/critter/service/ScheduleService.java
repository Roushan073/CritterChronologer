package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.entity.User;
import com.udacity.jdnd.course3.critter.repository.*;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
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
     * Given a Schedule DTO, create the schedule and return the created schedule details
     */
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        List<User> employees = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();

        // Find all pets from petId
        for(Long petId: scheduleDTO.getPetIds()) {
            pets.add(petRepository.findPetById(petId));
        }

        // Find all employees from employeeId
        for(Long employeeId: scheduleDTO.getEmployeeIds()) {
            employees.add(employeeRepository.findEmployeeById(employeeId));
        }

        // Set pet and employee to schedule
        Schedule schedule = scheduleDTOtoEntity(scheduleDTO);
        schedule.setPets(pets);
        schedule.setUsers(employees);

        Long scheduleId = scheduleRepository.save(schedule).getId();
        schedule.setId(scheduleId);
        scheduleDTO.setId(scheduleId);

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

        return scheduleDTO;
    }

    /**
     * Find all existing Schedules
     */
    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: scheduleRepository.findAll()) {
            scheduleDTOS.add(scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
    }

    /**
     * Copy Schedule DTO matching properties to Schedule Entity
     */
    public Schedule scheduleDTOtoEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        return schedule;
    }

    /**
     * Copy Schedule Entity matching properties to Schedule DTO
     */
    public ScheduleDTO scheduleEntityToDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        List<Long> petIds = new ArrayList<>();
        List<Long> empIds = new ArrayList<>();

        /**
         * As Schedule DTO contains only Ids of Pet and User, we need to extract them from
         * User and Pet, and assign them to Schedule DTO
         */
        for(Pet pet: schedule.getPets()) {
            petIds.add(pet.getId());
        }

        for(User employee: schedule.getUsers()) {
            empIds.add(employee.getId());
        }

        scheduleDTO.setPetIds(petIds);
        scheduleDTO.setEmployeeIds(empIds);

        return scheduleDTO;
    }
}
