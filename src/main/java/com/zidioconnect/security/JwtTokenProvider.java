package com.zidioconnect.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final int jwtExpirationInMs;

    public JwtTokenProvider(@Value("${jwt.expiration}") int jwtExpirationInMs) {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(String email) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token: ", e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public String generateTokenWithRole(String email, String role) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            return Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token with role: ", e);
            throw new RuntimeException("Error generating JWT token with role", e);
        }
    }

    public String getEmailFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Error getting email from JWT token: ", e);
            throw new RuntimeException("Error getting email from JWT token", e);
        }
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            return false;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            return false;
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            return false;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            return false;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromJWT(token);
        // You can enhance this to load user roles from DB if needed
        return new UsernamePasswordAuthenticationToken(email, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}