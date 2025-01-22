package com.example.movie.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final String secretKey = "yourVeryLongAndComplexSecretKeyThatIsAtLeast256BitsLong123456789";

    public String decodeToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        } catch (Exception e) {
            System.err.println("Token Decoding Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error decoding token", e);
        }
    }
}
