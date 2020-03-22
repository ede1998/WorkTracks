package me.erikhennig.worktracks.db;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Converters {
    @NonNull
    @TypeConverter
    public static LocalDate toLocalDate(@NonNull String date)
    {
        return LocalDate.parse(date);
    }

    @NonNull
    @TypeConverter
    public static String toISO8601Date(@NonNull LocalDate date)
    {
        return date.toString();
    }

    @NonNull
    @TypeConverter
    public static LocalTime toLocalTime(@NonNull String time)
    {
        return LocalTime.parse(time);
    }

    @NonNull
    @TypeConverter
    public static String toISO8601Time(@NonNull LocalTime time)
    {
        return time.toString();
    }

    @NonNull
    @TypeConverter
    public static Duration toDuration(@NonNull String duration)
    {
        return Duration.parse(duration);
    }

    @NonNull
    @TypeConverter
    public static String toISO8601Duration(@NonNull Duration duration)
    {
        return duration.toString();
    }
}
