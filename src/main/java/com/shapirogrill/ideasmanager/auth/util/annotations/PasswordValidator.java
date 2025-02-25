package com.shapirogrill.ideasmanager.auth.util.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%;,^&+=])[A-Za-z0-9@#$%^&+=,;]{8,20}$";
    // Contains only alpha, numerical and special chars. Size between 8 and 20
    // chars.

    @Override
    public boolean isValid(String pwd, ConstraintValidatorContext context) {
        if (pwd == null) {
            return false;
        }
        return pwd.matches(PASSWORD_PATTERN);
    }
}
