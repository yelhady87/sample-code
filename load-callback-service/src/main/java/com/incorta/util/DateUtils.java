package com.incorta.util;

public class DateUtils {
    private DateUtils() {
        // private constructor
    }

    public static String toDateTimestamp(long epoch) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new java.util.Date (epoch));
    }

}
