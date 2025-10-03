package com.harel.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * מחשב תאריך בעוד מספר ימים מהיום.
     * @param daysAhead מספר ימים מהיום
     * @return מחרוזת התאריך בפורמט "yyyy-MM-dd"
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
