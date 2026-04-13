package com.nickmenshikov.flux.tasks.controller;

import com.nickmenshikov.flux.auth.repository.UserRepository;
import com.nickmenshikov.flux.core.dto.CreateTaskRequest;
import com.nickmenshikov.flux.core.dto.UpdateTaskRequest;
import com.nickmenshikov.flux.core.model.Priority;
import com.nickmenshikov.flux.core.model.Status;
import com.nickmenshikov.flux.core.model.Task;
import com.nickmenshikov.flux.core.exception.UserNotFoundException;
import com.nickmenshikov.flux.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    @PostMapping(version = "1.0")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateTaskRequest request, Authentication authentication) {
        Task task = taskService.createTask(request, getCurrentUserId(authentication));
        return ResponseEntity.created(URI.create("/api/tasks/" + task.getId())).build();
    }

    @GetMapping(version = "1.0")
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        Page<Task> tasks = taskService.getAllTasks(getCurrentUserId(authentication), pageable, status, priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Authentication authentication) {
        Task task = taskService.getTaskById(id, getCurrentUserId(authentication));
        return ResponseEntity.ok(task);
    }

    @PatchMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody UpdateTaskRequest request, Authentication authentication) {
        Task task = taskService.updateTask(id, getCurrentUserId(authentication), request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        taskService.deleteTask(id, getCurrentUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId(Authentication authentication) {
        return userRepository.findUserByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found " + authentication.getName()))
                .getId();
    }
}
