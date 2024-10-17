package com.shapirogrill.ideasmanager.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shapirogrill.ideasmanager.security.SecurityConfig;
import com.shapirogrill.ideasmanager.user.User;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.user.UserRepository;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final String endpoint = "/api/auth";

    private final String mockUsername = "Username";

    private final String mockPassword = "PassWord";

    @Test
    public void givenExistingUsername_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String existingUsername = "usernameTaken";
        SignupRequest signupRequest = new SignupRequest(existingUsername, mockPassword);

        Mockito.when(this.userRepository.existsByUsername(Mockito.eq(existingUsername)))
                .thenReturn(true);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenValidSignUpRequest_whenRegister_thenOK() throws Exception {
        // Given
        SignupRequest signupRequest = new SignupRequest(mockUsername, mockPassword);

        // Mock
        Mockito.when(this.userRepository.existsByUsername(Mockito.eq(mockUsername)))
                .thenReturn(false);
        Mockito.when(this.userRepository.save(Mockito.any(User.class))).thenReturn(null);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenNonExistingUsername_whenLogin_thenUnauthorized() throws Exception {
        // Given
        String nonExistingUsername = "NotAUser";
        LoginRequest loginRequest = new LoginRequest(nonExistingUsername, mockPassword);

        // Mock
        Mockito.when(
                this.authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(UserNotFoundException.class);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void givenNonValidPassword_whenLogin_thenUnauthorized() throws Exception {
        // Given
        String nonValidPassword = "UnvalidPwd";
        LoginRequest loginRequest = new LoginRequest(mockUsername, nonValidPassword);

        // Mock
        Mockito.when(
                this.authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void givenValidCredentials_whenLogin_thenOk() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest(mockUsername, mockPassword);
        String token = "Returned-Token";

        // Mock
        Mockito.when(
                this.authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(null, null));

        Mockito.when(this.jwtTokenProvider.generateToken(Mockito.any(Authentication.class)))
                .thenReturn(token);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token)); // Assert success
    }
}
