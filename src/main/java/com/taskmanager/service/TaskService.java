package com.taskmanager.service;

import com.taskmanager.dto.request.CreateTaskRequest;
import com.taskmanager.dto.request.UpdateTaskRequest;
import com.taskmanager.dto.response.PagedData;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.enums.TaskStatus;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PagedData<TaskResponse> getTasks(String userId, TaskStatus status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Task> taskPage;

        if (status != null && search != null) {
            taskPage = taskRepository.findByUserIdAndStatusAndTitleContainingIgnoreCase(userId, status, search, pageable);
        } else if (status != null) {
            taskPage = taskRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (search != null) {
            taskPage = taskRepository.findByUserIdAndTitleContainingIgnoreCase(userId, search, pageable);
        } else {
            taskPage = taskRepository.findByUserId(userId, pageable);
        }

        PagedData<TaskResponse> pagedData = new PagedData<>();
        pagedData.setData(taskPage.getContent().stream().map(TaskResponse::from).toList());
        pagedData.setCurrentPage(taskPage.getNumber());
        pagedData.setPageSize(taskPage.getSize());
        pagedData.setMaxPages(taskPage.getTotalPages());
        pagedData.setTotalCount(taskPage.getTotalElements());
        return pagedData;
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(String userId, String taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(String userId, CreateTaskRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Task task = Task.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .reminder(request.isReminder())
                .build();

        task = taskRepository.save(task);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTask(String userId, String taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getReminder() != null) {
            task.setReminder(request.getReminder());
        }

        task = taskRepository.save(task);
        return TaskResponse.from(task);
    }
}
