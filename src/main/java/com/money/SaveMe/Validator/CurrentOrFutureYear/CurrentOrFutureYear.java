package com.money.SaveMe.Validator.CurrentOrFutureYear;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CurrentOrFutureYearValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentOrFutureYear {
    String message() default "Year must be the current year or a future year";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
