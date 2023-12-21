package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	private Key getSignKey() {//pega a chave secret e criptografa ela pra usar ela 
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private Claims extractAllClaims(String token) { // abre o token e pega as clains || o nome do user quanto tempo vale e quando foi criado
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {//pega as clains de uma forma diferente
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String extractUsername(String token) {// como o nome sugere pega o nome
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {//data de espiracao
		return extractClaim(token, Claims::getExpiration);
	}

	private Boolean isTokenExpired(String token) {//verifica se ja esta expirado
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {//valida o token
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));//verifica o nome e a expiracao
	}

	private String createToken(Map<String, Object> claims, String userName) {//cria a clain
		return Jwts.builder()
					.setClaims(claims)//cria as clains abaixo
					.setSubject(userName)//nome 
					.setIssuedAt(new Date(System.currentTimeMillis()))//a data que foi criado
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))//tempo de expiracao
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	public String generateToken(String userName) {//pegar todas as clains e criar o token
		Map<String, Object> claims = new HashMap<>();//map e o mesmo formato do json
		return createToken(claims, userName);
	}

}