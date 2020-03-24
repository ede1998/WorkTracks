package me.erikhennig.worktracks.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.rw.keyboardlistener.KeyboardUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.ChronoFormatter;
import me.erikhennig.worktracks.model.WorkTime;
import me.erikhennig.worktracks.viewmodel.WorkTimeViewModel;

public class AddOrEditEntryFragment extends Fragment implements View.OnFocusChangeListener, KeyboardUtils.SoftKeyboardToggleListener {
    private static final String TAG = AddOrEditEntryFragment.class.getName();

    private WorkTimeViewModel workTimeViewModel;
    private WorkTime workTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_or_edit_entry_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        workTimeViewModel = new ViewModelProvider(requireActivity()).get(WorkTimeViewModel.class);

        view.<CalendarView>findViewById(R.id.calendar).setOnDateChangeListener((clickedView, year, zeroBasedMonth, day) -> {
            LocalDate selectedDate = LocalDate.of(year, zeroBasedMonth + 1, day);
            Log.i(TAG, String.format("Changed date to [%s]. Updating UI.", selectedDate));
            this.loadWorkTime(selectedDate);
            this.updateInputFields();
            this.updateTextViews();
        });

        view.<Button>findViewById(R.id.cancel).setOnClickListener(clickedView -> this.navigateToTable());
        view.<Button>findViewById(R.id.save).setOnClickListener(clickedView -> this.trySaveInput());

        view.<EditText>findViewById(R.id.start).setOnFocusChangeListener(this);
        view.<EditText>findViewById(R.id.end).setOnFocusChangeListener(this);
        view.<EditText>findViewById(R.id.break_duration).setOnFocusChangeListener(this);

        KeyboardUtils.addKeyboardToggleListener(this.requireActivity(), this);

        RegexColorDeciderFactory.registerDurationPositiveNegativeDecider(view.findViewById(R.id.text_difference));
        RegexColorDeciderFactory.registerDurationPositiveNegativeDecider(view.findViewById(R.id.text_accumulated_difference));

        this.loadWorkTime(LocalDate.now());
        this.updateInputFields();
        this.updateTextViews();
    }

    private void loadWorkTime(LocalDate date) {
        IWorkTime wt = this.workTimeViewModel.getWorkTime(date);
        this.workTime = wt != null ? new WorkTime(wt) : new WorkTime(date);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) return;

        Log.d(TAG, "Input field lost focus. Checking contents and updating time status information.");

        EditText text = (EditText) view;

        switch (text.getId()) {
            case R.id.break_duration:
                final Duration duration = parse(text, ChronoFormatter::parseDuration);
                this.workTime.setBreakDuration(duration);
                break;
            case R.id.start:
                final LocalTime start = parse(text, ChronoFormatter::parseTime);
                this.workTime.setStartingTime(start);
                break;
            case R.id.end:
                final LocalTime end = parse(text, ChronoFormatter::parseTime);
                this.workTime.setEndingTime(end);
                break;
        }

        this.updateTextViews();
    }


    private static <T> T parse(EditText textField, Function<String, T> parser) {
        String inputName = "UNKNOWN";
        switch (textField.getId()) {
            case R.id.start:
                inputName = "starting time";
                break;
            case R.id.end:
                inputName = "ending time";
                break;
            case R.id.break_duration:
                inputName = "break duration";
                break;
        }

        try {
            return parser.apply(textField.getText().toString());
        } catch (DateTimeParseException e) {
            String errorMessage = "Input '" + e.getParsedString() + "' in field " + inputName + " could not be parsed.";
            Snackbar snackbar = Snackbar.make(textField, errorMessage, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return null;
        }
    }

    private void trySaveInput() {
        Log.i(TAG, "Trying to save input to database.");
        View view = this.requireView();

        boolean ignoreDay = view.<Switch>findViewById(R.id.ignoreDay).isChecked();
        String comment = view.<EditText>findViewById(R.id.comment).getText().toString();
        this.workTime.setIgnore(ignoreDay);
        this.workTime.setComment(comment);

        if (!this.workTime.validate()) {
            Log.e(TAG, String.format("Invalid work time [%s].", this.workTime));
            return;
        }

        this.workTimeViewModel.insertOrUpdate(this.workTime);

        Log.i(TAG, String.format("Successfully saved work time [%s]", this.workTime));

        this.navigateToTable();
    }

    private void updateInputFields() {
        View view = this.requireView();

        String startingTime = format(this.workTime.getStartingTime(), ChronoFormatter::formatTime);
        String endingTime = format(this.workTime.getEndingTime(), ChronoFormatter::formatTime);
        String duration = format(this.workTime.getBreakDuration(), ChronoFormatter::formatDuration);
        boolean ignore = this.workTime.getIgnore();
        String comment = this.workTime.getComment();

        view.<EditText>findViewById(R.id.start).setText(startingTime);
        view.<EditText>findViewById(R.id.end).setText(endingTime);
        view.<EditText>findViewById(R.id.break_duration).setText(duration);
        view.<EditText>findViewById(R.id.comment).setText(comment);
        view.<Switch>findViewById(R.id.ignoreDay).setChecked(ignore);
    }

    private static <T> String format(T value, Function<T, String> formatter) {
        if (value == null)
            return "";
        return formatter.apply(value);
    }

    private void updateTextViews() {
        Log.d(TAG, "Updating time status information text views.");
        View view = this.requireView();

        String durationString = this.getString(R.string.unknown_duration);
        String differenceString = this.getString(R.string.unknown_difference);
        String accumulatedDifferenceString = this.getString(R.string.unknown_difference);

        Duration duration = this.workTime.getWorkingDuration();
        if (duration != null) {
            durationString = ChronoFormatter.formatDuration(duration);

            // TODO extract as parameter
            final Duration expectedDuration = Duration.ofHours(7).plusMinutes(24);
            final Duration difference = duration.minus(expectedDuration);
            differenceString = ChronoFormatter.formatDuration(difference);
        }

        view.<TextView>findViewById(R.id.text_duration).setText(durationString);
        view.<TextView>findViewById(R.id.text_difference).setText(differenceString);
        view.<TextView>findViewById(R.id.text_accumulated_difference).setText(accumulatedDifferenceString);
    }

    private void navigateToTable() {
        Log.i(TAG, "Returning to time table fragment.");
        NavHostFragment.findNavController(AddOrEditEntryFragment.this).navigate(R.id.action_back_to_table);
    }

    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {
        Log.i(TAG, String.format("onKeyboardShowHide(%s) called.", isVisible));
        int visibility = isVisible ? View.GONE : View.VISIBLE;
        View root = this.getView();

        if (root == null) return;

        root.findViewById(R.id.calendar).setVisibility(visibility);
    }
}
