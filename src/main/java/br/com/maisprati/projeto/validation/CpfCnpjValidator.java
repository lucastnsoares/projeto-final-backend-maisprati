package br.com.maisprati.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfOrCnpj, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String cleanDocument = value.replaceAll("[.\\-/]", "").trim().toUpperCase();

        if (cleanDocument.length() == 11) {
            return isValidCpf(cleanDocument);
        } else if (cleanDocument.length() == 14) {
            return isValidAlphanumericCnpj(cleanDocument);
        }

        return false;
    }

    private boolean isValidCpf(String cpf) {
        if (!cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) firstDigit = 0;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) secondDigit = 0;

            return (cpf.charAt(9) - '0' == firstDigit) && (cpf.charAt(10) - '0' == secondDigit);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isValidAlphanumericCnpj(String cnpj) {
        if (!cnpj.matches("^[A-Z0-9]{12}[0-9]{2}$") || cnpj.chars().distinct().count() == 1) {
            return false;
        }

        int[] firstWeights = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] secondWeights = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int asciiValue = cnpj.charAt(i) - 48;
                sum += asciiValue * firstWeights[i];
            }
            int remainder = sum % 11;
            int firstCheckDigit = (remainder < 2) ? 0 : 11 - remainder;

            sum = 0;
            for (int i = 0; i < 12; i++) {
                int asciiValue = cnpj.charAt(i) - 48;
                sum += asciiValue * secondWeights[i];
            }
            sum += firstCheckDigit * secondWeights[12];

            remainder = sum % 11;
            int secondCheckDigit = (remainder < 2) ? 0 : 11 - remainder;

            return (cnpj.charAt(12) - '0' == firstCheckDigit) && (cnpj.charAt(13) - '0' == secondCheckDigit);
        } catch (Exception ex) {
            return false;
        }
    }
}
