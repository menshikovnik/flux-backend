package com.nickmenshikov.flux.auth.service;

import com.nickmenshikov.flux.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FluxUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(login)
                .or(() -> userRepository.findUserByEmail(login))
                .map(
                user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPasswordHash())
                        .authorities("ROLE_USER")
                        .build()
        ).orElseThrow(
                () -> new UsernameNotFoundException("User not found: " + login)
        );
    }
}
