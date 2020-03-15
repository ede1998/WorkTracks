package me.erikhennig.worktracks;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import me.erikhennig.worktracks.db.AppDatabase;
import me.erikhennig.worktracks.db.WorkTimeEntity;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.WorkTime;

public class AppRepository {
    private static AppRepository INSTANCE;

    private final AppDatabase database;

    private AppRepository(final AppDatabase database) {
        this.database = database;
    }

    public static AppRepository getInstance(final AppDatabase database) {
        if (INSTANCE == null) {
            synchronized (AppRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppRepository(database);
                }
            }
        }
        return INSTANCE;
    }

    public List<WorkTimeEntity> getAllWorkTimes() {
        return this.database.workTimeDao().getAllWorkTimes();
    }

    public LiveData<List<WorkTimeEntity>> getWorkTimes(LocalDate dayInWeek) {
        List<LocalDate> daysOfWeek = new Week(dayInWeek).getDates();
        return this.database.workTimeDao().getWorkTimes(daysOfWeek.toArray(new LocalDate[1]));
    }

    public WorkTime getWorkTime(@NonNull LocalDate date) {
        return this.database.workTimeDao().getWorkTime(date);
    }

    public void insert(WorkTime... workTimes) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            WorkTimeEntity[] entities = Arrays.stream(workTimes).map(WorkTimeEntity::new).toArray(WorkTimeEntity[]::new);
            this.database.workTimeDao().insert(entities);
        });
    }

    public void delete(@NonNull WorkTime workTime) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            WorkTimeEntity entity = new WorkTimeEntity(workTime);
            this.database.workTimeDao().delete(entity);
        });
    }

    public void update(@NonNull WorkTime workTime) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            WorkTimeEntity entity = new WorkTimeEntity(workTime);
            this.database.workTimeDao().update(entity);
        });
    }
}
