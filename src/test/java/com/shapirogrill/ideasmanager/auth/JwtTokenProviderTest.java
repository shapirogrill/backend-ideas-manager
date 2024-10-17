package com.shapirogrill.ideasmanager.auth;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import com.shapirogrill.ideasmanager.user.User;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.user.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
public class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication mockAuthentication;

    @MockBean
    private UserRepository userRepository;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private String mockUsername = "username";

    @Test
    public void testGenerateToken() {
        // Mock
        Mockito.when(mockAuthentication.getName()).thenReturn(mockUsername);

        // Act
        String token = jwtTokenProvider.generateToken(mockAuthentication);

        // Assert
        Assertions.assertNotNull(token);

        // Verify that token contains the correct subject (username)
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        Assertions.assertEquals(mockUsername, claims.getSubject());
        Assertions.assertNotNull(claims.getExpiration());
    }

    @Test
    public void testValidateToken_ValidToken() {
        // Mock
        Mockito.when(mockAuthentication.getName()).thenReturn(mockUsername);

        // Arrange
        String token = jwtTokenProvider.generateToken(mockAuthentication);

        // Act
        Claims claims = jwtTokenProvider.validateToken(token);

        // Assert
        Assertions.assertNotNull(claims);
        Assertions.assertEquals(mockUsername, claims.getSubject());
    }

    @Test
    public void testValidateToken_exceptionThrown() throws Exception {
        // Arrange
        String token = "I'm not a valid token";

        // Assert
        Assertions.assertNull(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void givenUser_whenGetAuthentication_thenAuthentication() {
        // Given
        String mockPassword = "password";
        User user = new User();
        user.setUsername(mockUsername);
        user.setPassword(mockPassword);

        Mockito.when(userRepository.findByUsername(Mockito.eq(mockUsername)))
                .thenReturn(Optional.of(user));

        // Act
        Authentication authentication = jwtTokenProvider.getAuthentication(mockUsername);

        // Assert
        Assertions.assertNotNull(authentication);
        Assertions.assertEquals(
                org.springframework.security.core.userdetails.User
                        .builder()
                        .username(mockUsername)
                        .password(mockPassword).build(),
                authentication.getPrincipal());
    }

    @Test
    public void givenInvalidUsername_whenGetAuthentication_thenUserNotFound() {
        // Given
        Mockito.when(userRepository.findByUsername(Mockito.eq(mockUsername)))
                .thenThrow(UserNotFoundException.class);

        // When & then
        Assertions.assertThrows(UserNotFoundException.class, () -> jwtTokenProvider.getAuthentication(mockUsername));
    }
}
