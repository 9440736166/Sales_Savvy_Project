package com.example.demo.adminservices;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Entitys.Category;
import com.example.demo.Entitys.Product;
import com.example.demo.Entitys.ProductImage;
import com.example.demo.Repositorys.CategoryRepository;
import com.example.demo.Repositorys.ProductImagesRepository;
import com.example.demo.Repositorys.ProductRepository;

@Service
public class AdminProductService {

	ProductRepository productRepository;
	ProductImagesRepository productImagesRepository;
	CategoryRepository categoryRepository;
	
	public AdminProductService(ProductRepository productRepository,ProductImagesRepository productImagesRepository,CategoryRepository categoryRepository) {
	
		this.productRepository = productRepository;
		this.productImagesRepository =  productImagesRepository;
		this.categoryRepository = categoryRepository;
	}
	
	public Product addProductWithImage(String name, String description,Double price,Integer stock,Integer categoryId,String imageUrl) {
	Optional<Category> category =	categoryRepository.findById(categoryId);
		
	if(category.isEmpty()) {
		throw new IllegalArgumentException("Invalid Category Id");
	} 
	
	Product product = new Product(name, description,BigDecimal.valueOf(price) , stock, category.get(), LocalDateTime.now(), LocalDateTime.now() );
		
	Product savedProduct = productRepository.save(product);
	
	if(imageUrl != null && !imageUrl.isEmpty()) {
	ProductImage productImage =	new ProductImage(savedProduct, imageUrl);
	
	productImagesRepository.save(productImage); 
	} else {
		throw new IllegalArgumentException("Product Image cannot Be Empty");
	}
	
	return savedProduct;
	}
	
	
	public void deleteProduct(int productId) {
		// First delete product image first then products because primary realation is there
		productImagesRepository.deleteByproductId(productId);
		productRepository.deleteById(productId);
	}
}
