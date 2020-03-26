package me.erikhennig.worktracks.model;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkTimeWithTimeStatus extends ReadOnlyWorkTime {

    private static final String TAG = WorkTimeWithTimeStatus.class.getName();

    // TODO extract as parameter
    private static final Duration WEEK_DURATION = Duration.ofHours(37);
    private static final List<DayOfWeek> WORK_DAYS = Collections.unmodifiableList(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
    private static final Duration DAY_DURATION = WEEK_DURATION.dividedBy(WORK_DAYS.size());

    private Duration duration;
    private Duration difference;
    private Duration accumulatedDifference;

    private WorkTimeWithTimeStatus(IWorkTime workTime) {
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

    public static List<WorkTimeWithTimeStatus> create(List<IWorkTime> workTimes) {
        List<WorkTimeWithTimeStatus> sortedWorkTimes = workTimes.stream()
                .sorted(Comparator.comparing(IWorkTime::getDate))
                .map(WorkTimeWithTimeStatus::new)
                .peek(x -> x.duration = calculateDayDuration(x))
                .collect(Collectors.toList());

        sortedWorkTimes.forEach(x -> x.difference = calculateDayDifference(x));
        sortedWorkTimes.stream().filter(ReadOnlyWorkTime::getIgnore).forEach(x -> x.accumulatedDifference = Duration.ZERO);

        List<WorkTimeWithTimeStatus> notIgnoredWorkTimes = sortedWorkTimes.stream().filter(x -> !x.getIgnore()).collect(Collectors.toList());

        Stream<Duration> differences = notIgnoredWorkTimes.stream().map(WorkTimeWithTimeStatus::getDifference);
        Iterator<Duration> accumulatingIterator = new AccumulatingIterator<>(differences, Duration.ZERO, Duration::plus);
        ArrayList<Duration> accumulatedDifferences = new ArrayList<>();
        accumulatingIterator.forEachRemaining(accumulatedDifferences::add);

        Iterator<Duration> accumulatedDifferenceIterator = accumulatedDifferences.iterator();
        Iterator<WorkTimeWithTimeStatus> workTimeIterator = notIgnoredWorkTimes.iterator();
        while (accumulatedDifferenceIterator.hasNext() && workTimeIterator.hasNext()) {
            WorkTimeWithTimeStatus workTime = workTimeIterator.next();
            Duration accumulatedDifference = accumulatedDifferenceIterator.next();

            Log.d(TAG, String.format("Accumulated difference of [%s] calculated for work time [%s]", accumulatedDifference, workTime));

            workTime.accumulatedDifference = accumulatedDifference;
        }

        return sortedWorkTimes;
    }

    private static Duration calculateDayDuration(IWorkTime workTime) {
        LocalTime startingTime = workTime.getStartingTime();
        LocalTime endingTime = workTime.getEndingTime();
        Duration breakDuration = workTime.getBreakDuration();

        if (startingTime == null || endingTime == null || breakDuration == null) {
            return null;
        }

        Duration totalDuration = Duration.between(startingTime, endingTime);
        Duration withBreak = totalDuration.minus(breakDuration);

        Log.d(TAG, String.format("Duration of [%s] calculated for work time [%s]", withBreak, workTime));

        return withBreak;
    }

    private static Duration calculateDayDifference(WorkTimeWithTimeStatus workTime) {
        boolean isUsualWorkDay = WORK_DAYS.contains(workTime.getDate().getDayOfWeek());
        return isUsualWorkDay? workTime.getDuration().minus(DAY_DURATION) : workTime.getDuration();
    }
}
