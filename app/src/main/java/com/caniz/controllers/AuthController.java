package com.caniz.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
public class AuthController{

	@PostMapping("/login")
	public String login(){
		return "login succesful";
	}

	@PostMapping("/refresh")
	public String refresh(){
		return "refresh succesful";
	}
}
