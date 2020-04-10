package me.erikhennig.worktracks.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.stream.Stream;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.WorkTimeValidator;
import me.erikhennig.worktracks.ui.colordecider.RegexColorDeciderFactory;
import me.erikhennig.worktracks.viewmodel.WorkDayViewModel;

public class AddOrEditEntryFragment extends Fragment implements View.OnFocusChangeListener, KeyboardUtils.SoftKeyboardToggleListener {
    private static final String TAG = AddOrEditEntryFragment.class.getName();

    private WorkDayViewModel workDayViewModel;
    private KeyboardToggleFocusSaver focusSaver;
    private ChronoFormatter chronoFormatter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_or_edit_entry_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.workDayViewModel = new ViewModelProvider(requireActivity()).get(WorkDayViewModel.class);
        this.focusSaver = new KeyboardToggleFocusSaver();
        this.chronoFormatter = ChronoFormatter.getInstance();

        view.<CalendarView>findViewById(R.id.calendar).setOnDateChangeListener((clickedView, year, zeroBasedMonth, day) -> {
            LocalDate selectedDate = LocalDate.of(year, zeroBasedMonth + 1, day);
            Log.i(TAG, String.format("Changed date to [%s].", selectedDate));
            this.workDayViewModel.changeDate(selectedDate);
        });

        view.<Button>findViewById(R.id.cancel).setOnClickListener(clickedView -> this.navigateToTable());
        view.<Button>findViewById(R.id.save).setOnClickListener(clickedView -> this.trySaveInput());

        view.findViewById(R.id.start).setOnFocusChangeListener(this);
        view.findViewById(R.id.end).setOnFocusChangeListener(this);
        view.findViewById(R.id.break_duration).setOnFocusChangeListener(this);
        view.findViewById(R.id.comment).setOnFocusChangeListener(this.focusSaver);

        KeyboardUtils.addKeyboardToggleListener(this.requireActivity(), this);

        RegexColorDeciderFactory.registerDurationPositiveNegativeDecider(view.findViewById(R.id.text_difference));
        RegexColorDeciderFactory.registerDurationPositiveNegativeDecider(view.findViewById(R.id.text_accumulated_difference));

        this.workDayViewModel.getWorkTimeOnChange().observe(this.getViewLifecycleOwner(), this::updateTextViews);
        this.workDayViewModel.getWorkTimeOnLoad().observe(this.getViewLifecycleOwner(), this::updateInputFields);

        this.workDayViewModel.changeDate(LocalDate.now());
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        this.focusSaver.onFocusChange(view, hasFocus);

        if (hasFocus) return;

        Log.d(TAG, "Input field lost focus. Checking contents and updating time status information.");

        EditText text = (EditText) view;

        switch (text.getId()) {
            case R.id.break_duration:
                final Duration duration = parse(text.getText().toString(), this.chronoFormatter::parseDuration, "break duration");
                this.workDayViewModel.setBreakDuration(duration);
                break;
            case R.id.start:
                final LocalTime start = parse(text.getText().toString(), this.chronoFormatter::parseTime, "starting time");
                this.workDayViewModel.setStartingTime(start);
                break;
            case R.id.end:
                final LocalTime end = parse(text.getText().toString(), this.chronoFormatter::parseTime, "ending time");
                this.workDayViewModel.setEndingTime(end);
                break;
        }
    }

    private <T> T parse(String text, Function<String, T> parser, String fieldHint) {
        try {
            if (text.isEmpty())
                return null;
            return parser.apply(text);
        } catch (DateTimeParseException e) {
            String errorMessage = "Input '" + e.getParsedString() + "' in field " + fieldHint + " could not be parsed.";
            Snackbar snackbar = Snackbar.make(this.requireView(), errorMessage, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return null;
        }
    }

    private void trySaveInput() {
        View view = this.requireView();

        Stream.of(R.id.start, R.id.end, R.id.break_duration)
                .map(view::<EditText>findViewById)
                .forEach(x -> this.onFocusChange(x, false));

        Log.i(TAG, "Trying to save input to database.");

        boolean ignoreDay = view.<Switch>findViewById(R.id.ignoreDay).isChecked();
        String comment = view.<EditText>findViewById(R.id.comment).getText().toString();
        this.workDayViewModel.setIgnore(ignoreDay);
        this.workDayViewModel.setComment(comment);

        boolean wasSaved = this.workDayViewModel.save();
        if (!wasSaved) {
            Log.e(TAG, String.format("Invalid work time [%s].", this.workDayViewModel.getWorkTime()));
            return;
        }


        Log.i(TAG, String.format("Successfully saved work time [%s]", this.workDayViewModel.getWorkTime()));

        this.navigateToTable();
    }

    private void updateInputFields(IWorkTime workTime) {
        View view = this.requireView();

        String startingTime = format(workTime.getStartingTime(), this.chronoFormatter::formatTime);
        String endingTime = format(workTime.getEndingTime(), this.chronoFormatter::formatTime);
        String duration = format(workTime.getBreakDuration(), this.chronoFormatter::formatDuration);
        boolean ignore = workTime.getIgnore();
        String comment = workTime.getComment();

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

    private void updateTextViews(IWorkTime workTime) {
        Log.d(TAG, "Updating time status information text views.");
        View view = this.requireView();

        String durationString = this.getString(R.string.unknown_duration);
        String differenceString = this.getString(R.string.unknown_difference);
        String accumulatedDifferenceString = this.getString(R.string.unknown_difference);

        boolean isValid = WorkTimeValidator.validate(workTime);
        Duration duration = isValid ? Duration.between(workTime.getStartingTime(), workTime.getEndingTime()).minus(workTime.getBreakDuration()) : null;
        if (duration != null) {
            durationString = this.chronoFormatter.formatDuration(duration);

            // TODO extract as parameter
            final Duration expectedDuration = Duration.ofHours(7).plusMinutes(24);
            final Duration difference = duration.minus(expectedDuration);
            differenceString = this.chronoFormatter.formatDuration(difference);
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

        View root = this.getView();
        if (root == null) return;

        int visibility = isVisible ? View.GONE : View.VISIBLE;
        root.findViewById(R.id.calendar).setVisibility(visibility);
    }
}
