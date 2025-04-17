package com.example.demo.Services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Entitys.JWTToken;
import com.example.demo.Entitys.User;
import com.example.demo.Repositorys.JWTTokenRepository;
import com.example.demo.Repositorys.Userrepository;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

	private final Key SIGNING_KEY;
	Userrepository userrepository;
	JWTTokenRepository jwtTokenRepository;
	BCryptPasswordEncoder passwordEncoder = new  BCryptPasswordEncoder();

	public AuthService(Userrepository userrepository,JWTTokenRepository jwtTokenRepository,@Value("${jwt.secret}") String jwtsecret ) {

		this.userrepository = userrepository;
		this.jwtTokenRepository =jwtTokenRepository;
		this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtsecret.getBytes(StandardCharsets.UTF_8));
	}


	public User Login(String username, String password) {

		// Step_1 Fetch the user object
		Optional<User> existinguser = userrepository.findByUsername(username);
		if(existinguser.isPresent()) {

			User user = existinguser.get();
			if(!passwordEncoder.matches(password, user.getPassword())) { 

				throw new RuntimeException("Invalid Password");
			}  
			return user;
		} else {
			throw new RuntimeException("Invalid Username");
		}

	}


	public String generateToken(User user) {
    
		String token;

		LocalDateTime currentTime = LocalDateTime.now();
		JWTToken existingToken = jwtTokenRepository.findByUserId(user.getUserId());

		if(existingToken != null && currentTime.isBefore(existingToken.getExpiresAt())){

			token=existingToken.getToken();
		} else {
			token = generateNewToken(user);
			if(existingToken != null) {
				jwtTokenRepository.delete(existingToken);
			}

			saveToken(user, token);
		}

		return token;
	}

	public String generateNewToken(User user) {

		JwtBuilder builder = Jwts.builder();
		builder.setSubject(user.getUsername());
		builder.claim("role", user.getRole().name());
		builder.setIssuedAt(new Date());
		builder.setExpiration(new Date(System.currentTimeMillis()+3600000));
		builder.signWith(SIGNING_KEY);
		String token = builder.compact();
		return token;
	}

	public void saveToken(User user, String token) {

		JWTToken jwtToken = new JWTToken(user,token,LocalDateTime.now(),LocalDateTime.now().plusHours(1));
		jwtTokenRepository.save(jwtToken);
		System.out.println(token);
	}

	// Validate the token
	public boolean validateToken(String token) {
		try {

			Jwts.parserBuilder()
			.setSigningKey(SIGNING_KEY)
			.build()
			.parseClaimsJws(token);

			// Check the token is present or not 
			Optional<JWTToken> jwtToken =jwtTokenRepository.findByToken(token);

			if(jwtToken.isPresent()) {
				return jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
			}
			return false;
		} catch (Exception e) {
			System.out.println(" Token validation is faild " + e.getMessage());
			return false;			
		}
	}

	public String extractUsername(String token) {

		return  Jwts.parserBuilder()
				.setSigningKey(SIGNING_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public void logout(User user) {
		
		jwtTokenRepository.deleteByUserId(user.getUserId());
	}
}
