package com.example.demo.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entitys.Product;
import com.example.demo.Entitys.User;
import com.example.demo.Services.ProductService;

import io.jsonwebtoken.lang.Objects;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/products")
public class ProductController {


	ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}


	@GetMapping
	public ResponseEntity<Map<String, Object>> getProducts (@RequestParam String category,HttpServletRequest request){
		try {
			
		User authenticatedUser = (User) request.getAttribute("authenticatedUser");
		if(authenticatedUser==null) {
			return ResponseEntity.status(401).body (Map.of("Error", "Unauthorized Access"));

		} 
		//List of products
		List<Product> products =  productService.getProductByCategory(category);

		//Creating Response
		Map<String,Object> response = new HashMap<>();
		//creating user information
		Map<String,String> userInfo = new HashMap<>();
		userInfo.put("username", authenticatedUser.getUsername());
		userInfo.put("Role", authenticatedUser.getRole().name());
		//adding to user information to response
		response.put("UserInfo", userInfo);

		List<Map<String,Object>> productList= new ArrayList<>();

		for(Product product: products) {
			Map<String,Object> productDetails = new HashMap<>();
			productDetails.put("productId",product.getProductId());
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price", product.getPrice());
			productDetails.put("stock", product.getStock());

			List<String> images=  productService.getproductImages(product.getProductId());
			productDetails.put("images", images);
			productList.add(productDetails);
		}

		response.put("products", productList);
		return ResponseEntity.ok(response);
	} catch (Exception e) {

		return ResponseEntity.badRequest().body(Map.of("Error",e.getMessage()));
	}
	}

	// for checking cookie
	@GetMapping("/test-cookie")
	public ResponseEntity<?> testCookie(@CookieValue(name = "authToken", required = false) String token) {
	    if (token == null) {
	        return ResponseEntity.status(401).body("Unauthorized: Missing token");
	    }
	    return ResponseEntity.ok("Token: " + token);
	}
}

