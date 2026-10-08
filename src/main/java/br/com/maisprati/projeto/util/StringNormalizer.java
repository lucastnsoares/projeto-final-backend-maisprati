package br.com.maisprati.projeto.util;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component 
public class StringNormalizer {


    @Named("toUpperCase")
    public String toUpperCase(String value) {
        return (value != null && !value.isBlank()) ? value.trim().toUpperCase(Locale.ROOT) : null;
    }

    @Named("toLowerCase")
    public String toLowerCase(String value) {
        return (value != null && !value.isBlank()) ? value.trim().toLowerCase(Locale.ROOT) : null;
    }

    @Named("trimOnly")
    public String trimOnly(String value) {
        return (value != null && !value.isBlank()) ? value.trim() : null;
    }
}
