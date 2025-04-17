package com.example.demo.Repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entitys.User;

@Repository
public interface Userrepository extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String username);
		
	Optional<User> findByEmail(String email);
	

	
}
