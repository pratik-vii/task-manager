package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {

    Page<Task> findByUserId(String userId, Pageable pageable);

    Page<Task> findByUserIdAndStatus(String userId, TaskStatus status, Pageable pageable);

    Page<Task> findByUserIdAndTitleContainingIgnoreCase(String userId, String title, Pageable pageable);

    Page<Task> findByUserIdAndStatusAndTitleContainingIgnoreCase(String userId, TaskStatus status, String title, Pageable pageable);

    Optional<Task> findByIdAndUserId(String id, String userId);
}
