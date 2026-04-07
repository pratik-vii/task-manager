package com.taskmanager.entity;

import com.taskmanager.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_task_user_id", columnList = "user_id"),
        @Index(name = "idx_task_user_status", columnList = "user_id, status"),
        @Index(name = "idx_task_user_title", columnList = "user_id, title")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    @Id
    private String id;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Column(length = 500)
    private String description;

    private LocalDate dueDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean reminder = false;
}
