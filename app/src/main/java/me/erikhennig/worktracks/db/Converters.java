package me.erikhennig.worktracks.db;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Converters {
    @TypeConverter
    public static LocalDate toLocalDate(long epochDay)
    {
        return LocalDate.ofEpochDay(epochDay);
    }

    @TypeConverter
    public static long toEpochDay(@NonNull LocalDate date)
    {
        return date.toEpochDay();
    }

    @TypeConverter
    public static LocalTime toLocalTime(long secondOfDay)
    {
        return LocalTime.ofSecondOfDay(secondOfDay);
    }

    @TypeConverter
    public static long toSecondOfDay(@NonNull LocalTime time)
    {
        return time.toSecondOfDay();
    }

    @TypeConverter
    public static Duration toDuration(long seconds)
    {
        return Duration.ofSeconds(seconds);
    }

    @TypeConverter
    public static long toSeconds(@NonNull Duration duration)
    {
        return duration.getSeconds();
    }
}
