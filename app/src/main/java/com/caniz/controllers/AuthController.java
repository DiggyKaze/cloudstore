package com.caniz.controllers;

import com.caniz.dto.RegisterDto;
import com.caniz.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDto dto, Model model) {
        try {
            userService.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
            return "redirect:/";  // → tillbaka till hemsidan efter registrering
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response, Model model) {

        String token = userService.login(username, password);

        if (token == null) {
            model.addAttribute("error", "Fel användarnamn eller lösenord");
            return "login";
        }

        // Spara JWT i en HTTP-only cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 timme
        response.addCookie(cookie);

        return "redirect:/products";
    }
}