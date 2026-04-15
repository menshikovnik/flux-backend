package com.nickmenshikov.flux.tasks.controller;

import com.nickmenshikov.flux.core.dto.CreateTaskRequest;
import com.nickmenshikov.flux.core.dto.TaskResponse;
import com.nickmenshikov.flux.core.dto.UpdateTaskRequest;
import com.nickmenshikov.flux.core.model.FluxUserDetails;
import com.nickmenshikov.flux.core.model.Priority;
import com.nickmenshikov.flux.core.model.Status;
import com.nickmenshikov.flux.core.model.Task;
import com.nickmenshikov.flux.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping(version = "1.0")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateTaskRequest request, @AuthenticationPrincipal FluxUserDetails userDetails) {
        Task task = taskService.createTask(request, userDetails.getUser());
        return ResponseEntity.created(URI.create("/api/tasks/" + task.getId())).build();
    }

    @GetMapping(version = "1.0")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long projectId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal FluxUserDetails userDetails) {
        return ResponseEntity.ok(taskService.getAllTasks(userDetails.getUser(), pageable, status, priority, projectId).map(TaskResponse::from));
    }

    @GetMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id, @AuthenticationPrincipal FluxUserDetails userDetails) {
        return ResponseEntity.ok(TaskResponse.from(taskService.getTaskById(id, userDetails.getUser())));
    }

    @PatchMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @RequestBody UpdateTaskRequest request, @AuthenticationPrincipal FluxUserDetails userDetails) {
        return ResponseEntity.ok(TaskResponse.from(taskService.updateTask(id, userDetails.getUser(), request)));
    }

    @DeleteMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal FluxUserDetails userDetails) {
        taskService.deleteTask(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
