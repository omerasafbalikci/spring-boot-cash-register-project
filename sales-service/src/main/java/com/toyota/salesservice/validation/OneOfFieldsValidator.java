package com.toyota.salesservice.validation;

import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class for {@link OneOfFields} annotation.
 * Checks that only one of the fields (buyPay, percent, or moneyDiscount) is provided in a {@link CreateCampaignRequest}.
 */

public class OneOfFieldsValidator implements ConstraintValidator<OneOfFields, CreateCampaignRequest> {
    @Override
    public void initialize(OneOfFields constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateCampaignRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean isBuyPayPresent = value.getBuyPay() != null && !value.getBuyPay().isBlank();
        boolean isPercentPresent = value.getPercent() != null;
        boolean isMoneyDiscountPresent = value.getMoneyDiscount() != null;

        int presentCount = 0;
        if (isBuyPayPresent) presentCount++;
        if (isPercentPresent) presentCount++;
        if (isMoneyDiscountPresent) presentCount++;

        return presentCount == 1;
    }
}
