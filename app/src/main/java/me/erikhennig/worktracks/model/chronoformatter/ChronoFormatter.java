package me.erikhennig.worktracks.model.chronoformatter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;

import me.erikhennig.worktracks.model.PreferenceUtils;
import me.erikhennig.worktracks.model.Week;

public class ChronoFormatter implements PreferenceUtils.OnChangeDurationDisplay {

    private static ChronoFormatter INSTANCE;

    private ChronoFormatter() {
    }

    public static ChronoFormatter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChronoFormatter();
        }
        return INSTANCE;
    }

    private DurationFormatter durationFormatter = new DurationWithMinutesFormatter();

    public Duration parseDuration(String input) {
        LocalTime time = parseTime(input);
        return Duration.between(LocalTime.of(0, 0), time);
    }

    public String formatDuration(Duration duration) {
        return this.durationFormatter.formatDuration(duration);
    }

    public String formatDurationWithMinutes(Duration duration) {
        return new DurationWithMinutesFormatter().formatDuration(duration);
    }

    public LocalTime parseTime(String input) {
        return LocalTime.parse(input);
    }

    public String formatTime(LocalTime time) {
        return time.toString();
    }

    public String formatWeek(Week week) {
        return String.format(Locale.getDefault(), "%04d-%02d", week.getYear(), week.getWeek());
    }

    public String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }

    public String formatDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE,  Locale.getDefault()).substring(0,2);
    }

    @Override
    public void onChangeDurationDisplay(PreferenceUtils.DurationDisplay displayOption) {
        this.setDurationDisplay(displayOption);
    }

    public void setDurationDisplay(PreferenceUtils.DurationDisplay displayOption) {
        switch (displayOption) {
            case AS_DECIMAL:
                this.durationFormatter = new DurationAsDecimalFormatter();
                break;
            case WITH_MINUTES:
                this.durationFormatter = new DurationWithMinutesFormatter();
                break;
        }
    }
}
