package br.com.maisprati.projeto.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternationalPhoneValidatorTest {

    private InternationalPhoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InternationalPhoneValidator();
    }

    @Test
    void shouldAcceptNullPhone() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void shouldAcceptBlankPhone() {
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void shouldAcceptValidInternationalPhone() {
        assertTrue(
                validator.isValid("+5551999999999", null)
        );
    }

    @Test
    void shouldRejectPhoneWithoutPlusSign() {
        assertFalse(
                validator.isValid("5551999999999", null)
        );
    }

    @Test
    void shouldRejectInvalidInternationalPhone() {
        assertFalse(
                validator.isValid("+123", null)
        );
    }

    @Test
    void shouldRejectMalformedPhone() {
        assertFalse(
                validator.isValid("+INVALID", null)
        );
    }
}