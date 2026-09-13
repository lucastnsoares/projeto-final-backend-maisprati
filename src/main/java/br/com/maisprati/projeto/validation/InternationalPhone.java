package br.com.maisprati.projeto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InternationalPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface InternationalPhone {
    String message() default "Número de telefone inválido.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
