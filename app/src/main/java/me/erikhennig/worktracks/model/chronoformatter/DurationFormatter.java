package me.erikhennig.worktracks.model.chronoformatter;

import java.time.Duration;
import java.util.Locale;

interface DurationFormatter {
    String formatDuration(Duration duration);
}

class DurationWithMinutesFormatter implements DurationFormatter {
    @Override
    public String formatDuration(Duration duration) {
        final long minutes = duration.abs().toMinutes();
        String formatString = "%02d:%02d";
        if (duration.isNegative())
            formatString = "-" + formatString;
        return String.format(Locale.getDefault(), formatString, minutes / 60, minutes % 60);
    }
}

class DurationAsDecimalFormatter implements DurationFormatter {

    @Override
    public String formatDuration(Duration duration) {
        double hours = (double) duration.toMinutes() / Duration.ofHours(1).toMinutes();
        return String.format(Locale.getDefault(), "%.2f", hours);
    }
}