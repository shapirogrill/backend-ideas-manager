package com.shapirogrill.ideasmanager.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;

    @GetMapping("/users")
    public Iterable<User> findAllEmployees() {
        return this.userRepository.findAll();
    }
}
