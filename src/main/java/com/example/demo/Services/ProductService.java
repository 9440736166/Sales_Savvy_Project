package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Entitys.Category;
import com.example.demo.Entitys.Product;
import com.example.demo.Entitys.ProductImage;
import com.example.demo.Repositorys.CategoryRepository;
import com.example.demo.Repositorys.ProductImagesRepository;
import com.example.demo.Repositorys.ProductRepository;

@Service
public class ProductService {

	ProductRepository productRepository;
	ProductImagesRepository  productImagesRepository;
	CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, ProductImagesRepository  productImagesRepository, CategoryRepository categoryRepository) {

		this.productRepository = productRepository;
		this. productImagesRepository =  productImagesRepository;
		this.categoryRepository = categoryRepository;
	}

	//It will gives you list of products what you ask as category else it will show all products
	public List<Product> getProductByCategory(String categoryName){
			if(categoryName != null && !categoryName.isEmpty()) {
			Optional<Category> categoryOpt = categoryRepository.findBycategoryName(categoryName);
			if(categoryOpt.isPresent()) {
				Category category =  categoryOpt.get();
				return productRepository.findByCategory_CategoryId(category.getCategoryId());
			} else {
			
				throw new RuntimeException("Category is Not Found");
			}
		}  else {
			return productRepository.findAll();
		}
	}

	//Based on product ID we can fetch the product images
	public List<String>  getproductImages(Integer productId){
		
		List<ProductImage> productimg = productImagesRepository.findByProduct_ProductId(productId);
		
		List<String> imagesUrls = new ArrayList<>();
		
		for (ProductImage image : productimg) {
			imagesUrls.add(image.getImageUrl());
		}
			
		return imagesUrls;
	}



}
