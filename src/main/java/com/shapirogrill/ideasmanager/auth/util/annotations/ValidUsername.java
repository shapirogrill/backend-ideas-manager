package com.shapirogrill.ideasmanager.auth.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "Invalid username: must be 5-20 characters, alphanumeric, underscore or hyphen only";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
