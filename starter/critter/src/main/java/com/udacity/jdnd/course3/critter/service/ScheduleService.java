package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.entity.User;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        List<User> users = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();

        // Find all pets from petId
        for(Long petId: scheduleDTO.getPetIds()) {
            pets.add(petRepository.findPetById(petId));
        }

        // Find All employee from employeeId
        for(Long employeeId: scheduleDTO.getEmployeeIds()) {
            users.add(employeeRepository.findEmployeeById(employeeId));
        }

        // Set pet and employee to schedule
        Schedule schedule = scheduleDTOtoEntity(scheduleDTO);
        schedule.setPets(pets);
        schedule.setUsers(users);

        Long scheduleId = scheduleRepository.save(schedule).getId();
        scheduleDTO.setId(scheduleId);

        return scheduleDTO;
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: scheduleRepository.findAll()) {
            ScheduleDTO scheduleDTO = scheduleEntityToDTO(schedule);
            List<Long> petIds = new ArrayList<>();
            List<Long> empIds = new ArrayList<>();

            for(Pet pet: schedule.getPets()) {
                petIds.add(pet.getId());
            }

            for(User user: schedule.getUsers()) {
                empIds.add(user.getId());
            }

            scheduleDTO.setPetIds(petIds);
            scheduleDTO.setEmployeeIds(empIds);
            scheduleDTOS.add(scheduleDTO);
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
        return scheduleDTO;
    }
}
