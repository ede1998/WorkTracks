package me.erikhennig.worktracks.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

class ReadOnlyWorkTime implements IWorkTime {
    protected int id;
    protected LocalDate date;
    protected boolean ignore;
    protected LocalTime startingTime;
    protected LocalTime endingTime;
    protected Duration breakDuration;
    protected String comment;

    public ReadOnlyWorkTime(LocalDate date, boolean ignore, LocalTime startingTime, LocalTime endingTime, Duration breakDuration, String comment) {
        this.id = -1;
        this.date = date;
        this.ignore = ignore;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.breakDuration = breakDuration;
        this.comment = comment;
    }

    public ReadOnlyWorkTime(IWorkTime workTime) {
        this.id = workTime.getId();
        this.date = workTime.getDate();
        this.ignore = workTime.getIgnore();
        this.startingTime = workTime.getStartingTime();
        this.endingTime = workTime.getEndingTime();
        this.breakDuration = workTime.getBreakDuration();
        this.comment = workTime.getComment();
    }

    protected ReadOnlyWorkTime() {

    }

    public boolean validate() {
        return this.date != null && this.startingTime != null && this.endingTime != null && this.breakDuration != null;
    }

    public Duration getWorkingDuration() {
        if (!this.validate()) {
            return null;
        }
        Duration totalDuration = Duration.between(this.startingTime, this.endingTime);
        Duration withBreak = totalDuration.minus(this.breakDuration);

        return withBreak;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "WorkTime(Id: [%d], Date: [%s], Ignore: [%s], Start: [%s], End: [%s], Break: [%s], Comment: [%s])",
                this.id,
                this.date,
                this.ignore,
                this.startingTime,
                this.endingTime,
                this.breakDuration,
                this.comment
        );
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public boolean getIgnore() {
        return this.ignore;
    }

    @Override
    public LocalTime getStartingTime() {
        return this.startingTime;
    }

    @Override
    public LocalTime getEndingTime() {
        return this.endingTime;
    }

    @Override
    public Duration getBreakDuration() {
        return this.breakDuration;
    }

    @Override
    public String getComment() {
        return this.comment;
    }
}
