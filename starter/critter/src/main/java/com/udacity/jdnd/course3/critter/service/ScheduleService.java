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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        List<User> employees = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();

        // Find all pets from petId
        for(Long petId: scheduleDTO.getPetIds()) {
            pets.add(petRepository.findPetById(petId));
        }

        // Find All employee from employeeId
        for(Long employeeId: scheduleDTO.getEmployeeIds()) {
            employees.add(employeeRepository.findEmployeeById(employeeId));
        }

        // Set pet and employee to schedule
        Schedule schedule = scheduleDTOtoEntity(scheduleDTO);
        schedule.setPets(pets);
        schedule.setUsers(employees);

        Long scheduleId = scheduleRepository.save(schedule).getId();
        // This is required otherwise while setting this schedule to pet/employee it will have no Id (0)
        // and throw exception
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

        // Updating schedule to Employees
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

    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: scheduleRepository.findAll()) {
            scheduleDTOS.add(scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
    }

    public Schedule scheduleDTOtoEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        return schedule;
    }

    public ScheduleDTO scheduleEntityToDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        List<Long> petIds = new ArrayList<>();
        List<Long> empIds = new ArrayList<>();

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
