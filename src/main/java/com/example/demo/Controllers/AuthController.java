package com.example.demo.Controllers;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entitys.User;
import com.example.demo.Services.AuthService;
import com.example.demo.dto.LoginRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth") 
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	// My code 
//	@PostMapping("/login")
//	public ResponseEntity<?> Login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
//		
//		try {
//		User user = authService.Login(loginRequest.getUsername(), loginRequest.getPassword());
//		
//		// generate a token
//		String token = authService.generateToken(user);
//		// add cookie
//		Cookie cookie = new Cookie("authToken", token);
//		cookie.setHttpOnly(true);
//		cookie.setSecure(false); // Set true if using  HTTPS
//		cookie.setPath("/");
//		cookie.setMaxAge(3600);//eapaire in 1 hour
//		cookie.setDomain("localhost");
//		response.addCookie(cookie);
//		response.addHeader("Set-Cookie", String.format("authToken=%s HttpOnly; path=/; Max-Age=3600 ;SameSite=None", token));
//		
//		Map<String, Object> responseBody = new HashMap<>();
//		responseBody.put("message", "Login is Successfull");
//		responseBody.put("username", user.getUsername());
//		responseBody.put("role", user.getRole().name());
//		
//		return ResponseEntity.ok(responseBody);
//		
//		}
//		catch(Exception e) {
//			
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Error",e.getMessage()));
//		}
		
		@PostMapping("/login")
		public ResponseEntity<?> Login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		    try {
		        User user = authService.Login(loginRequest.getUsername(), loginRequest.getPassword());
		        String token = authService.generateToken(user);

		        // Set the token in a cookie
		        Cookie cookie = new Cookie("authToken", token);
		        cookie.setHttpOnly(true);
		        cookie.setSecure(false); // Set to true if using HTTPS
		        cookie.setPath("/"); // Ensure the cookie is accessible across the entire domain
		        cookie.setMaxAge(3600); // Expires in 1 hour
		        cookie.setDomain("localhost"); // Set the domain explicitly
		        response.addCookie(cookie);

		        // Add the cookie to the response header (for cross-origin support)
		        response.addHeader("Set-Cookie", String.format("authToken=%s; HttpOnly; Path=/; Max-Age=3600; SameSite=None; Domain=localhost Secure", token));
		                          
		        // Create the response body
		        Map<String, Object> responseBody = new HashMap<>();
		        responseBody.put("message", "Login is Successful");
		        responseBody.put("username", user.getUsername());
		        responseBody.put("role", user.getRole().name());

		        return ResponseEntity.ok(responseBody);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Error", e.getMessage()));
		    }
		}
		
		@PostMapping("/logout")
		public ResponseEntity<Map<String,String>> logout(HttpServletRequest request, HttpServletResponse response){
			
			try {
				User user = (User)request.getAttribute("authenticatedUser");
				authService.logout(user);
				Cookie cookie = new Cookie("authToken",null);
				cookie.setHttpOnly(true);
				cookie.setMaxAge(3600);
				cookie.setPath("/");
				 response.addCookie(cookie);
				
				Map<String, String> responseBody = new  HashMap<>();
				responseBody.put("message","LOGOUT SUCCESSFUL");
				return ResponseEntity.ok(responseBody);
				
			}catch (Exception e) {
				Map<String, String> errorBody = new  HashMap<>();
				errorBody.put("error","LOGOUT FAILD");
				return ResponseEntity.status(500).body(errorBody);			
			}

		}
	}

