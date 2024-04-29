package org.example.ecommerce.util;

public class StrChecker {
    private StrChecker() {
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}

