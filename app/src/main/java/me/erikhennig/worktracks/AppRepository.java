package me.erikhennig.worktracks;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import me.erikhennig.worktracks.db.AppDatabase;
import me.erikhennig.worktracks.db.WorkTimeEntity;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.IWorkTime;

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

    public List<IWorkTime> getAllWorkTimes() {
        Future<List<IWorkTime>> f = AppDatabase.databaseExecutor.submit(() -> (List<IWorkTime>) (Object) this.database.workTimeDao().getAllWorkTimes());
        try {
            return f.get();
        } catch (ExecutionException | InterruptedException e) {
            // TODO add logging?
        }
        return new ArrayList<>();
    }

    public LiveData<List<IWorkTime>> getWorkTimes(Week week) {
        List<LocalDate> daysOfWeek = week.getDates();
        return (LiveData<List<IWorkTime>>) (Object) this.database.workTimeDao().getWorkTimes(daysOfWeek.toArray(new LocalDate[1]));
    }

    public IWorkTime getWorkTime(@NonNull LocalDate date) {
        Future<IWorkTime> f = AppDatabase.databaseExecutor.submit(() -> this.database.workTimeDao().getWorkTime(date));
        try {
            return f.get();
        } catch (ExecutionException | InterruptedException e) {
            // TODO add logging?
        }
        return null;
    }

    public void insert(IWorkTime... workTimes) {
        AppDatabase.databaseExecutor.execute(() -> {
            WorkTimeEntity[] entities = Arrays.stream(workTimes).map(WorkTimeEntity::new).toArray(WorkTimeEntity[]::new);
            this.database.workTimeDao().insert(entities);
        });
    }

    public void delete(@NonNull IWorkTime workTime) {
        AppDatabase.databaseExecutor.execute(() -> {
            WorkTimeEntity entity = new WorkTimeEntity(workTime);
            this.database.workTimeDao().delete(entity);
        });
    }

    public void update(@NonNull IWorkTime workTime) {
        AppDatabase.databaseExecutor.execute(() -> {
            WorkTimeEntity entity = new WorkTimeEntity(workTime);
            this.database.workTimeDao().update(entity);
        });
    }
}
