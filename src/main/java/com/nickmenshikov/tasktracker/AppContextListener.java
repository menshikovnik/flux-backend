package com.nickmenshikov.tasktracker;

import com.nickmenshikov.tasktracker.dao.TaskDao;
import com.nickmenshikov.tasktracker.dao.UserDao;
import com.nickmenshikov.tasktracker.service.TaskService;
import com.nickmenshikov.tasktracker.service.UserService;
import com.nickmenshikov.tasktracker.util.DataSourceFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.sql.DataSource;


@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        String url = servletContext.getInitParameter("jdbc.url");
        String user = servletContext.getInitParameter("jdbc.user");
        String password = servletContext.getInitParameter("jdbc.password");

        DataSource dataSource = DataSourceFactory.create(url, user, password);

        UserDao userDao = new UserDao(dataSource);
        UserService userService = new UserService(userDao);
        TaskDao taskDao = new TaskDao(dataSource);
        TaskService taskService = new TaskService(taskDao);

        servletContext.setAttribute("dataSource", dataSource);
        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("taskService", taskService);
    }
}
