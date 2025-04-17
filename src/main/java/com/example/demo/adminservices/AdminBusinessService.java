package com.example.demo.adminservices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.Entitys.Order;
import com.example.demo.Entitys.OrderItem;
import com.example.demo.Entitys.OrderStatus;
import com.example.demo.Repositorys.OrderItemRepository;
import com.example.demo.Repositorys.OrderRepository;
import com.example.demo.Repositorys.ProductRepository;

@Service
public class AdminBusinessService {

	OrderRepository orderRepository;
	OrderItemRepository orderItemRepository;
	ProductRepository productRepository;

	public AdminBusinessService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,ProductRepository productRepository ) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
	}

	// Claculating the Monthly business by passing month and year
	public Map<String, Object> calculateMonthlyBusiness(int month, int year){

		if(year < 2000 || year > 2025) {
			throw new IllegalArgumentException("Inavlid Year " + year);
		}
		List<Order> successFullOrders =  orderRepository.findSuccessfulOrdersByMonthAndYear(month, year);

		double totalBusiness = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();

		for(Order order : successFullOrders) {

			totalBusiness += order.getTotalAmount().doubleValue();

			List<OrderItem> orderItems= orderItemRepository.findByOrderId(order.getOrderId());
			for(OrderItem item : orderItems) {
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0)+ item.getQuantity());
			}
		}

		Map<String, Object> businessReport = new HashMap<>();

		businessReport.put("totalBusiness", totalBusiness);
		businessReport.put("categorySales", categorySales);

		return  businessReport;
	}


	//Calculating the Daily Business By passing Current date
	public Map<String, Object> calculateDailyBusiness(LocalDate date){

		if(date == null) {
			throw new IllegalArgumentException("Inavlid Date as date cannot be null");
		}
		List<Order> successFullOrders =  orderRepository.findSuccessfulOrdersByDate(date);

		double totalBusiness = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();

		for(Order order : successFullOrders) {

			totalBusiness += order.getTotalAmount().doubleValue();

			List<OrderItem> orderItems= orderItemRepository.findByOrderId(order.getOrderId());
			for(OrderItem item : orderItems) {
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0)+ item.getQuantity());
			}
		}

		Map<String, Object> businessReport = new HashMap<>();

		businessReport.put("totalBusiness", totalBusiness);
		businessReport.put("categorySales", categorySales);

		return businessReport;
	}


	// calculating the Yearly Business By passing a year
	public Map<String, Object> calculateYearlyBusiness(int year){

		if(year < 2000 || year > 2025) {
			throw new IllegalArgumentException("Inavlid Year " + year);
		}
		List<Order> successFullOrders =  orderRepository.findSuccessfulOrdersByYear(year);

		double totalBusiness = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();

		for(Order order : successFullOrders) {

			totalBusiness += order.getTotalAmount().doubleValue();

			List<OrderItem> orderItems= orderItemRepository.findByOrderId(order.getOrderId());
			for(OrderItem item : orderItems) {
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0)+ item.getQuantity());
			}
		}

		Map<String, Object> businessReport = new HashMap<>();

		businessReport.put("totalBusiness", totalBusiness);
		businessReport.put("categorySales", categorySales);

		return businessReport;
	}


	// Calcualting the Overal Business 
	public Map<String, Object> calculateOverallBusiness(){

		BigDecimal totalOverAllBusiness = orderRepository.calculateOverAllBusiness();
		List<Order> successFullOrders = orderRepository.findAllByStatus(OrderStatus.SUCCESS.name());
		Map<String, Integer> categorySales = new HashMap<>();
		for(Order order : successFullOrders) {
			List<OrderItem> orderItems= orderItemRepository.findByOrderId(order.getOrderId());
			for(OrderItem item : orderItems) {
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0)+ item.getQuantity());
			}
		}
		Map<String, Object> businessReport = new HashMap<>();
		businessReport.put("totalBusiness", totalOverAllBusiness.doubleValue());
		businessReport.put("categorySales", categorySales);

		return businessReport;
	}



}
