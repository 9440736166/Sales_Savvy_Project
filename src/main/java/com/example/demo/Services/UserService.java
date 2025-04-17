package com.example.demo.Services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entitys.User;
import com.example.demo.Repositorys.Userrepository;

@Service
public class UserService {

	private final Userrepository userrepository;
	
	public UserService(Userrepository userrepository) {
		this.userrepository = userrepository;
	}
	
	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public User ValadiateUser(User user) {
		
	  if(userrepository.findByUsername(user.getUsername()).isPresent()) {
		
		  
		  throw new RuntimeException("Username is already taken");
	  }
	  
	  if(userrepository.findByEmail(user.getEmail()).isPresent()){
		  
		  throw new RuntimeException("Email already exist");
	  }
	  
	  // encode the password
	  
//	    String plainpassword = user.getPassword();
//	    String encodepassword = passwordEncoder.encode(plainpassword);
//	    user.setPassword(encodepassword);
	  // or
	   user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		return userrepository.save(user);
	}
}
