package com.caniz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class AuthController{

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, Model model){

		if (username != null && password != null) {
			return "redirect:/";
		}
		model.addAttribute("error", "Invalid credentials");
		return "login";
	}

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model){


       if (username != null && password != null) {
            return "redirect:/";
        }

        model.addAttribute("error", "Invalid credentials");
        return "register";
    }



	@PostMapping("/refresh")
	public String refresh(){
		return "refresh succesful";
	}
}
