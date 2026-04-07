package com.taskmanager.controller;

import com.taskmanager.dto.request.CreateTaskRequest;
import com.taskmanager.dto.request.UpdateTaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.dto.response.success.SuccessResponse;
import com.taskmanager.entity.enums.TaskStatus;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@GetMapping("/tasks")
	public ResponseEntity<SuccessResponse<List<TaskResponse>>> getTasks(
			Authentication authentication,
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		String userId = (String) authentication.getPrincipal();
		return ResponseEntity.ok(
				SuccessResponse.fromPagedData(taskService.getTasks(userId, status, search, page, size)));
	}

	@GetMapping("/task/{taskId}")
	public ResponseEntity<SuccessResponse<TaskResponse>> getTask(
			Authentication authentication,
			@PathVariable String taskId) {
		String userId = (String) authentication.getPrincipal();
		return ResponseEntity.ok(new SuccessResponse<TaskResponse>()
				.setData(taskService.getTask(userId, taskId)));
	}

	@PostMapping("/task")
	public ResponseEntity<SuccessResponse<TaskResponse>> createTask(
			Authentication authentication,
			@Valid @RequestBody CreateTaskRequest request) {
		String userId = (String) authentication.getPrincipal();
		return ResponseEntity.ok(new SuccessResponse<TaskResponse>()
				.setData(taskService.createTask(userId, request)));
	}

	@PutMapping("/task/{taskId}")
	public ResponseEntity<SuccessResponse<TaskResponse>> updateTask(
			Authentication authentication,
			@PathVariable String taskId,
			@Valid @RequestBody UpdateTaskRequest request) {
		String userId = (String) authentication.getPrincipal();
		return ResponseEntity.ok(new SuccessResponse<TaskResponse>()
				.setData(taskService.updateTask(userId, taskId, request)));
	}
}
