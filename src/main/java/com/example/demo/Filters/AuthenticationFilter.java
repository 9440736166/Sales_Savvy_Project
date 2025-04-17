package com.example.demo.Filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.demo.Entitys.Role;
import com.example.demo.Entitys.User;
import com.example.demo.Repositorys.Userrepository;
import com.example.demo.Services.AuthService;

import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/api/*","/admin/*"})
@Component
public class AuthenticationFilter implements Filter{

	private final AuthService authService;
	private final Userrepository userrepository;
	public static final String ALLOWED_ORIGIN = "http://localhost:5173";

	public static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	private static final String[] UNAUTHENTICATED_PATHS = {
			"/api/users/register",
			"/api/auth/login"
	};

	
	public AuthenticationFilter(AuthService authService, Userrepository userrepository) {
		System.out.println("Filert is started.");
		this.authService = authService;
		this.userrepository = userrepository;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			executeFilterLogin(request, response, chain);

		} catch (Exception e) {
			logger.error("Unexpected Error in Authentication Filter ",e);
			HttpServletResponse httpServletResponse =  (HttpServletResponse) response;
			sendErrorResponse(httpServletResponse,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Internal Server Error");
		}

	}


	public void executeFilterLogin(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException  {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		

		String requestURI = httpRequest.getRequestURI();
		logger.info("Request URI : {}", requestURI);
		if(Arrays.asList(UNAUTHENTICATED_PATHS).contains(requestURI)) {
			chain.doFilter(request, response);
			return;
		}
		
		// Handel preflight (OPTIONS) requests
//		if(httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
//			setCORSHeaders(httpResponse);
//			return;
//		}
		
		if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
		    setCORSHeaders(httpResponse);
		    return;
		}

		
		//Extract and validate the token
		String  token = getAuthonTokenFromCookies(httpRequest);
		System.out.println(token);
		if(token == null || !authService.validateToken(token)) {
			System.out.println("Received Token: " + token);
			sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized : Invalid or missing token");
			return;
		} 

		//Extract username and verify user
		String username = authService.extractUsername(token);
		Optional<User> useroptional = userrepository.findByUsername(username);
		if(useroptional.isEmpty()) {
			sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized : User Not Found");
			return;
		} 
		
		User authenticatedUser = useroptional.get();
		Role role = authenticatedUser.getRole();
		logger.info("Authenticated User : {}, Role: {}", authenticatedUser.getUsername(), role);

		//Role-based access control
		if(requestURI.startsWith("/admin/") && role != Role.ADMIN) {
			sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Unauthorized : Admin access required");
			return;
		}
		
		
		if(requestURI.startsWith("/api/") && role != Role.CUSTOMER) {

			sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Unauthorized : Customer access required");
			return;
		}

		//Attach user details to request
		httpRequest.setAttribute("authenticatedUser", authenticatedUser);
		chain.doFilter(request, response);

	}
	
	// My code 
	private void setCORSHeaders(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setStatus(HttpServletResponse.SC_OK);

	}

	
	public void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
		response.setStatus(statusCode);
		response.getWriter().write(message);
	}

	
	// get the token from the request
	//My code
	private String getAuthonTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			return Arrays.stream(cookies)
					.filter(cookie -> "authToken".equals(cookie.getName()))
					.map(Cookie::getValue)
					.findFirst()
					.orElse(null);
		}
		return null;
	
	}

	// Deepseek
//	private String getAuthonTokenFromCookies(HttpServletRequest request) {
//	    // Log request headers for debugging
//	    System.out.println("Request Headers:");
//	    Enumeration<String> headerNames = request.getHeaderNames();
//	    while (headerNames.hasMoreElements()) {
//	        String headerName = headerNames.nextElement();
//	        System.out.println(headerName + ": " + request.getHeader(headerName));
//	    }
//
//	    // Retrieve cookies from the request
//	    Cookie[] cookies = request.getCookies();
//	    if (cookies != null) {
//	        System.out.println("Cookies found in request:");
//	        for (Cookie cookie : cookies) {
//	            System.out.println("Cookie Name: " + cookie.getName() + ", Cookie Value: " + cookie.getValue());
//	        }
//	        // Find the authToken cookie
//	        return Arrays.stream(cookies)
//	                .filter(cookie -> "authToken".equals(cookie.getName()))
//	                .map(Cookie::getValue)
//	                .findFirst()
//	                .orElse(null);
//	    } else {
//	        System.out.println("No cookies found in request.");
//	    }
//	    return null;
//	}
}
