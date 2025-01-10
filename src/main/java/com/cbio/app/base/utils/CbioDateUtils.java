package com.cbio.app.base.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CbioDateUtils {

    public static final String PLUS_3 = "+03:00";
    public static final String MINUS_3 = "-03:00";
    public static String FORMAT_BRL = "HH:mm dd/MM";
    public static String FORMAT_BRL_SEC = "HH:mm:ss dd/MM";
    public static String FORMAT_BRL_DATE_TIME = "dd/MM/yyyy HH:mm:ss";
    public static String FORMAT_BRL_DATE_TIME_FULL = "dd/MM/yyyy'T'HH:mm";

    public static String getDateTimeFormated(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(FORMAT_BRL).format(localDateTime);
    }

    public static String getDateTimeWithSecFormated(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(FORMAT_BRL_SEC).format(localDateTime);
    }

    public static String getDateTimeWithSecFormated(LocalDateTime localDateTime, String format) {
        return DateTimeFormatter.ofPattern(format).format(localDateTime);
    }

    public static String getDateTimeFormated(LocalDateTime localDateTime, String format, String offsetId) {
        ZonedDateTime saoPauloTime = localDateTime.atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneOffset.of(offsetId));
        return saoPauloTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static LocalDateTime getLocalDateTime(LocalDateTime localDateTime, String format, String offsetId) {
        ZonedDateTime saoPauloTime = localDateTime.atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneOffset.of(offsetId));
        return saoPauloTime.toLocalDateTime();
    }

    public static LocalDateTime fromDate(Date date) {
        return date.toInstant()
                .atZone(getZoneId())
                .toLocalDateTime();
    }

    public static ZoneId getZoneId() {
        return ZoneId.of(MINUS_3);
    }

    public static Date fromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(getZoneId()).toInstant());
    }

    public static class LocalDate {
        public static java.time.LocalDate now() {
            return getZonedDateTimeNow().toLocalDate();
        }

    }


    private static ZonedDateTime getZonedDateTimeNow() {
        return ZonedDateTime.now(getZoneId());
    }

    public static class LocalDateTimes {

        public static LocalDateTime now() {
            return getZonedDateTimeNow().toLocalDateTime();
        }

        public static LocalDateTime getFrom(LocalDateTime localDateTime) {
            return ZonedDateTime.of(localDateTime, getZoneId()).toLocalDateTime();
        }
        public static LocalDateTime getFrom(String localDateTime) {
            return ZonedDateTime.of(LocalDateTime.parse(localDateTime), getZoneId()).toLocalDateTime();
        }
    }
}
