package com.caniz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("title", "Home Page");
        model.addAttribute("message", "Welcome to CloudStore");


        return "home";
    }
}
