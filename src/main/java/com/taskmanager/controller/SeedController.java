package com.taskmanager.controller;

import com.taskmanager.dto.response.success.SuccessResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.entity.UserConfig;
import com.taskmanager.entity.UserPassword;
import com.taskmanager.entity.enums.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserPasswordRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SeedController {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/seed")
    @Transactional
    public ResponseEntity<SuccessResponse> seed() {
        // Create user "test" if not exists
        User user = userRepository.findByUsername("test").orElseGet(() -> {
            User newUser = User.builder()
                    .username("test")
                    .name("Test User")
                    .email("test@taskmanager.com")
                    .userConfig(new UserConfig())
                    .build();
            newUser = userRepository.save(newUser);

            UserPassword userPassword = UserPassword.builder()
                    .userId(newUser.getId())
                    .encryptedPassword(passwordEncoder.encode("password"))
                    .build();
            userPasswordRepository.save(userPassword);

            return newUser;
        });

        // Generate 100 tasks
        String[] subjects = {"API", "Database", "UI", "Auth", "Logging", "Cache", "Search", "Config", "Deploy", "Test"};
        String[] actions = {"Fix", "Implement", "Refactor", "Review", "Update", "Optimize", "Document", "Debug", "Design", "Migrate"};
        TaskStatus[] statuses = {TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.DONE};

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String title = actions[i % actions.length] + " " + subjects[i / actions.length] + " #" + (i + 1);
            TaskStatus status = statuses[i % 3];
            LocalDate dueDate = LocalDate.now().plusDays(i - 30);

            Task task = Task.builder()
                    .userId(user.getId())
                    .title(title)
                    .description("Task description for: " + title)
                    .status(status)
                    .dueDate(dueDate)
                    .reminder(i % 4 == 0)
                    .build();
            tasks.add(task);
        }
        taskRepository.saveAll(tasks);

        return ResponseEntity.ok(new SuccessResponse()
                .setMessage("Seeded 100 tasks for user 'test'"));
    }
}
