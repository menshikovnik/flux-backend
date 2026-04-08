package com.nickmenshikov.tasktracker.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickmenshikov.tasktracker.exception.BadRequestException;
import com.nickmenshikov.tasktracker.dto.LoginRequest;
import com.nickmenshikov.tasktracker.dto.RegistrationRequest;
import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private UserService userService;
    private ObjectMapper mapper;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
        mapper = (ObjectMapper) getServletContext().getAttribute("jacksonMapper");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        switch (path) {
            case "/login" -> handleLogin(req, resp);
            case "/register" -> handleRegister(req, resp);
            case "/logout" -> {
                req.getSession().invalidate();
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            case null, default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginRequest loginRequest = mapper.readValue(request.getReader(), LoginRequest.class);

        response.setContentType("application/json");

        if (loginRequest.username() == null || loginRequest.username().isBlank()
                || loginRequest.password() == null || loginRequest.password().isBlank()) {
            throw new BadRequestException("Username and password must not be empty");
        }

        User user = userService.login(loginRequest.username(), loginRequest.password());

        response.setStatus(HttpServletResponse.SC_OK);

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        Map<String, String> successResponse = Map.of(
                "status", "success",
                "message", "User has been logged in: " + loginRequest.username()
        );

        mapper.writeValue(response.getWriter(), successResponse);
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {

        RegistrationRequest registrationRequest = mapper.readValue(request.getReader(), RegistrationRequest.class);

        response.setContentType("application/json");

        if (registrationRequest.password() == null || registrationRequest.password().isBlank()
                || registrationRequest.username() == null || registrationRequest.username().isBlank()) {
            throw new BadRequestException("Password or username is null or blank");
        }

        if (!registrationRequest.password().equals(registrationRequest.confirmPassword())) {
            throw new BadRequestException("Passwords don't match");
        }

        userService.register(registrationRequest.username(), registrationRequest.password());
        response.setStatus(HttpServletResponse.SC_CREATED);
        var successResponse = Map.of(
                "success", "User has been registered"
        );
        mapper.writeValue(response.getWriter(), successResponse);
    }
}
