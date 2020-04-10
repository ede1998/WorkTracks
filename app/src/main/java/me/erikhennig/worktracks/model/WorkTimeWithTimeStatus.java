package me.erikhennig.worktracks.model;

import java.time.Duration;

public class WorkTimeWithTimeStatus extends ReadOnlyWorkTime {

    private Duration duration;
    private Duration difference;
    private Duration accumulatedDifference;

    WorkTimeWithTimeStatus(IWorkTime workTime) {
        this.id = workTime.getId();
        this.date = workTime.getDate();
        this.ignore = workTime.getIgnore();
        this.startingTime = workTime.getStartingTime();
        this.endingTime = workTime.getEndingTime();
        this.breakDuration = workTime.getBreakDuration();
        this.comment = workTime.getComment();
    }

    public Duration getDuration() {
        return this.duration;
    }

    public Duration getAccumulatedDifference() {
        return accumulatedDifference;
    }

    public Duration getDifference() {
        return difference;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    void setDifference(Duration difference) {
        this.difference = difference;
    }

    void setAccumulatedDifference(Duration accumulatedDifference) {
        this.accumulatedDifference = accumulatedDifference;
    }
}
