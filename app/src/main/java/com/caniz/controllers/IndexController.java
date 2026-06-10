package com.caniz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        final List<String> items = new ArrayList<>(List.of("Item 1", "Item 2", "Item 3"));
        model.addAttribute("title", "Home Page");
        model.addAttribute("message", "Welcome to Spring Boot with Thymeleaf");
        model.addAttribute("user", "Guest");
        model.addAttribute("items", items);
        return "index";
    }
}
