package me.erikhennig.worktracks.model;

import android.content.Context;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Set;

public class WorkTimeConfiguration {

    private final Duration weekDuration;
    private final Set<DayOfWeek> workDays;

    public static WorkTimeConfiguration fromPreferences(Context context) {
        Duration weekDuration = PreferenceUtils.getWeeklyWorkDuration(context);
        Set<DayOfWeek> workingDays = PreferenceUtils.getWorkingDays(context);

        return new WorkTimeConfiguration(weekDuration, workingDays);
    }

    private WorkTimeConfiguration(Duration weekDuration, Set<DayOfWeek> workDays) {
        this.weekDuration = weekDuration;
        this.workDays = workDays;
    }

    public Duration getWeekDuration() {
        return this.weekDuration;
    }

    public Set<DayOfWeek> getWorkDays() {
        return this.workDays;
    }

    public boolean isWorkDay(DayOfWeek day) {
        return this.workDays.contains(day);
    }

    public Duration getDayDuration() {
        return this.weekDuration.dividedBy(this.workDays.size());
    }
}
