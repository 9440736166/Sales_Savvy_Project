package com.example.demo.Repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Entitys.JWTToken;

import jakarta.transaction.Transactional;

@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {

	@Query("SELECT t FROM JWTToken t WHERE t.user.userId=:userId")
     public JWTToken findByUserId(int userId);
	
	public Optional<JWTToken> findByToken(String token);

	@Modifying
	@Transactional
	@Query("DELETE FROM JWTToken t WHERE t.user.userId = :userId")
	public void deleteByUserId(int userId);
	

}
