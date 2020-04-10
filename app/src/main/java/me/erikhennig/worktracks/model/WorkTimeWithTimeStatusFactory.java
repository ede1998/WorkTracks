package me.erikhennig.worktracks.model;

import android.util.Log;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkTimeWithTimeStatusFactory {

    private static final String TAG = WorkTimeWithTimeStatusFactory.class.getName();

    private final WorkTimeConfiguration configuration;

    public WorkTimeWithTimeStatusFactory(WorkTimeConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<WorkTimeWithTimeStatus> create(List<IWorkTime> workTimes) {
        List<WorkTimeWithTimeStatus> sortedWorkTimes = workTimes.stream()
                .sorted(Comparator.comparing(IWorkTime::getDate))
                .map(WorkTimeWithTimeStatus::new)
                .peek(WorkTimeWithTimeStatusFactory::calculateDayDuration)
                .peek(this::calculateDayDifference)
                .collect(Collectors.toList());

        computeAccumulatedDifferences(sortedWorkTimes);

        return sortedWorkTimes;
    }

    private static void computeAccumulatedDifferences(List<WorkTimeWithTimeStatus> sortedWorkTimes) {
        sortedWorkTimes.stream().filter(ReadOnlyWorkTime::getIgnore).forEach(x -> x.setAccumulatedDifference(Duration.ZERO));

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

            workTime.setAccumulatedDifference(accumulatedDifference);
        }
    }

    private static void calculateDayDuration(WorkTimeWithTimeStatus workTime) {
        if (!WorkTimeValidator.validate(workTime)) return;

        LocalTime startingTime = workTime.getStartingTime();
        LocalTime endingTime = workTime.getEndingTime();
        Duration breakDuration = workTime.getBreakDuration();

        Duration totalDuration = Duration.between(startingTime, endingTime);
        Duration withBreak = totalDuration.minus(breakDuration);

        Log.d(TAG, String.format("Duration of [%s] calculated for work time [%s]", withBreak, workTime));

        workTime.setDuration(withBreak);
    }

    private void calculateDayDifference(WorkTimeWithTimeStatus workTime) {
        boolean isUsualWorkDay = this.configuration.isWorkDay(workTime.getDate().getDayOfWeek());
        Duration expectedDuration = isUsualWorkDay ? this.configuration.getDayDuration(): Duration.ZERO;
        workTime.setDifference(workTime.getDuration().minus(expectedDuration));
    }


}
