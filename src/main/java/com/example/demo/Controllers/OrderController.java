package com.example.demo.Controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entitys.User;
import com.example.demo.Services.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> getOrderForUser(HttpServletRequest request){
	
		try {
		User user = (User)request.getAttribute("authenticateduser");
		if(user==null) {
			return ResponseEntity.status(401).body(Map.of("error","User Not Authenticated"));
		}
		Map<String, Object> response = orderService.getOrderForUser(user);
		
		
		return ResponseEntity.ok(response);
		}catch (IllegalArgumentException e) {
			return ResponseEntity.status(401).body(Map.of("error",e.getMessage()));
		}
		 catch (Exception e) {
			 e.printStackTrace();
			 return ResponseEntity.status(500).body(Map.of("error","An Unexpected Error Occured"));
		 }
	}
}
