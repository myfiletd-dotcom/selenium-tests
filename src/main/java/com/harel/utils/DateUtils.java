package com.harel.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Calculates a date in a few days from today
     * @param daysAhead A few days from today
     * @return  Format date string "yyyy-MM-dd"
     */
    public static String getDateFromToday(int daysAhead) {
        LocalDate date = LocalDate.now().plusDays(daysAhead-1);
        return date.format(formatter);
    }

    public static String getTodayDate() {
        LocalDate today = LocalDate.now();
        return today.format(formatter);

    }
}
