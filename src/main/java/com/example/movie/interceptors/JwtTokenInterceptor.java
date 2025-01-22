package com.example.movie.interceptors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

public class JwtTokenInterceptor implements HandlerInterceptor {
    // Inject secret key từ application.properties
    @Value("${jwt.secret}")
    private String secretKey = "yourVeryLongAndComplexSecretKeyThatIsAtLeast256BitsLong123456789";

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // Skip for OPTIONS requests
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            return true;
//        }
//
//        // Get the Authorization header
//        String authHeader = request.getHeader("Authorization");
//
//        // Check if header exists and starts with "Bearer "
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
//            return false;
//        }
//
//        // Extract the token
//        String token = authHeader.substring(7);
//
//        try {
//            // Verify token
//            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
//            JWTVerifier verifier = JWT.require(algorithm)
//                    .build();
//
//            // Decode and verify
//            DecodedJWT jwt = verifier.verify(token);
//
//            // Additional checks
//            // Kiểm tra token đã hết hạn chưa
//            Date expiresAt = jwt.getExpiresAt();
//            if (expiresAt != null && expiresAt.before(new Date())) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
//                return false;
//            }
//
//            // Extract username from the "sub" claim
//            String username = jwt.getSubject();
//
//            // Add username to request attributes
//            request.setAttribute("username", username);
//
//            return true;
//        } catch (JWTVerificationException e) {
//            // Token verification failed
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//            return false;
//        } catch (Exception e) {
//            // Other unexpected errors
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token processing error");
//            return false;
//        }
//    }
}