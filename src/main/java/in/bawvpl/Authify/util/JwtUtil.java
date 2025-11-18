package in.bawvpl.Authify.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    /**
     * Token expiration in seconds (default 3600 if not provided).
     */
    @Value("${jwt.expiration.seconds:3600}")
    private long jwtExpirationSeconds;

    private Key signingKey;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("Missing jwt.secret.key property");
        }

        // If property is base64-encoded, decode; otherwise use raw bytes
        if (isBase64(jwtSecret)) {
            byte[] decoded = Base64.getDecoder().decode(jwtSecret);
            signingKey = Keys.hmacShaKeyFor(decoded);
        } else {
            signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationSeconds * 1000L);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        Date exp = extractExpiration(token);
        return exp.before(new Date());
    }

    /**
     * Validates token: checks signature and expiration and optionally subject matches.
     */
    public boolean validateToken(String token, String expectedUsername) {
        try {
            final String username = extractUsername(token);
            return (username != null && username.equals(expectedUsername) && !isTokenExpired(token));
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean isBase64(String s) {
        if (s == null) return false;
        String trimmed = s.trim();
        // quick check — base64 strings are typically length%4==0 and contain only base64 chars
        if (trimmed.length() % 4 != 0) return false;
        return trimmed.matches("^[A-Za-z0-9+/=\\r\\n]+$");
    }
}
