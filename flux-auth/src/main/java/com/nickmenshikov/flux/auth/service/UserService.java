package com.nickmenshikov.flux.auth.service;

import com.nickmenshikov.flux.auth.repository.UserRepository;
import com.nickmenshikov.flux.core.exception.BadRequestException;
import com.nickmenshikov.flux.core.exception.UsernameAlreadyTakenException;
import com.nickmenshikov.flux.core.model.User;
import com.nickmenshikov.flux.core.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public User register(String username, String email, String password, String confirmPassword) {
        userRepository.findUserByUsername(username).ifPresent(
                _ -> {
                    throw new UsernameAlreadyTakenException("Username is already taken: " + username);
                }
        );

        userRepository.findUserByEmail(email).ifPresent(
                _ -> {
                    throw new UsernameAlreadyTakenException("Email is already taken: " + username);
                }
        );

        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords don't match");
        }

        User newUser = new User();

        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setCreatedAt(Instant.now());

        return userRepository.save(newUser);
    }
}
