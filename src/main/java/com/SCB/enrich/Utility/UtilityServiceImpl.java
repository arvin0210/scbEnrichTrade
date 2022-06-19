package com.SCB.enrich.Utility;

import org.apache.commons.validator.routines.CurrencyValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.springframework.stereotype.Component;

@Component
public class UtilityServiceImpl implements UtilityService {
    @Override
    public boolean isInteger(String str) {
        boolean returnValue = true;
        if (null == new IntegerValidator().validate(str)) {
            returnValue = false;
        }
        return returnValue;
    }

    @Override
    public boolean isDouble(String str) {
        boolean returnValue = true;
        if (null == new CurrencyValidator().validate(str)) {
            returnValue = false;
        }
        return returnValue;
    }
}
