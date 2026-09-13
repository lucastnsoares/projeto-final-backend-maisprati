package br.com.maisprati.projeto.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfCnpjValidatorTest {

    private CpfCnpjValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CpfCnpjValidator();
    }

    @Test
    void shouldAcceptNullValue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void shouldAcceptBlankValue() {
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void shouldAcceptValidCpf() {
        assertTrue(
                validator.isValid("529.982.247-25", null)
        );
    }

    @Test
    void shouldRejectCpfWithInvalidCheckDigits() {
        assertFalse(
                validator.isValid("529.982.247-26", null)
        );
    }

    @Test
    void shouldRejectCpfWithRepeatedDigits() {
        assertFalse(
                validator.isValid("111.111.111-11", null)
        );
    }

    @Test
    void shouldRejectInvalidDocumentLength() {
        assertFalse(
                validator.isValid("123456", null)
        );
    }

    @Test
    void shouldRejectInvalidCpfCharacters() {
        assertFalse(
                validator.isValid("ABCDEFGHIJK", null)
        );
    }

    @Test
    void shouldRejectInvalidCnpjFormat() {
        assertFalse(
                validator.isValid("!!!!!!!!!!!!00", null)
        );
    }
}