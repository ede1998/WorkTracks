package me.erikhennig.worktracks.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Locale;

public class TimeDurationFormatter {

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
}
