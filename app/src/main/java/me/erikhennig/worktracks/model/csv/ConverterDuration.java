package me.erikhennig.worktracks.model.csv;

import com.opencsv.bean.AbstractBeanField;

import java.time.Duration;

import me.erikhennig.worktracks.model.ChronoFormatter;

public class ConverterDuration extends AbstractBeanField {
    @Override
    protected Object convert(String value) {
        return ChronoFormatter.parseDuration(value);
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value instanceof Duration) {
            Duration d = (Duration) value;
            return ChronoFormatter.formatDuration(d);
        }
        return "";
    }
}
