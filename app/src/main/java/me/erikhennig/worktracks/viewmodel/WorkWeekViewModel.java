package me.erikhennig.worktracks.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.List;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.WorkTracksApp;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.Week;

public class WorkWeekViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final MediatorLiveData<List<IWorkTime>> data = new MediatorLiveData<>();

    private Week week;
    private LiveData<List<IWorkTime>> t;

    public WorkWeekViewModel(@NonNull Application application) {
        super(application);
        WorkTracksApp app = (WorkTracksApp) application;
        this.repository = app.getRepository();
        this.setWeek(Week.now());
    }

    public LiveData<List<IWorkTime>> getWorkTimes() {
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
        if (this.t != null) {
            this.data.removeSource(this.t);
        }
        this.t = this.repository.getWorkTimes(week);
        List<IWorkTime> l = this.t.getValue();
        this.data.addSource(this.t, this.data::setValue);
    }

    public Week getWeek() {
        return this.week;
    }
}
