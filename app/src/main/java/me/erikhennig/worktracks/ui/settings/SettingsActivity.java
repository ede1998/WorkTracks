package me.erikhennig.worktracks.ui.settings;

import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.PropertyResourceBundle;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.PreferenceUtils;
import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference reset = this.findPreference(PreferenceUtils.RESET);
            if (reset != null) {
                reset.setOnPreferenceClickListener(preference -> {
                    ResetDialogFragment dialog = new ResetDialogFragment();
                    dialog.show(this.getParentFragmentManager(), "reset");
                    return true;
                });
            }

            EditTextPreference workDuration = this.findPreference(PreferenceUtils.WEEKLY_WORK_DURATION);
            if (workDuration != null) {
                workDuration.setOnBindEditTextListener(preference -> {
                    preference.setInputType(InputType.TYPE_CLASS_NUMBER);
                });
                workDuration.setOnPreferenceChangeListener((preference, newValue) -> {
                        String value = (String) newValue;
                        //try {
                        //    Duration d = ChronoFormatter.parseDuration(value);
                        //}
                        return false;
                    });
            }

            PreferenceUtils.setOnChangeDurationDisplay(ChronoFormatter.getInstance());
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceUtils.register(this.requireContext());
        }

        @Override
        public void onPause() {
            super.onPause();
            PreferenceUtils.unregister(this.requireContext());
        }
    }
}