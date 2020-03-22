package me.erikhennig.worktracks.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkTime implements IWorkTime {
    private int id;
    private LocalDate date;
    private boolean ignore;
    private LocalTime startingTime;
    private LocalTime endingTime;
    private Duration breakDuration;
    private String comment;

    public WorkTime() {

    }

    public WorkTime(LocalDate date) {
        this.date = date;
    }

    public WorkTime(LocalDate date, boolean ignore, LocalTime startingTime, LocalTime endingTime, Duration breakDuration, String comment) {
        this.id = -1;
        this.date = date;
        this.ignore = ignore;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.breakDuration = breakDuration;
        this.comment = comment;
    }

    public WorkTime(IWorkTime workTime) {
        this.id = workTime.getId();
        this.date = workTime.getDate();
        this.ignore = workTime.getIgnore();
        this.startingTime = workTime.getStartingTime();
        this.endingTime = workTime.getEndingTime();
        this.breakDuration = workTime.getBreakDuration();
        this.comment = workTime.getComment();
    }

    public boolean validate() {
        return this.date != null && this.startingTime != null && this.endingTime != null && this.breakDuration != null;
    }

    public Duration getWorkingDuration() {
        if (this.startingTime == null || this.endingTime == null || this.breakDuration == null) {
            return null;
        }
        Duration totalDuration = Duration.between(this.startingTime, this.endingTime);
        Duration withBreak = totalDuration.minus(this.breakDuration);

        return withBreak;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndingTime(LocalTime endingTime) {
        this.endingTime = endingTime;
    }

    public void setBreakDuration(Duration breakDuration) {
        this.breakDuration = breakDuration;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
