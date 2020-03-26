package me.erikhennig.worktracks.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import me.erikhennig.worktracks.model.IWorkTime;

@Entity(tableName = "work_time", indices = {@Index(value = {"date"}, unique = true)})
public class WorkTimeEntity implements IWorkTime {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private LocalDate date;

    private boolean ignore;

    @NonNull
    private LocalTime startingTime;

    @NonNull
    private LocalTime endingTime;

    @NonNull
    private Duration breakDuration;

    private String comment;

    public WorkTimeEntity(int id, @NonNull LocalDate date, boolean ignore, @NonNull LocalTime startingTime, @NonNull LocalTime endingTime, @NonNull Duration breakDuration, String comment) {
        this.id = id;
        this.date = date;
        this.ignore = ignore;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.breakDuration = breakDuration;
        this.comment = comment;

    }
    @Ignore
    public WorkTimeEntity(@NonNull IWorkTime workTime)
    {
        this.date = workTime.getDate();
        this.ignore = workTime.getIgnore();
        this.startingTime = workTime.getStartingTime();
        this.endingTime = workTime.getEndingTime();
        this.breakDuration = workTime.getBreakDuration();
        this.comment = workTime.getComment();
    }

    @Ignore
    public WorkTimeEntity(int id, @NonNull IWorkTime workTime) {
        this(workTime);
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @NonNull
    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public boolean getIgnore() {
        return this.ignore;
    }

    @NonNull
    @Override
    public LocalTime getStartingTime() {
        return this.startingTime;
    }

    @NonNull
    @Override
    public LocalTime getEndingTime() {
        return this.endingTime;
    }

    @NonNull
    @Override
    public Duration getBreakDuration() {
        return this.breakDuration;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

}
