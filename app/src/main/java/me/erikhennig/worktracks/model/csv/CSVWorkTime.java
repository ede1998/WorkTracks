package me.erikhennig.worktracks.model.csv;

import android.util.Log;

import androidx.annotation.NonNull;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.Reader;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import me.erikhennig.worktracks.model.IWorkTime;

public class CSVWorkTime implements IWorkTime {

    private static final String TAG = CSVWorkTime.class.getName();

    @CsvBindByName(required = true)
    @CsvDate("yyyy-MM-dd")
    private LocalDate date;

    @CsvBindByName
    private boolean ignore;

    @CsvBindByName(required = true)
    @CsvDate("HH:mm")
    private LocalTime startingTime;

    @CsvBindByName(required = true)
    @CsvDate("HH:mm")
    private LocalTime endingTime;

    @CsvCustomBindByName(converter = ConverterDuration.class, required = true)
    private Duration breakDuration;

    @CsvBindByName
    private String comment;

    public CSVWorkTime() {
        this.ignore = false;
    }

    public CSVWorkTime(@NonNull IWorkTime workTime) {
        this.date = workTime.getDate();
        this.ignore = workTime.getIgnore();
        this.startingTime = workTime.getStartingTime();
        this.endingTime = workTime.getEndingTime();
        this.breakDuration = workTime.getBreakDuration();
        this.comment = workTime.getComment();
    }

    public static Stream<CSVWorkTime> read(Reader reader) {
        return new CsvToBeanBuilder<CSVWorkTime>(reader)
                .withType(CSVWorkTime.class)
                .withFilter(CSVWorkTime::isLineEmpty)
                .build()
                .stream();
    }

    private static boolean isLineEmpty(String[] line) {
        return Arrays.stream(line).anyMatch(x -> x != null && !x.isEmpty());
    }

    public static boolean write(Writer writer, List<IWorkTime> workTimes) {
        Stream<CSVWorkTime> wts = workTimes.stream().map(CSVWorkTime::new);

        StatefulBeanToCsv<CSVWorkTime> beanToCsv = new StatefulBeanToCsvBuilder<CSVWorkTime>(writer).build();
        try {
            beanToCsv.write(wts);
            return true;
        } catch (CsvDataTypeMismatchException e) {
            Log.e(TAG, String.format("Type mismatch. [%s]", e.getMessage()));
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            Log.e(TAG, String.format("Failed to write work time to file. Field [%s] was empty. Message: [%s]", e.getDestinationField(), e.getMessage()));
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getId() {
        return -1;
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

    @NonNull
    @Override
    public String toString() {
        return String.format("CSVWorkTime(Date: [%s], Ignore: [%s], Start: [%s], End: [%s], Break: [%s], Comment: [%s])",
                this.date,
                this.ignore,
                this.startingTime,
                this.endingTime,
                this.breakDuration,
                this.comment
        );
    }

}
