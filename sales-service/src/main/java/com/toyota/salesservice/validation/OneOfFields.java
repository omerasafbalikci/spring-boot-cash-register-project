package com.toyota.salesservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate that only one of the fields (buyPay, percent, or moneyDiscount) is provided in a class.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneOfFieldsValidator.class)
public @interface OneOfFields {
    String message() default "Only one of buyPay, percent, or moneyDiscount must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
