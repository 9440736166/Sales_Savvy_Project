package com.example.demo.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.Entitys.OrderItem;
import com.example.demo.Entitys.Product;
import com.example.demo.Entitys.ProductImage;
import com.example.demo.Entitys.User;
import com.example.demo.Repositorys.OrderItemRepository;
import com.example.demo.Repositorys.ProductImagesRepository;
import com.example.demo.Repositorys.ProductRepository;

@Service
public class OrderService {

	OrderItemRepository orderitemRepository;
	ProductRepository productRepository;
	ProductImagesRepository productImagesRepository;

	public OrderService(OrderItemRepository orderitemRepository,ProductRepository productRepository,ProductImagesRepository productImagesRepository) {
		this.orderitemRepository = orderitemRepository;
		this.productImagesRepository = productImagesRepository;
		this.productRepository =  productRepository;
	}

	public Map<String, Object> getOrderForUser(User user){

		List<OrderItem> orderItems =  orderitemRepository.findSuccessfulOrderItemByUserId(user.getUserId());

		Map<String, Object> response = new HashMap<>();
		response.put("username", user.getUsername());
		response.put("role", user.getRole());

		List<Map<String, Object>> products = new ArrayList<>();

		for (OrderItem item : orderItems) {
			Product product = productRepository.findById(item.getProductId()).orElse(null);	
		if(product==null) {
			continue;
		}
		List<ProductImage> images = productImagesRepository.findByProduct_ProductId(product.getProductId());
		
		String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
		
		Map<String, Object> productDetails = new HashMap<>();
		productDetails.put("order_id", item.getOrder().getOrderId());
		productDetails.put("quantity", item.getQuantity());
		productDetails.put("total_price", item.getTotalPrice());
		productDetails.put("image_url", imageUrl);
		productDetails.put("product_id", product.getProductId());
		productDetails.put("Description", product.getDescription());
		productDetails.put("price_per_unit", item.getPricePerUnit());
		
		products.add(productDetails);
		
		}

		response.put("products", products);

		return response;
	}
}
