package ru.mai.khasanov.cryptowebmessenger.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mai.khasanov.cryptowebmessenger.DTO.LoginRequest;
import ru.mai.khasanov.cryptowebmessenger.Services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        String result = userService.authenticateUser(username, password);
        
        return switch (result) {
            case "Created" -> ResponseEntity.status(HttpStatus.CREATED).body("{'message': 'User created and logged in', 'username': '" + username + "'}");
            case "Logged" -> ResponseEntity.ok().body("{'message': 'Logged in successfully', 'username': '" + username + "'}");
            case "Incorrect" -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{'error': 'Incorrect password'}");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'error': 'Internal server error'}");
        };
    }
}
