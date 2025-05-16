package com.example.demo.config;

        import io.jsonwebtoken.Claims;
        import io.jsonwebtoken.Jwts;
        import io.jsonwebtoken.io.Decoders;
        import io.jsonwebtoken.security.Keys;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Service;

        import com.example.demo.Models.Username;

        import javax.crypto.SecretKey;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.function.Function;
@Service
public class JwtService {
    private final SecretKey secretKey;

    // Инициализируем ключ один раз при создании сервиса
    public JwtService() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                "yHAVE+Q8z5ynTYp2y4JNepFNLplUPDsjkInSMQTAXog="
        ));
    }

    public String generateToken(Username user) {
        return Jwts.builder()
                .subject(user.getLogin())
                .issuer("DCB")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 5000))  //5 секунд
                .signWith(secretKey) // Используем предварительно созданный ключ
                .compact();
    }

    // Извлечение username из токена
    public String extractLogin(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Проверка токена
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractLogin(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}
