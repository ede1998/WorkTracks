package me.erikhennig.worktracks.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.time.DayOfWeek.*;

public class ChronoFormatter {

    public static Duration parseDuration(String input) {
        LocalTime time = parseTime(input);
        return Duration.between(LocalTime.of(0, 0), time);
    }

    public static String formatDuration(Duration duration) {
        final long minutes = duration.abs().toMinutes();
        String formatString = "%02d:%02d";
        if (duration.isNegative())
            formatString = "-" + formatString;
        return String.format(Locale.getDefault(), formatString, minutes / 60, minutes % 60);
    }

    public static LocalTime parseTime(String input) {
        return LocalTime.parse(input);
    }

    public static String formatTime(LocalTime time) {
        return time.toString();
    }

    public static String formatWeek(Week week) {
        return String.format(Locale.getDefault(), "%04d-%02d", week.getYear(), week.getWeek());
    }

    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }

    public static String formatDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Mo";
            case TUESDAY:
                return "Di";
            case WEDNESDAY:
                return "Mi";
            case THURSDAY:
                return "Do";
            case FRIDAY:
                return "Fr";
            case SATURDAY:
                return "Sa";
            case SUNDAY:
                return "So";
            default:
                throw new IllegalStateException("Unexpected value: " + dayOfWeek);
        }
    }
}
