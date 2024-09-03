package com.cbio.app.base.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateRocketUtils {

    public static String FORMAT_BRL = "HH:mm dd/MM";
    public static String getDateTimeFormated(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(FORMAT_BRL).format(localDateTime);
    }
}
