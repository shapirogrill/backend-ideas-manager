package com.shapirogrill.ideasmanager.security;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.user.UserRepository;
import com.shapirogrill.ideasmanager.utils.TestClassFactory;

@SpringBootTest
public class CusetomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void givenNotExistingUsername_whenLoadUserByUsername_thenUserNotFound() {
        // Given
        String username = "invalid_username";
        Mockito.when(userRepository.findByUsername(username)).thenThrow(UserNotFoundException.class);

        // When & then
        Assertions.assertThrows(UserNotFoundException.class,
                () -> this.userDetailsService.loadUserByUsername(username));
    }

    @Test
    public void givenUsername_whenLoadUserByUsername_thenUserDetails() {
        // Given
        String username = "username";
        String password = "pwd";
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(TestClassFactory.createUser(username, password)));

        // When
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        // Then
        Assertions.assertTrue(userDetails.getUsername().equals(username));
        Assertions.assertTrue(userDetails.getPassword().equals(password));
    }

}
