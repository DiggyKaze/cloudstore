package com.caniz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class CartController {
    @GetMapping("/cart")
    public String cart(Model model){
        model.addAttribute("cartItems", List.of());
        return "cart";
    }
}
