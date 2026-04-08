package com.nickmenshikov.tasktracker.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickmenshikov.tasktracker.exception.AppException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {
    private ObjectMapper mapper;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext servletContext = filterConfig.getServletContext();
        mapper = (ObjectMapper) servletContext.getAttribute("jacksonMapper");
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (AppException e) {
            writeError(response, e.getStatusCode(), e.getErrorCode(), e.getMessage());
        } catch (JsonProcessingException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "invalid_json", "Invalid JSON request body");
        } catch (RuntimeException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "internal_error", "Something went wrong");
        }
    }

    private void writeError(ServletResponse response, int statusCode, String errorCode, String message)
            throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (httpResponse.isCommitted()) {
            return;
        }
        httpResponse.setStatus(statusCode);
        httpResponse.setContentType("application/json");
        mapper.writeValue(httpResponse.getWriter(), Map.of(
                "error", errorCode,
                "message", message
        ));
    }
}
