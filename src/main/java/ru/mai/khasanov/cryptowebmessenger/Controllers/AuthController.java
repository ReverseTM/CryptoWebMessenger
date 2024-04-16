package ru.mai.khasanov.cryptowebmessenger.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mai.khasanov.cryptowebmessenger.DTO.LoginRequest;
import ru.mai.khasanov.cryptowebmessenger.Services.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String result = authService.authenticateUser(loginRequest);
        long userId;

        Map<String, Object> response = new HashMap<>();

        if (result.startsWith("Logged")) {
            userId = Long.parseLong(result.substring(7));

            response.put("message", "User created and logged in");
            response.put("username", username);
            response.put("userId", userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result.startsWith("Created")) {
            userId = Long.parseLong(result.substring(8));

            response.put("message", "Logged in successfully");
            response.put("username", username);
            response.put("userId", userId);

            return ResponseEntity.ok().body(response);
        } else if (result.startsWith("Incorrect")) {
            response.put("error", "Incorrect password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            response.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
