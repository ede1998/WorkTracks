package me.erikhennig.worktracks.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public interface IWorkTime {
    int getId();

    LocalDate getDate();

    boolean getIgnore();

    LocalTime getStartingTime();

    LocalTime getEndingTime();

    Duration getBreakDuration();

    String getComment();
}
