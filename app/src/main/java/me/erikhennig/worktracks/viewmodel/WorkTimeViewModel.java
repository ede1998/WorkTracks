package me.erikhennig.worktracks.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.List;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.WorkTracksApp;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.IWorkTime;

public class WorkTimeViewModel extends AndroidViewModel {

    private AppRepository repository;

    public WorkTimeViewModel(@NonNull Application application) {
        super(application);
        WorkTracksApp app = (WorkTracksApp) application;
        this.repository = app.getRepository();
    }

    public List<IWorkTime> getAllWorkTimes() {
        return this.repository.getAllWorkTimes();
    }

    public LiveData<List<IWorkTime>> getWorkTimes(Week week) {
        return this.repository.getWorkTimes(week);
    }

    public IWorkTime getWorkTime(@NonNull LocalDate date) {
        return this.repository.getWorkTime(date);
    }

    public void insertOrUpdate(IWorkTime workTime) {
        IWorkTime wt = this.repository.getWorkTime(workTime.getDate());
        if (wt != null)
        {
            this.repository.update(workTime);
        }
        else
        {
            this.repository.insert(workTime);
        }
    }

    public void delete(@NonNull IWorkTime workTime) {
        this.repository.delete(workTime);
    }
}
