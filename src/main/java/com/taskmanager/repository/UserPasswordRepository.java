package com.taskmanager.repository;

import com.taskmanager.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordRepository extends JpaRepository<UserPassword, String> {
}
