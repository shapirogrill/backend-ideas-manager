package com.shapirogrill.ideasmanager.auth.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid password: must be 8-20 characters that contains lower, UPPER and special chars";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
