package ru.mai.khasanov.cryptowebmessenger.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mai.khasanov.cryptowebmessenger.DTO.LoginRequest;
import ru.mai.khasanov.cryptowebmessenger.Models.User;

import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Optional<User> optionalUser = userService.getUserByUsername(username);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (passwordEncoder.matches(password, existingUser.getPassword())) {
                return "Logged " + existingUser.getId();
            } else {
                return "Incorrect";
            }
        } else {
            User newUser = userService.createUser(username, passwordEncoder.encode(password));
            return "Created " + newUser.getId();
        }
    }
}

