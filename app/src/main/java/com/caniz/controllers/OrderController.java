package com.caniz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;

@Controller
public class OrderController {

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", List.of());
        return "orders";
    }

    @PostMapping("/orders/create")
    public String createOrder() {
        return "redirect:/order-confirmation";
    }

    @GetMapping("/order-confirmation")
    public String orderConfirmation() {
        return "order-confirmation";
    }
}