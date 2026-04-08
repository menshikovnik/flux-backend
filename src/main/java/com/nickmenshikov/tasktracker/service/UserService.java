package com.nickmenshikov.tasktracker.service;

import com.nickmenshikov.tasktracker.dao.UserDao;
import com.nickmenshikov.tasktracker.exception.UserNotFoundException;
import com.nickmenshikov.tasktracker.exception.UsernameAlreadyTakenException;
import com.nickmenshikov.tasktracker.exception.InvalidPasswordException;
import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.util.PasswordUtil;
import java.time.Instant;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(String username, String password) {

        userDao.findByUsername(username).ifPresent(
                user -> {
                    throw new UsernameAlreadyTakenException("Username is already taken: " + username);
                }
        );

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setCreatedAt(Instant.now());

        userDao.save(newUser);
    }

    public User login(String username, String password) {

        User user = userDao.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found " + username)
        );

        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        return user;
    }
}
