package com.example.gestao.carros.infra.security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Adicione estes imports para Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    // Adicione o Logger aqui
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    TokenService tokenService;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Request received. Method: {}, URI: {}", request.getMethod(), request.getRequestURI()); // Log da requisição

        // Para requisições OPTIONS, você pode querer adicionar um log específico
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("Preflight OPTIONS request detected for URI: {}", request.getRequestURI());
            // O filtro CORS do Spring Security deve lidar com isso antes, mas é bom logar aqui para ver
        }

        var token = this.recoverToken(request);
        if (token != null) {
            logger.debug("Token recovered: {}", token); // Use debug para não vazar token em produção
        } else {
            logger.info("No Authorization token found in request.");
        }

        var login = tokenService.validateToken(token);

        if (login != null) {
            logger.info("Token validated. Login: {}", login);
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("User {} authenticated successfully.", login);
        } else if (token != null) { // Se o token existe, mas não é válido
             logger.warn("Invalid or expired token for URI: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
        logger.info("Response sent for URI: {}", request.getRequestURI()); // Log após o processamento
    }

    private String recoverToken(HttpServletRequest request) {	
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}