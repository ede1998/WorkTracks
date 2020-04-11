package me.erikhennig.worktracks.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.PreferenceUtils;
import me.erikhennig.worktracks.model.SystemUtils;
import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getName();

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

        private ChronoFormatter chronoFormatter;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            this.chronoFormatter = ChronoFormatter.getInstance();

            final Preference reset = this.findPreference(PreferenceUtils.RESET);
            if (reset != null) {
                reset.setOnPreferenceClickListener(preference -> {
                    ResetDialogFragment dialog = new ResetDialogFragment();
                    dialog.show(this.getParentFragmentManager(), "reset");
                    return true;
                });
            }

            final EditTextPreference workDuration = this.findPreference(PreferenceUtils.WEEKLY_WORK_DURATION);
            if (workDuration != null) {
                workDuration.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME));
                workDuration.setOnPreferenceChangeListener((preference, newValue) -> {
                    String value = (String) newValue;
                    try {
                        Duration duration = this.chronoFormatter.parseDuration(value);
                        if (duration.isNegative()) {
                            this.showSnackbar("Duration may not be negative.");
                        }
                        return !duration.isNegative();
                    } catch (DateTimeParseException e) {
                        this.showSnackbar("Input '" + e.getParsedString() + "' could not be parsed.");
                        return false;
                    }
                });
            }

            PreferenceUtils.setOnChangeDurationDisplay(ChronoFormatter.getInstance());

            final Preference versionCode = this.findPreference(PreferenceUtils.VERSION_CODE);
            if (versionCode != null) {
                versionCode.setSummary(SystemUtils.getAppVersionCode(this.requireContext()).toString());
            }

            final Preference versionName = this.findPreference(PreferenceUtils.VERSION_NAME);
            if (versionName != null) {
                versionName.setSummary(SystemUtils.getAppVersionName(this.requireContext()));
            }

            final Preference projectWebPage = this.findPreference(PreferenceUtils.PROJECT_WEB_PAGE);
            if (projectWebPage != null) {
                projectWebPage.setOnPreferenceClickListener(x -> this.openBrowser(x.getSummary().toString()));
            }

            final Preference icon_attribution = this.findPreference(PreferenceUtils.ICON_ATTRIBUTION);
            if (icon_attribution != null) {
                icon_attribution.setOnPreferenceClickListener(x -> this.openBrowser(this.getString(R.string.icon_attribution_link)));
            }
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

        private void showSnackbar(String message) {
            Snackbar snackbar = Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        private boolean openBrowser(String link) {
            Log.i(TAG, String.format("Opening link [%s].", link));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            this.startActivity(browserIntent);
            return true;
        }
    }
}