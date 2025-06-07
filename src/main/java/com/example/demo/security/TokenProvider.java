package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UserEntity;
import com.example.demo.security.vo.CustomUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/*
	JWT 토큰을 생성하고 검증하는 역할을 수행하는 클래스
*/

@Service
public class TokenProvider {

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	private Key SIGNING_KEY;

	@PostConstruct
	public void init() {
		this.SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
	
	public String create(UserEntity userEntity) {
		Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

		return Jwts.builder()
			.signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
			.setSubject(String.valueOf(userEntity.getId()))
			.setIssuer("demo app")
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.compact();
	}

	public String validateAndGetUserId(String token) {
		Claims claims = Jwts.parserBuilder()
            .setSigningKey(SIGNING_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();

		return claims.getSubject();
	}

	public String create(final Authentication authentication) {
		CustomUser userPrincipal = (CustomUser) authentication.getPrincipal();

		Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

		return Jwts.builder()
			.setSubject(userPrincipal.getName())
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
			.compact();
	}

	public String createByUserId(final Long userId) {
		Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
			.compact();
	}
}
