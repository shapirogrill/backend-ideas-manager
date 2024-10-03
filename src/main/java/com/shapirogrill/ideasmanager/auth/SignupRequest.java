package com.shapirogrill.ideasmanager.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    private String username;
    private String password;
}
