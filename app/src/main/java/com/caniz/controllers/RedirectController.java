package com.caniz.controllers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile({"dev", "stage"})
public class RedirectController {

    @GetMapping("/swagger")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html";
    }
}
