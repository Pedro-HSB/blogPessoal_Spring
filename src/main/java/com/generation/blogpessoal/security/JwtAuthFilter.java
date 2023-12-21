package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;//chama para validar

    @Autowired
    private UserDetailsServiceImpl userDetailsService;//precisamos validar o user too

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {//cria um filter
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
    
        try{
            if (authHeader != null && authHeader.startsWith("Bearer ")) {//verifica se o token existe e se chama bearer
                token = authHeader.substring(7);//verifica os 7 caracteres
                username = jwtService.extractUsername(token);//pega so o token e oega o nome do usuario
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {//verifica se o usuario esta autenticado
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                if (jwtService.validateToken(token, userDetails)) {//aqui ele valida o token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            
            }
            filterChain.doFilter(request, response);//ele tenta fazer a authenticacao || busca um login e senha no corpo do HTTP
    

        }catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
                | SignatureException | ResponseStatusException e){//devolve todos os erros possiveis
            response.setStatus(HttpStatus.FORBIDDEN.value());//403 o token expirou ou nao existe
            return;
        }
    }
}