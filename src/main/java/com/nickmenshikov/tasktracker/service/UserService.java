package com.nickmenshikov.tasktracker.service;

import com.nickmenshikov.tasktracker.dao.UserDao;
import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.util.PasswordUtil;

import java.time.Instant;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(String username, String password) {

        User user = userDao.findByUsername(username);

        if (user.getUsername() != null) {
            throw new RuntimeException("Username is already taken");
        } else {
            user = new User();
        }

        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setCreatedAt(Instant.now());

        userDao.save(user);
    }
}
