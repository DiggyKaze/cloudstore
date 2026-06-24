package com.caniz.controllers;

import com.caniz.dto.LoginDto;
import com.caniz.dto.RegisterDto;
import com.caniz.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// Restcontroller för att få swagger att fungera med redirects
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;
    public AuthRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {

        String token = userService.login(
                dto.getUsername(),
                dto.getPassword());

        if (token == null) {
            return ResponseEntity.status(401)
                    .body("Fel användarnamn eller lösenord");
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {

        try {
            userService.register(
                    dto.getUsername(),
                    dto.getEmail(),
                    dto.getPassword()
            );

            return ResponseEntity.ok("User created successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
