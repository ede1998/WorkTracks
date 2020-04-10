package me.erikhennig.worktracks;

import android.app.Application;
import android.content.Context;

import me.erikhennig.worktracks.db.AppDatabase;
import me.erikhennig.worktracks.model.PreferenceUtils;
import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;

public class WorkTracksApp extends Application {

    public AppDatabase getDatabase() {
        return AppDatabase.getDatabase(this);
    }

    public AppRepository getRepository() {
        return AppRepository.getInstance(this.getDatabase());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.loadOptions();
    }

    private void loadOptions() {
        Context context = this.getApplicationContext();
        ChronoFormatter.getInstance().setDurationDisplay(PreferenceUtils.getDurationDisplay(context));
    }
}
