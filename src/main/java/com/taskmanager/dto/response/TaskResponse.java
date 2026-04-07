package com.taskmanager.dto.response;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private boolean reminder;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .reminder(task.isReminder())
                .build();
    }
}
