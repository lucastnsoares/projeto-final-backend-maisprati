package br.com.maisprati.projeto.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InternationalPhoneValidator implements ConstraintValidator<InternationalPhone, String> {

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String trimmedValue = value.trim();
        if (!trimmedValue.startsWith("+")) {
            return false;
        }

        try {
            PhoneNumber number = phoneUtil.parse(trimmedValue, null);

            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }
}