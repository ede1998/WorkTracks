package me.erikhennig.worktracks.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.WorkTracksApp;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.WorkTimeConfiguration;
import me.erikhennig.worktracks.model.WorkTimeWithTimeStatus;
import me.erikhennig.worktracks.model.WorkTimeWithTimeStatusFactory;

public class WorkWeekViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final MediatorLiveData<List<WorkTimeWithTimeStatus>> data = new MediatorLiveData<>();

    private Week week;
    private LiveData<List<IWorkTime>> workTimes;
    private WorkTimeWithTimeStatusFactory factory;

    public WorkWeekViewModel(@NonNull Application application) {
        super(application);
        WorkTracksApp app = (WorkTracksApp) application;
        this.repository = app.getRepository();
        this.setWeek(Week.now());
    }

    public LiveData<List<WorkTimeWithTimeStatus>> getWorkTimes() {
        return this.data;
    }

    public void increaseWeek() {
        this.setWeek(this.week.plus(1));
    }

    public void decreaseWeek() {
        this.setWeek(this.week.minus(1));
    }

    public void setWeek(Week week) {
        this.week = week;
        if (this.workTimes != null) {
            this.data.removeSource(this.workTimes);
        }
        this.workTimes = this.repository.getWorkTimes(week);

        this.data.addSource(this.workTimes, this::updateData);
    }

    private void updateData(List<IWorkTime> workTimes) {
        if (this.factory == null) return;
        List<WorkTimeWithTimeStatus> list = this.factory.create(workTimes);
        this.data.setValue(list);
    }

    public Week getWeek() {
        return this.week;
    }

    public void updateConfiguration(WorkTimeConfiguration configuration) {
        this.factory = new WorkTimeWithTimeStatusFactory(configuration);
    }
}
