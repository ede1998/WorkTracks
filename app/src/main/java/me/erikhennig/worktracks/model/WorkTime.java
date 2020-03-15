package me.erikhennig.worktracks.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public interface WorkTime {
    int getId();

    LocalDate getDate();

    boolean getIgnore();

    LocalTime getStartingTime();

    LocalTime getEndingTime();

    Duration getBreakDuration();
}
