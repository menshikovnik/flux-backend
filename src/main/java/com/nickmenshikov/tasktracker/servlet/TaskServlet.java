package com.nickmenshikov.tasktracker.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickmenshikov.tasktracker.dto.CreateTaskRequest;
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
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        switch (path) {
            case "/getAll" -> handleGetAll(req, resp);
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateTaskRequest createTaskRequest = mapper.readValue(req.getReader(), CreateTaskRequest.class);
        resp.setContentType("application/json");

        if (createTaskRequest.title() == null || createTaskRequest.title().isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of("error", "Title must not be empty"));
            return;
        }

        if (createTaskRequest.priority() == null || createTaskRequest.status() == null || createTaskRequest.creatorId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of("error", "Priority, status and creatorId must not be empty"));
            return;
        }

        taskService.createTask(
                createTaskRequest.title(),
                createTaskRequest.description(),
                createTaskRequest.status().name(),
                createTaskRequest.priority().name(),
                createTaskRequest.creatorId().toString()
        );

        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), Map.of("status", "success"));
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = Optional.ofNullable(req.getSession(false)).map(
                session -> (User) session.getAttribute("user")
        ).orElseThrow(
                () -> new RuntimeException("Session or user not found")
        );

        resp.setContentType("application/json");

        try {
            List<Task> tasks = taskService.getAllTasks(user.getId().toString());
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), tasks);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorData = Map.of(
                    "error", "Something went wrong",
                    "details", e.getMessage() != null ? e.getMessage() : "Unknown error"
            );
            mapper.writeValue(resp.getWriter(), errorData);
            resp.getWriter().flush();
        }
    }
}
