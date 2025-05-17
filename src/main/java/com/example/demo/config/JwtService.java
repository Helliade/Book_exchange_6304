package com.example.demo.config;

        import com.example.demo.Models.Username;
        import io.jsonwebtoken.Claims;
        import io.jsonwebtoken.Jwts;
        import io.jsonwebtoken.io.Decoders;
        import io.jsonwebtoken.security.Keys;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Service;

        import javax.crypto.SecretKey;
        import java.util.Date;
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

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer("DCB")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000))  //10 секунд
                .signWith(secretKey) // Используем предварительно созданный ключ
                .compact();
    }

    public String generateToken(Username user) {
        return Jwts.builder()
                .subject(user.getLogin())
                .issuer("DCB")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000))  //10 секунд
                .signWith(secretKey) // Используем предварительно созданный ключ
                .compact();
    }

    // Извлечение username из токена
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Проверка токена
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
