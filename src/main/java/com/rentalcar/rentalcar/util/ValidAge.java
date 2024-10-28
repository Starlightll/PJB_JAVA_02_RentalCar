package com.rentalcar.rentalcar.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
public @interface ValidAge {
    String message() default "Age must be between 18 and 80 years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
