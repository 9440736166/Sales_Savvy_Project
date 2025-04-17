package com.example.demo.Controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entitys.User;
import com.example.demo.Services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
	
	UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<?> Validate(@RequestBody User user) {

		try {
		User users = userService.ValadiateUser(user);
		return ResponseEntity.ok(Map.of("message","User Registration Successfully","user",users));
		} catch (Exception e) {
			
			return ResponseEntity.badRequest().body(Map.of("ERROR", e.getMessage()));
		}
	}
}
