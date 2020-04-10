package me.erikhennig.worktracks.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;

public class PreferenceUtils {

    public static final String WEEKLY_WORK_DURATION = "weekly_work_duration";
    public static final String DURATION_AS_MINUTES = "duration_as_minutes";
    public static final String RESET = "reset";

    private static OnChangeDurationDisplay onChangeDurationDisplay;
    private static SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, key) -> {
        if (onChangeDurationDisplay != null && DURATION_AS_MINUTES.equals(key)) {
            boolean showMinutes = sharedPreferences.getBoolean(DURATION_AS_MINUTES, true);
            onChangeDurationDisplay.onChangeDurationDisplay(createDurationDisplay(showMinutes));
        }
    };
    private static ChronoFormatter chronoFormatter = ChronoFormatter.getInstance();

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

    public static Duration getWeeklyWorkDuration(Context context) {
        String duration = getSharedPreferences(context).getString(WEEKLY_WORK_DURATION, null);
        if (duration == null) return null;
        try {
            return chronoFormatter.parseDuration(duration);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public enum DurationDisplay {
        WITH_MINUTES,
        AS_DECIMAL
    }

    private static DurationDisplay createDurationDisplay(boolean withMinutes) {
        return withMinutes ? DurationDisplay.WITH_MINUTES : DurationDisplay.AS_DECIMAL;
    }

    @FunctionalInterface
    public interface OnChangeDurationDisplay {
        void onChangeDurationDisplay(DurationDisplay displayOption);
    }

    public static void setOnChangeDurationDisplay(OnChangeDurationDisplay callback) {
        onChangeDurationDisplay = callback;
    }

    public static DurationDisplay getDurationDisplay(Context context) {
        boolean showMinutes = getSharedPreferences(context).getBoolean(DURATION_AS_MINUTES, true);
        return createDurationDisplay(showMinutes);
    }

}
