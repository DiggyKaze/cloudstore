package com.caniz.controllers;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class HomeController {

    private final Environment environment;

    public HomeController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("title", "Home Page");
        model.addAttribute("message", "Welcome to CloudStore");

        boolean prodProfile = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        model.addAttribute("showSwagger", !prodProfile);

        return "home";
    }
}
