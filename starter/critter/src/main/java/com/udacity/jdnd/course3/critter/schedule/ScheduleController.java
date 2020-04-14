package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.entity.User;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CustomerService customerService;

    @Autowired
    PetService petService;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleDTOtoEntity(scheduleDTO);
        long scheduleId = scheduleService.createSchedule(schedule, scheduleDTO.getPetIds(), scheduleDTO.getEmployeeIds());
        scheduleDTO.setId(scheduleId);
        return scheduleDTO;
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: scheduleService.getAllSchedules()) {
            scheduleDTOS.add(scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: petService.getScheduleForPet(petId)) {
            scheduleDTOS.add(scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: employeeService.getScheduleForEmployee(employeeId)) {
            scheduleDTOS.add(scheduleEntityToDTO(schedule));
        }
        return scheduleDTOS;
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule: customerService.getScheduleForCustomer(customerId)) {
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
