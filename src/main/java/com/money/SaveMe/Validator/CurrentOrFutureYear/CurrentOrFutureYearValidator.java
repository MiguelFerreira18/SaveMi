package com.money.SaveMe.Validator.CurrentOrFutureYear;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CurrentOrFutureYearValidator implements ConstraintValidator<CurrentOrFutureYear, Integer> {

    public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
        if(year ==null) return false;
        int currentYear = Year.now().getValue();
        return year >= currentYear;


    }
}
