package br.com.maisprati.projeto.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;

public final class PhoneUtils {
    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    private PhoneUtils() {}

    public static String formatToE164(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            return null;
        }
        try {
            Phonenumber.PhoneNumber parsedNumber = PHONE_UTIL.parse(rawPhone.trim(), null);
            return PHONE_UTIL.format(parsedNumber, PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            return rawPhone.replaceAll("[^0-9+]", "");
        }
    }
}
