package com.shapirogrill.ideasmanager.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import com.shapirogrill.ideasmanager.user.User;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
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

    private final String endpoint = "/v1/auth";

    private final String mockUsername = "Username";

    private final String mockPassword = "PassWord1,";

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
    public void givenTooShortUsername_whenRegister_thenBadRequest() throws Exception {
        // Given
        String tooShortUsername = "cut"; // < 5 chars
        SignupRequest signupRequest = new SignupRequest(tooShortUsername, mockPassword);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenTooLongUsername_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String tooLongUsername = "usernameWithMoreThan20Chars"; // > 20 chars
        SignupRequest signupRequest = new SignupRequest(tooLongUsername, mockPassword);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenInvalidCharUsername_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String usernameWithInvalidChars = "user,;af,dp"; // Contains invalid chars
        SignupRequest signupRequest = new SignupRequest(usernameWithInvalidChars, mockPassword);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenTooShortPassword_whenRegister_thenBadRequest() throws Exception {
        // Given
        String tooShortPwd = "cU4t,"; // < 8 chars
        SignupRequest signupRequest = new SignupRequest(mockUsername, tooShortPwd);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenTooLongPassword_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String tooLongPwd = "pasword_ieithMoreThan20Chars"; // > 20 chars
        SignupRequest signupRequest = new SignupRequest(mockUsername, tooLongPwd);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenPwdWithoutLowerChar_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String invalidChar = "PWD-WITH0UT-LC"; // No Lower case
        SignupRequest signupRequest = new SignupRequest(mockUsername, invalidChar);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenPwdWithoutUpperChar_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String invalidChar = "pwd-with0ut-uc"; // No Upper case
        SignupRequest signupRequest = new SignupRequest(mockUsername, invalidChar);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenPwdWithoutNumerical_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String invalidChar = "PWD-WITHOUT-NUM"; // Without numerical
        SignupRequest signupRequest = new SignupRequest(mockUsername, invalidChar);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenPwdWithoutSpecialChar_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String invalidChar = "PWDwITH0UTspecChar"; // Without special char
        SignupRequest signupRequest = new SignupRequest(mockUsername, invalidChar);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenPwdWithInvalidChar_whenRegister_thenBadRequestException() throws Exception {
        // Given
        String invalidChar = "PWDwITH0UT  specChar"; // With spaces (invalid char)
        SignupRequest signupRequest = new SignupRequest(mockUsername, invalidChar);

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
                this.authenticationManager
                        .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
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
                this.authenticationManager
                        .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
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
                this.authenticationManager
                        .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
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
