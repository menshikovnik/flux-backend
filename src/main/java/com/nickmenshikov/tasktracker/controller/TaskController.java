package com.nickmenshikov.tasktracker.controller;

import com.nickmenshikov.tasktracker.dto.CreateTaskRequest;
import com.nickmenshikov.tasktracker.dto.UpdateTaskRequest;
import com.nickmenshikov.tasktracker.model.Priority;
import com.nickmenshikov.tasktracker.model.Status;
import com.nickmenshikov.tasktracker.model.Task;
import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping(version = "1.0")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateTaskRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Task task = taskService.createTask(request, user.getId());
        return ResponseEntity.created(URI.create("/api/tasks/" + task.getId())).build();
    }

    @GetMapping(version = "1.0")
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @PageableDefault(size = 20) Pageable pageable,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        Page<Task> tasks = taskService.getAllTasks(user.getId(), pageable, status, priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("user");

        Task task = taskService.getTaskById(id, user.getId());
        return ResponseEntity.ok(task);
    }

    @PatchMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody UpdateTaskRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Task task = taskService.updateTask(id, user.getId(), request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        taskService.deleteTask(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
