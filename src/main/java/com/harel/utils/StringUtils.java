package com.harel.utils;

public class StringUtils {

    
     public static int getDaysFromString(String totalDaysStr) {
        // Assumes totalDaysStr contains only digits or is like "30 days"
        String digits = totalDaysStr.replaceAll("\\D+", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }
}
