package com.shapirogrill.ideasmanager.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shapirogrill.ideasmanager.auth.JwtTokenProvider;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String token = getJwtFromRequest(request);

        try {
            if (token != null) {
                Claims claims = jwtTokenProvider.validateToken(token);
                if (claims != null) { // if null, token is not valid
                    Authentication authentication = jwtTokenProvider.getAuthentication(claims.getSubject());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (UserNotFoundException ex) {
            log.error("UserNotFoundException raised : " + ex);
        } catch (ServletException ex) {
            log.error("ServletException raised : " + ex);
        } catch (java.io.IOException ex) {
            log.error("IO Exception raised : " + ex);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
