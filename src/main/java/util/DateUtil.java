package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Supporting functions for working with dates.
 */
public class DateUtil {

    /** Date template(can be changed). */
    private static final String DATE_PATTERN = "dd.MM.yyyy";

    /** Date formatter. */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * Using {@link DateUtil#DATE_PATTERN} to format date to string.
     * @param date - date
     * @return formatted string
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    /**
     * Converting string using {@link DateUtil#DATE_PATTERN} into {@link LocalDate}.
     * Returns null if string cant be converted.
     * @param dateString - Date as String
     * @return Resulting date
     */
    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    /**
     * Parse from labirint.
     * Returns null if string cant be converted.
     * @param dateString - Date as String
     * @return Resulting date
     */
    public static LocalDate parseLabirint(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString.trim().substring(0, dateString.indexOf(' ')), LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parse from ozon.
     * Returns null if string cant be converted.
     * @param dateString - Date as String
     * @return Resulting date
     */
    public static LocalDate parseOzon(String dateString) {
        String[] array = dateString.split(" ");
        String convertedString = array[0] + "." + monthToNum(array[1]) + "." + array[2];
        try {
            return DATE_FORMATTER.parse(convertedString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Checking string(is it valid date).
     * @param dateString
     * @return true - string is correct
     */
    public static boolean validDate(String dateString) {
        return DateUtil.parse(dateString) != null;
    }

    /**
     * Supporting function converting string-month to string-number.
     * @param month
     * @return
     */
    private static String monthToNum(String month) {
        switch (month) {
            case "января": return "01";
            case "февраля": return "02";
            case "марта": return "03";
            case "апреля": return "04";
            case "мая": return "05";
            case "июня": return "06";
            case "июля": return "07";
            case "августа": return "08";
            case "сентября": return "09";
            case "октября": return "10";
            case "ноября": return "11";
            case "декабря": return "12";
            default: return "-1";
        }
    }

}