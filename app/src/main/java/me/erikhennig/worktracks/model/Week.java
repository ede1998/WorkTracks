package me.erikhennig.worktracks.model;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Week {
    private LocalDate firstDayOfWeek;
    private static WeekFields WeekFields = java.time.temporal.WeekFields.of(Locale.getDefault());

    public static Week now() {
        Week w =  new Week();
        w.firstDayOfWeek = LocalDate.now().with(WeekFields.dayOfWeek(), 1);
        return w;
    }

    public static Week of(int year, int weekOfYear) {
        Week w = new Week();
        w.firstDayOfWeek = LocalDate.of(year, 1, 1)
                .with(WeekFields.weekOfYear(), weekOfYear)
                .with(WeekFields.dayOfWeek(), 1);
        return w;
    }

    private Week() {
    }

    public List<LocalDate> getDates() {
        return IntStream.rangeClosed(0, 6)
                .mapToObj(x -> this.firstDayOfWeek.plusDays(x))
                .collect(Collectors.toList());
    }

    int getWeek() {
        return this.firstDayOfWeek.get(WeekFields.weekOfYear());
    }

    int getYear() {
        return this.firstDayOfWeek.getYear();
    }
}
