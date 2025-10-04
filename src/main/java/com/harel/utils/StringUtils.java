package com.harel.utils;

public class StringUtils {

    //* Extracts the number of days from a string  */
     public static int getDaysFromString(String totalDaysStr) {
        // Assumes totalDaysStr contains only digits or is like "30 days"
        String digits = totalDaysStr.replaceAll("\\D+", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }
}
