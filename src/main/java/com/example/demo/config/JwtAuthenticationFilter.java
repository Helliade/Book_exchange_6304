package com.example.demo.config;


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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;
    private String login;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//TODO заключить все в трай кэтч
        final String authHedear = request.getHeader("Authorization");  //Ищет заголовок Authorization с префиксом Bearer

        if (authHedear == null || !authHedear.startsWith("Bearer")) {
            filterChain.doFilter(request,response);
            return;
        }

        final String jwt = authHedear.substring(7); //Удаляет "Bearer "
        login = jwtService.extractLogin(jwt);       //Декодирует логин из токена

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (login != null && authentication == null){  //Аутентификация пользователя

            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

             if (jwtService.isTokenValid(jwt,userDetails)) {
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

    public String getLogin() {
        return login;
    }
}

