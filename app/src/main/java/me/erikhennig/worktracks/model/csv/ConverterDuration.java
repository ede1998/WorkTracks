package me.erikhennig.worktracks.model.csv;

import com.opencsv.bean.AbstractBeanField;

import java.time.Duration;

import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;

public class ConverterDuration extends AbstractBeanField {
    private ChronoFormatter chronoFormatter = ChronoFormatter.getInstance();

    @Override
    protected Object convert(String value) {
        return this.chronoFormatter.parseDuration(value);
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value instanceof Duration) {
            Duration d = (Duration) value;
            return this.chronoFormatter.formatDurationWithMinutes(d);
        }
        return "";
    }
}
