package com.shapirogrill.ideasmanager.auth.util.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{5,20}$";
    // Contains only alpha, numerical, '_' and '-' between 5 and 20 chars.

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return false;
        }
        return username.matches(USERNAME_PATTERN);
    }
}
