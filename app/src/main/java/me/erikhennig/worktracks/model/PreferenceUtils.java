package me.erikhennig.worktracks.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;

public class PreferenceUtils {

    public static final String WEEKLY_WORK_DURATION = "weekly_work_duration";
    public static final String WORKING_DAYS = "working_days";
    public static final String DURATION_AS_MINUTES = "duration_as_minutes";
    public static final String RESET = "reset";

    private static ChronoFormatter chronoFormatter = ChronoFormatter.getInstance();

    private static OnChangeDurationDisplay onChangeDurationDisplay;
    private static OnChangeWeeklyWorkDuration onChangeWeeklyWorkDuration;
    private static OnChangeWorkingDays onChangeWorkingDays;

    private static SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, key) -> {
        switch (key) {
            case WEEKLY_WORK_DURATION:
                if (onChangeWeeklyWorkDuration != null) {
                    onChangeWeeklyWorkDuration.onChangeWeeklyWorkDuration(getWeeklyWorkDuration(sharedPreferences));
                }
                break;
            case DURATION_AS_MINUTES:
                if (onChangeDurationDisplay != null) {
                    onChangeDurationDisplay.onChangeDurationDisplay(getDurationDisplay(sharedPreferences));
                }
                break;
            case WORKING_DAYS:
                if (onChangeWorkingDays != null) {
                    onChangeWorkingDays.onChangeWorkingDays(getWorkingDays(sharedPreferences));
                }
                break;
        }
    };

    private PreferenceUtils() {
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void register(Context context) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public static void unregister(Context context) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @FunctionalInterface
    public interface OnChangeWeeklyWorkDuration {
        void onChangeWeeklyWorkDuration(Duration duration);
    }

    public static Duration getWeeklyWorkDuration(Context context) {
        return getWeeklyWorkDuration(getSharedPreferences(context));
    }

    private static Duration getWeeklyWorkDuration(SharedPreferences preferences) {
        String duration = preferences.getString(WEEKLY_WORK_DURATION, "37:00");
        try {
            return chronoFormatter.parseDuration(duration);
        } catch (DateTimeParseException e) {
            return Duration.ofHours(37);
        }
    }

    public static void setOnChangeWeeklyWorkDuration(OnChangeWeeklyWorkDuration callback) {
        onChangeWeeklyWorkDuration = callback;
    }

    public enum DurationDisplay {
        WITH_MINUTES,
        AS_DECIMAL
    }

    @FunctionalInterface
    public interface OnChangeDurationDisplay {
        void onChangeDurationDisplay(DurationDisplay displayOption);
    }

    public static void setOnChangeDurationDisplay(OnChangeDurationDisplay callback) {
        onChangeDurationDisplay = callback;
    }

    public static DurationDisplay getDurationDisplay(Context context) {
        return getDurationDisplay(getSharedPreferences(context));
    }

    private static DurationDisplay getDurationDisplay(SharedPreferences preferences) {
        boolean showMinutes = preferences.getBoolean(DURATION_AS_MINUTES, true);
        return showMinutes ? DurationDisplay.WITH_MINUTES : DurationDisplay.AS_DECIMAL;
    }

    @FunctionalInterface
    public interface OnChangeWorkingDays {
        void onChangeWorkingDays(Set<DayOfWeek> workingDays);
    }

    public static void setOnChangeWorkingDays(OnChangeWorkingDays callback) {
        onChangeWorkingDays = callback;
    }

    public static Set<DayOfWeek> getWorkingDays(Context context) {
        return getWorkingDays(getSharedPreferences(context));
    }

    private static Set<DayOfWeek> getWorkingDays(SharedPreferences preferences) {
        Set<String> defaultDays = Stream.of(1,2,3,4,5).map(Object::toString).collect(Collectors.toSet());
        Set<String> p = preferences.getStringSet(WORKING_DAYS, defaultDays);
        return p.stream()
                .map(Integer::parseInt)
                .map(DayOfWeek::of)
                .collect(Collectors.toSet());
    }
}
