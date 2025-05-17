package com.example.demo.config;


import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private TokenUsageService tokenUsageService;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, TokenUsageService tokenUsageService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.tokenUsageService = tokenUsageService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//TODO заключить все в трай кэтч ?
        String path = request.getRequestURI();
        if (path.startsWith("/api/users/login")) {
            filterChain.doFilter(request, response); // Пропускаем без проверки JWT
            return;
        }


        final String authHedear = request.getHeader("Authorization");  //Ищет заголовок Authorization с префиксом Bearer

        if (authHedear == null || !authHedear.startsWith("Bearer")) {
            filterChain.doFilter(request,response);
            return;
        }

        final String jwt = authHedear.substring(7); //Удаляет "Bearer "

        // Проверяем, не использовался ли токен ранее
        if (tokenUsageService.isTokenUsed(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token already used");
            return;
        }

        if (!jwtService.isTokenValid(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        String username = jwtService.extractUsername(jwt);       //Декодирует логин из токена
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Помечаем токен как использованный
        Date expiry = Jwts.parser()
                .setSigningKey(jwtService.getSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();
        tokenUsageService.markTokenAsUsed(jwt, expiry);

        // Создаем новый токен для следующего запроса
        String newToken = jwtService.generateToken(userDetails);
        response.setHeader("New-Access-Token", newToken);









        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (username != null && authentication == null){  //Аутентификация пользователя

             if (jwtService.isTokenValid(jwt)) {
                 UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(  //Создает объект аутентификации
                         userDetails,
                         null, userDetails.getAuthorities());

                 authenticationToken.setDetails(
                         new WebAuthenticationDetailsSource()
                                 .buildDetails(request)
                 );
                 // Сохраняет аутентификацию в SecurityContext
                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
             }
        }
        filterChain.doFilter(request,response);
    }

//    public String getLogin() {
//        return login;
//    }
}

