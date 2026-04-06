package com.nickmenshikov.tasktracker.servlet;

import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        switch (path) {
            case "/login" -> handleLogin(req, resp);
            case "/register" -> handleRegister(req, resp);
            case "/logout" -> {
                req.getSession().invalidate();
                resp.sendRedirect(req.getContextPath() + "/api/auth/login");
            }
            case null, default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            try {
                response.getWriter().write("Username and password must not be empty");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        User user = userService.login(username, password);

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        try {
            response.sendRedirect(request.getContextPath() + "/tasks");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        if (!password.equals(confirm)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write("Пароли не совпадают");
            return;
        }

        userService.register(username, password);
    }
}
