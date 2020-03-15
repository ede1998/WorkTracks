package me.erikhennig.worktracks;

import android.app.Application;

import me.erikhennig.worktracks.db.AppDatabase;

public class WorkTracksApp extends Application {

    public AppDatabase getDatabase() {
        return AppDatabase.getDatabase(this);
    }

    public AppRepository getRepository() {
        return AppRepository.getInstance(this.getDatabase());
    }
}
