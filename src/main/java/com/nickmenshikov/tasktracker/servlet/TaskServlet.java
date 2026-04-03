package com.nickmenshikov.tasktracker.servlet;

import com.nickmenshikov.tasktracker.service.TaskService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {

    private TaskService taskService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        taskService = (TaskService) config.getServletContext().getAttribute("taskService");
        if (taskService == null) {
            throw new ServletException("TaskService not found in ServletContext");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        switch (path) {
            case "/create" -> handleCreate(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        switch (path) {
            case "/getAll" -> handleGetAll(req, resp);
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String priority = req.getParameter("priority");
        String creatorId = req.getParameter("creatorId");
        String status = req.getParameter("status");

        if (title == null || title.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                resp.getWriter().write("Title must not be empty");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        taskService.createTask(title, description, status, priority, creatorId);
        try {
            resp.sendRedirect(req.getContextPath() + "/tasks");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetAll(HttpServletRequest req, HttpServletResponse resp) {
        String creatorId = req.getParameter("creatorId");

        try {
            taskService.getAllTasks(creatorId);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
