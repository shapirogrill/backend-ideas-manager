package com.shapirogrill.ideasmanager.auth;

import com.shapirogrill.ideasmanager.auth.util.annotations.ValidPassword;
import com.shapirogrill.ideasmanager.auth.util.annotations.ValidUsername;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    @ValidUsername
    private String username;
    @ValidPassword
    private String password;
}
