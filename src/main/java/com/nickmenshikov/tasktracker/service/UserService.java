package com.nickmenshikov.tasktracker.service;

import com.nickmenshikov.tasktracker.exception.BadRequestException;
import com.nickmenshikov.tasktracker.exception.UserNotFoundException;
import com.nickmenshikov.tasktracker.exception.UsernameAlreadyTakenException;
import com.nickmenshikov.tasktracker.exception.InvalidPasswordException;
import com.nickmenshikov.tasktracker.model.User;
import com.nickmenshikov.tasktracker.repository.UserRepository;
import com.nickmenshikov.tasktracker.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public User register(String username, String password, String confirmPassword) {

        userRepository.findUserByUsername(username).ifPresent(
                user -> {
                    throw new UsernameAlreadyTakenException("Username is already taken: " + username);
                }
        );

        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords don't match");
        }

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setCreatedAt(Instant.now());

        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public User login(String username, String password) {

        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found " + username)
        );

        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        return user;
    }
}
