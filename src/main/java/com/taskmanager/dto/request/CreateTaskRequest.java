package com.taskmanager.dto.request;

import com.taskmanager.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {

    @NotBlank
    private String title;

    @Size(max = 500)
    private String description;

    private TaskStatus status = TaskStatus.TODO;

    private LocalDate dueDate;

    private boolean reminder = false;
}
