package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
