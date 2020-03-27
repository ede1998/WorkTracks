package me.erikhennig.worktracks.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.WorkTracksApp;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.WorkTime;
import me.erikhennig.worktracks.model.WorkTimeValidator;

public class WorkDayViewModel extends AndroidViewModel {

    private static final String TAG = WorkDayViewModel.class.getName();

    private AppRepository repository;
    final private MutableLiveData<IWorkTime> workTimeLiveDataOnChange;
    final private MutableLiveData<IWorkTime> workTimeLiveDataOnLoad;
    private WorkTime workTime;
    private boolean storedInDb;

    public WorkDayViewModel(@NonNull Application application) {
        super(application);
        WorkTracksApp app = (WorkTracksApp) application;
        this.repository = app.getRepository();
        this.workTimeLiveDataOnChange = new MutableLiveData<>();
        this.workTimeLiveDataOnLoad = new MutableLiveData<>();
        this.workTime = new WorkTime();
    }

    public void changeDate(@NonNull LocalDate date) {
        IWorkTime wt = this.repository.getWorkTime(date);
        this.storedInDb = wt != null;
        this.workTime = (wt == null) ? new WorkTime(date) : new WorkTime(wt);
        this.workTimeLiveDataOnLoad.setValue(this.workTime);
        this.workTimeLiveDataOnChange.setValue(this.workTime);
    }

    public LiveData<IWorkTime> getWorkTimeOnLoad() {
        return this.workTimeLiveDataOnLoad;
    }

    public LiveData<IWorkTime> getWorkTimeOnChange() {
        return this.workTimeLiveDataOnChange;
    }

    public IWorkTime getWorkTime() {
        return this.workTime;
    }

    public void setBreakDuration(Duration duration) {
        this.workTime.setBreakDuration(duration);
        this.workTimeLiveDataOnChange.setValue(this.workTime);
    }

    public void setStartingTime(LocalTime time) {
        this.workTime.setStartingTime(time);
        this.workTimeLiveDataOnChange.setValue(this.workTime);
    }

    public void setEndingTime(LocalTime time) {
        this.workTime.setEndingTime(time);
        this.workTimeLiveDataOnChange.setValue(this.workTime);
    }

    public void setComment(String comment) {
        this.workTime.setComment(comment);
        this.workTimeLiveDataOnChange.setValue(this.workTime);
    }

    public void setIgnore(boolean ignore) {
        this.workTime.setIgnore(ignore);
        this.workTimeLiveDataOnChange.setValue(this.workTime);
    }

    public boolean save() {
        IWorkTime wt = this.workTime;

        if (!WorkTimeValidator.validate(wt)) {
            return false;
        }

        if (this.storedInDb) {
            Log.d(TAG, String.format("Updating existing entity [%s] in database.", wt));
            this.repository.update(wt.getId(), wt);
        } else {
            Log.d(TAG, String.format("Inserting new entity [%s] in database.", wt));
            this.repository.insert(wt);
        }

        return true;
    }
}
