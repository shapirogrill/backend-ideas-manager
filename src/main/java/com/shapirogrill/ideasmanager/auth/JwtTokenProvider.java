package com.shapirogrill.ideasmanager.auth;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.shapirogrill.ideasmanager.security.CustomUserDetailsService;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${app.jwtSecret}") // Load the secret key from application.properties
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}") // Load the expiration time from application.properties
    private int jwtExpirationInMs;

    private final CustomUserDetailsService userDetailsService;

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Génération du JWT
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();
    }

    public Claims validateToken(String authToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                    .build()
                    .parseClaimsJws(authToken) // parse the token to validate the signature and check the expiration
                    .getBody();
        } catch (SignatureException ex) {
           log.warn("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty.");
        }
        return null; // return false if any exception occurs
    }

    public Authentication getAuthentication(String username) throws UserNotFoundException {
        // Load user details from the database
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Create an Authentication object
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
