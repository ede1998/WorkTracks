package me.erikhennig.worktracks.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkTime extends ReadOnlyWorkTime {

    public WorkTime() {
        super();
    }

    public WorkTime(LocalDate date) {
        super();
        this.date = date;
    }

    public WorkTime(LocalDate date, boolean ignore, LocalTime startingTime, LocalTime endingTime, Duration breakDuration, String comment) {
        super(date,ignore,startingTime,endingTime,breakDuration,comment);
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
