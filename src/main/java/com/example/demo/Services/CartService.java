package com.example.demo.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Entitys.CartItem;
import com.example.demo.Entitys.Product;
import com.example.demo.Entitys.ProductImage;
import com.example.demo.Entitys.User;
import com.example.demo.Repositorys.CartRepository;
import com.example.demo.Repositorys.ProductImagesRepository;
import com.example.demo.Repositorys.ProductRepository;
import com.example.demo.Repositorys.Userrepository;

@Service
public class CartService {

	CartRepository cartRepository;
	Userrepository userrepository;
	ProductRepository productRepository;
	ProductImagesRepository productImagesRepository;

	public CartService(CartRepository cartRepository,Userrepository userrepository,ProductRepository productRepository,ProductImagesRepository productImagesRepository) {
		this.cartRepository = cartRepository;
		this.userrepository = userrepository;
		this.productRepository = productRepository;
		this.productImagesRepository = productImagesRepository;
	}

	public int getCartItems(int userId) {
		return cartRepository.countTotalItems(userId);
	}


	public void addToCart(int userId, int productId, int quantity) {
		User user = userrepository.findById(userId).orElseThrow(()->new IllegalArgumentException("User Not Found With Username : " + userId));
		Product product  = productRepository.findById(productId).orElseThrow(()->new IllegalArgumentException("Product Not Found With ID : " + productId));

		//Fetch cartitem with userId nd product to check if already userid with productId entry exists
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);

		if(existingItem.isPresent()) {
			//Updat ethe quantity of existing cart item with the product
			CartItem cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity()+1);
			cartRepository.save(cartItem);
		}  else {
			//Create new cart item with userid and new product and save
			CartItem newItem = new CartItem(user,product,quantity);
			cartRepository.save(newItem);
		}

	}

	public Map<String,Object> getcartItems(int userId){

		List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);
		Map<String,Object> response = new HashMap<>();
		User user =userrepository.findById(userId).orElseThrow( ()-> new IllegalArgumentException("User not found"));
		response.put("username", user.getUsername());
		response.put("role", user.getRole());

		List<Map<String, Object>> products = new ArrayList<>();
		int overallTotalPrice =  0;
		for(CartItem cartItem:cartItems) {
			Map<String, Object> productDetails = new HashMap<>();
			Product product = cartItem.getProduct();
			List<ProductImage>	productImages= productImagesRepository.findByProduct_ProductId(product.getProductId());

			String imageUrls = null;
			if(productImages != null && !productImages.isEmpty()) {
				imageUrls = productImages.get(0).getImageUrl();
			} else {
				imageUrls = "default-Image-Url";
			}
			productDetails.put("productId", product.getProductId());
			productDetails.put("image_url",imageUrls);
			productDetails.put("name", product.getDescription());
			productDetails.put("price_per_unit",product.getPrice());
			productDetails.put("quantity", cartItem.getQuantity());
			productDetails.put("total_price",cartItem.getQuantity()*product.getPrice().doubleValue());

			products.add(productDetails);

			//overallTotalPrice = (int) (overallTotalPrice+cartItem.getQuantity()*product.getPrice().doubleValue());
			overallTotalPrice += cartItem.getQuantity()*product.getPrice().doubleValue();

		}

		Map<String, Object> cart = new HashMap<>();
		cart.put("product", products);
		cart.put("overall_total_price", overallTotalPrice);

		response.put("cart", cart);

		return response;
	}

	public void updateCartItemQuantity (int userId, int productId, int quantity) {
		User user = userrepository.findById(userId)
		.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
		// Fetch cart item for this userId and productId
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct (userId, productId);
		if (existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			if (quantity == 0) {
				deleteCartItem (userId, productId);
			} else {
				cartItem.setQuantity (quantity);
				cartRepository.save(cartItem);
			}
		}
	}
	
	public void deleteCartItem(int userId, int productId) {
		User user = userrepository.findById(userId)
		.orElseThrow(() -> new IllegalArgumentException("User not found"));
		Product product = productRepository.findById(productId)
		.orElseThrow(() -> new IllegalArgumentException("Product not found"));
		cartRepository.deleteCartItem (userId, productId);
		}
}
