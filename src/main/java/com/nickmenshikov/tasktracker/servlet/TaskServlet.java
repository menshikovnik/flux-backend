package com.nickmenshikov.tasktracker.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickmenshikov.tasktracker.dto.CreateTaskRequest;
import com.nickmenshikov.tasktracker.exception.BadRequestException;
import com.nickmenshikov.tasktracker.exception.UnauthorizedException;
import com.nickmenshikov.tasktracker.model.Task;
import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.service.TaskService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {

    private TaskService taskService;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        taskService = (TaskService) config.getServletContext().getAttribute("taskService");
        mapper = (ObjectMapper) config.getServletContext().getAttribute("jacksonMapper");
        if (taskService == null) {
            throw new ServletException("TaskService not found in ServletContext");
        }
        if (mapper == null) {
            throw new ServletException("ObjectMapper not found in ServletContext");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        switch (path) {
            case "/create" -> handleCreate(req, resp);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        switch (path) {
            case "/getAll" -> handleGetAll(req, resp);
            case "/get" -> handleGetById(req, resp);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleGetById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        User user = Optional.ofNullable(req.getSession(false)).map(
                session -> (User) session.getAttribute("user")
        ).orElseThrow(
                () -> new UnauthorizedException("Authentication is required")
        );

        resp.setContentType("application/json");

        if (idParam == null) {
            throw new BadRequestException("Missing id parameter");
        }

        Long id = parseTaskId(idParam);
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), taskService.getTaskById(id, user.getId()));
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateTaskRequest createTaskRequest = mapper.readValue(req.getReader(), CreateTaskRequest.class);
        resp.setContentType("application/json");

        User user = Optional.ofNullable(req.getSession(false)).map(
                session -> (User) session.getAttribute("user")
        ).orElseThrow(
                () -> new UnauthorizedException("Authentication is required")
        );

        if (createTaskRequest.title() == null || createTaskRequest.title().isBlank()) {
            throw new BadRequestException("Title must not be empty");
        }

        if (createTaskRequest.priority() == null || createTaskRequest.status() == null) {
            throw new BadRequestException("Priority and status must not be empty");
        }

        taskService.createTask(
                createTaskRequest.title(),
                createTaskRequest.description(),
                createTaskRequest.status().name(),
                createTaskRequest.priority().name(),
                user.getId()
        );

        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), Map.of("status", "success"));
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = Optional.ofNullable(req.getSession(false)).map(
                session -> (User) session.getAttribute("user")
        ).orElseThrow(
                () -> new UnauthorizedException("Authentication is required")
        );

        resp.setContentType("application/json");

        List<Task> tasks = taskService.getAllTasks(user.getId().toString());
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), tasks);
    }

    private Long parseTaskId(String idParam) {
        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Task id must be a number");
        }
    }
}
