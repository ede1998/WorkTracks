package me.erikhennig.worktracks.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface WorkTimeDao {
    @Query("SELECT * FROM WorkTimeEntity")
    List<WorkTimeEntity> getAllWorkTimes();

    @Query("SELECT * FROM WorkTimeEntity WHERE date IN (:dates)")
    LiveData<List<WorkTimeEntity>> getWorkTimes(@NonNull LocalDate[] dates);

    @Query("SELECT * FROM user WHERE date = :date")
    WorkTimeEntity getWorkTime(@NonNull LocalDate date);

    @Insert
    void insert(@NonNull WorkTimeEntity... workTimes);

    @Delete
    void delete(@NonNull WorkTimeEntity workTime);

    @Update
    void update(@NonNull WorkTimeEntity workTime);
}
