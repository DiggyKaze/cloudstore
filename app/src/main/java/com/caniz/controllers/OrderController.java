package com.caniz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class OrderController {
    @GetMapping("/orders")
    public String orders(Model model){
        model.addAttribute("orders", List.of());
        return "orders";
    }

}
