package com.nickmenshikov.tasktracker.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private ObjectMapper mapper;

    @Override
    public void init(FilterConfig filterConfig) {
        mapper = (ObjectMapper) filterConfig.getServletContext().getAttribute("jacksonMapper");
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);

        String path = request.getRequestURI().substring(request.getContextPath().length());

        boolean isLoginPath = path.equals("/api/auth/login");
        boolean isRegisterPath = path.equals("/api/auth/register");
        boolean isLogoutPath = path.equals("/api/auth/logout");

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isLoginPath || isRegisterPath || isLogoutPath || isLoggedIn) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            mapper.writeValue(response.getWriter(), Map.of(
                    "error", "unauthorized",
                    "message", "Authentication is required"
            ));
        }
    }
}
