package me.erikhennig.worktracks.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.List;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.db.WorkTimeEntity;
import me.erikhennig.worktracks.model.WorkTime;

public class WorkTimeViewModel extends AndroidViewModel {

    private AppRepository repository;

    public WorkTimeViewModel(@NonNull Application application, @NonNull AppRepository repository) {
        super(application);
        this.repository = repository;
    }

    public List<WorkTimeEntity> getAllWorkTimes() {
        return this.repository.getAllWorkTimes();
    }

    public LiveData<List<WorkTimeEntity>> getWorkTimes(LocalDate dayInWeek) {
        return this.repository.getWorkTimes(dayInWeek);
    }

    public WorkTime getWorkTime(@NonNull LocalDate date) {
        return this.repository.getWorkTime(date);
    }

    public void insert(WorkTime... workTimes) {
        this.repository.insert(workTimes);
    }

    public void delete(@NonNull WorkTime workTime) {
        this.repository.delete(workTime);
    }

    public void update(@NonNull WorkTime workTime) {
        this.repository.update(workTime);
    }
}
