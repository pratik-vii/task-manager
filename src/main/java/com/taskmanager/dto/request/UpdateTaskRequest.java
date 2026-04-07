package com.taskmanager.dto.request;

import com.taskmanager.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {

    private String title;

    @Size(max = 500)
    private String description;

    private TaskStatus status;

    private LocalDate dueDate;

    private Boolean reminder;
}
