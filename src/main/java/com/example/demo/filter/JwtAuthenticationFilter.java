package com.example.demo.filter;

import com.example.demo.service.JwtService;
import com.example.demo.service.TokenUsageService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final TokenUsageService tokenUsageService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, TokenUsageService tokenUsageService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.tokenUsageService = tokenUsageService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Пропускаем публичные эндпоинты
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Проверяем наличие токена в параметре запроса (для /home)
            String jwt = request.getParameter("token");

            // 3. Если нет в параметре, проверяем в заголовке
            if (jwt == null) {
                final String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                    return;
                }
            }

            // 4. Проверяем тип токена (должен быть access) Проверяем валидность токена
            if (!jwtService.validateAccessToken(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            // 5. Проверяем, не использовался ли токен ранее
            if (tokenUsageService.isTokenUsed(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token already used");
                return;
            }

            // 6. Извлекаем данные пользователя
            String username = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 7. Создаем аутентификацию
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 8. Устанавливаем аутентификацию в контекст
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 9. Помечаем токен как использованный
            Date expiry = Jwts.parser()
                    .setSigningKey(jwtService.getSecretKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getExpiration();
            tokenUsageService.markTokenAsUsed(jwt, expiry);

            // 10. Генерируем новый токен
            String newToken = jwtService.generateAccessToken(userDetails);
            response.setHeader("accessToken", newToken);


            // 11. Продолжаем цепочку фильтров
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Обработка непредвиденных ошибок
            throw new RuntimeException("Authentication failed:", e);
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//                    "Authentication failed: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();

        return  path.startsWith("/favicon.ico") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/auth") ||
                path.startsWith("/home") ||
                path.startsWith("/history") ||
                path.startsWith("/order") ||
                path.startsWith("/hello") ||
                path.endsWith(".html") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/error");
    }
}

