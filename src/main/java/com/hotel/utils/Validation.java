package com.hotel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Validation {
    private Validation() {}   // utility class, never instantiated

    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "password", "password1", "12345678", "123456789", "qwerty123", "iloveyou", "admin123");

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (e.length() > 254 || e.contains(" ") || e.contains("..")) return false;
        int at = e.indexOf('@');
        if (at <= 0 || at != e.lastIndexOf('@')) return false;     // exactly one @
        String local = e.substring(0, at);
        if (local.length() > 64 || local.startsWith(".") || local.endsWith(".")) return false;
        return e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    }

    /** Returns the list of broken rules; an empty list means the password is acceptable. */
    public static List<String> passwordProblems(String password, String email) {
        List<String> errors = new ArrayList<>();
        if (password == null) { errors.add("Password is required"); return errors; }
        if (password.length() < 8)  errors.add("At least 8 characters");
        if (password.length() > 64) errors.add("At most 64 characters");
        if (!password.matches(".*[A-Z].*")) errors.add("At least one uppercase letter");
        if (!password.matches(".*[a-z].*")) errors.add("At least one lowercase letter");
        if (!password.matches(".*\\d.*"))   errors.add("At least one digit");
        if (!password.matches(".*[^A-Za-z0-9].*")) errors.add("At least one special character");
        if (password.matches(".*\\s.*"))    errors.add("No spaces allowed");
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) errors.add("Password is too common");
        if (email != null && isValidEmail(email)) {
            String local = email.trim().toLowerCase().split("@")[0];
            if (local.length() >= 3 && password.toLowerCase().contains(local))
                errors.add("Password must not contain your email name");
        }
        return errors;
    }

    public static boolean isStrongPassword(String password, String email) {
        return passwordProblems(password, email).isEmpty();
    }
}