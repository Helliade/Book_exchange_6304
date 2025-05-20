package com.example.demo.config;

        import com.example.demo.Models.Username;
        import com.example.demo.service.UsernameService;
        import io.jsonwebtoken.*;
        import io.jsonwebtoken.io.Decoders;
        import io.jsonwebtoken.security.Keys;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Service;

        import javax.crypto.SecretKey;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.UUID;
//@Service
//public class JwtService {
//    private final SecretKey secretKey;
//
//    // Инициализируем ключ один раз при создании сервиса
//    public JwtService() {
//        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
//                "yHAVE+Q8z5ynTYp2y4JNepFNLplUPDsjkInSMQTAXog="
//        ));
//    }
//
//    public SecretKey getSecretKey() {
//        return secretKey;
//    }
//
//    public String generateToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuer("DCB")
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 50000))  //10 секунд
//                .signWith(secretKey) // Используем предварительно созданный ключ
//                .compact();
//    }
//
//    public String generateToken(Username user) {
//        return Jwts.builder()
//                .subject(user.getLogin())
//                .issuer("DCB")
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 50000))  //30 секунд
//                .signWith(secretKey) // Используем предварительно созданный ключ
//                .compact();
//    }
//
//    // Извлечение username из токена
//    public String extractUsername(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
//
//    // Проверка токена
//    public boolean isTokenValid(String token) {
//        try {
//            Jwts.parser()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException ex) {
//            throw new JwtException("Токен просрочен", ex);
//        } catch (UnsupportedJwtException ex) {
//            throw new JwtException("Неподдерживаемый токен", ex);
//        } catch (MalformedJwtException ex) {
//            throw new JwtException("Неверный формат токена", ex);
//        } catch (SignatureException ex) {
//            throw new JwtException("Невалидная подпись", ex);
//        }
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        return claimsResolver.apply(claims);
//    }
//
//    public Date extractExpiration(String token) {
//        return Jwts.parser()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getExpiration();
//    }
//}





@Service
public class JwtService {
    private final String SECRET_KEY = "yHAVE+Q8z5ynTYp2y4JNepFNLplUPDsjkInSMQTAXog=";
    private final long ACCESS_EXPIRATION = 3 * 60 * 1000; // 3 минуты
    private final long REFRESH_EXPIRATION = 6 * 60 * 60 * 1000; // 6 часов
    private final TokenUsageService tokenUsageService;
    private final UsernameService usernameService;


    public JwtService(TokenUsageService tokenUsageService, UsernameService usernameService) {
        this.tokenUsageService = tokenUsageService;
        this.usernameService = usernameService;
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, ACCESS_EXPIRATION, "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, REFRESH_EXPIRATION, "refresh");
    }

    private String buildToken(UserDetails userDetails, long expiration, String tokenType) {
        String tokenId = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenId", tokenId);
        claims.put("type", tokenType);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (!"access".equals(claims.get("type"))) {
                throw new JwtException("Not access token");
            }

            return !isTokenUsed(token) && isTokenUnexpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Token exception", e);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (!"refresh".equals(claims.get("type"))) {
                throw new JwtException("Not refresh token");
            }

            return !isTokenUsed(token) && isTokenUnexpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Token exception", e);
        }
    }

    public void invalidateToken(String token) {
        Claims claims = extractAllClaims(token);
        tokenUsageService.markTokenAsUsed(token, claims.getExpiration());
    }

    private boolean isTokenUsed(String token) {
        return tokenUsageService.isTokenUsed(token);
    }

    private boolean isTokenUnexpired(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Username extractUsernameModel(String token) {
        return usernameService.getUsernameByLogin(extractClaim(token, Claims::getSubject));
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getRemainingTokenExpiration(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (JwtException | IllegalArgumentException e) {
            return 0; // Токен невалидный или истекший
        }
    }
}
