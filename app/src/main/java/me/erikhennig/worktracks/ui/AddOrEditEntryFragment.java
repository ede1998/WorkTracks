package me.erikhennig.worktracks.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.TimeDurationFormatter;
import me.erikhennig.worktracks.model.WorkTime;
import me.erikhennig.worktracks.viewmodel.WorkTimeViewModel;

public class AddOrEditEntryFragment extends Fragment implements View.OnFocusChangeListener {

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
            this.loadWorkTime(LocalDate.of(year, zeroBasedMonth + 1, day));
            this.updateInputFields();
            this.updateTextViews();
        });

        view.<Button>findViewById(R.id.cancel).setOnClickListener(clickedView -> this.navigateToTable());
        view.<Button>findViewById(R.id.save).setOnClickListener(clickedView -> this.trySaveInput());

        view.<EditText>findViewById(R.id.start).setOnFocusChangeListener(this);
        view.<EditText>findViewById(R.id.end).setOnFocusChangeListener(this);
        view.<EditText>findViewById(R.id.break_duration).setOnFocusChangeListener(this);

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

        EditText text = (EditText) view;


        switch (text.getId()) {
            case R.id.break_duration:
                final Duration duration = parse(text, TimeDurationFormatter::parseDuration);
                this.workTime.setBreakDuration(duration);
                break;
            case R.id.start:
                final LocalTime start = parse(text, TimeDurationFormatter::parseTime);
                this.workTime.setStartingTime(start);
                break;
            case R.id.end:
                final LocalTime end = parse(text, TimeDurationFormatter::parseTime);
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
        View view = requireView();

        boolean ignoreDay = view.<Switch>findViewById(R.id.ignoreDay).isChecked();
        this.workTime.setIgnore(ignoreDay);

        if (!this.workTime.validate()) {
            return;
        }

        this.workTimeViewModel.insertOrUpdate(this.workTime);

        this.navigateToTable();
    }

    private void updateInputFields() {
        View view = requireView();

        String startingTime = format(this.workTime.getStartingTime(), TimeDurationFormatter::formatTime);
        String endingTime = format(this.workTime.getEndingTime(), TimeDurationFormatter::formatTime);
        String duration = format(this.workTime.getBreakDuration(), TimeDurationFormatter::formatDuration);
        boolean ignore = this.workTime.getIgnore();

        view.<EditText>findViewById(R.id.start).setText(startingTime);
        view.<EditText>findViewById(R.id.end).setText(endingTime);
        view.<EditText>findViewById(R.id.break_duration).setText(duration);
        view.<Switch>findViewById(R.id.ignoreDay).setChecked(ignore);
    }

    private static <T> String format(T value, Function<T, String> formatter)
    {
        if (value == null)
            return "";
        return formatter.apply(value);
    }

    private void updateTextViews() {
        View view = requireView();

        String durationString = this.getString(R.string.duration);
        String differenceString = this.getString(R.string.difference);
        String accumulatedDifferenceString = this.getString(R.string.accumulated_difference);

        int positiveColor = ContextCompat.getColor(this.requireContext(), R.color.colorPositive);
        int negativeColor = ContextCompat.getColor(this.requireContext(), R.color.colorNegative);
        int neutralColor = ContextCompat.getColor(this.requireContext(), R.color.colorNeutral);

        int differenceColor = neutralColor;
        int accumulatedDifferenceColor = neutralColor;

        Duration duration = this.workTime.getWorkingDuration();
        if (duration != null) {
            durationString = TimeDurationFormatter.formatDuration(duration);

            // TODO extract as parameter
            final Duration expectedDuration = Duration.ofHours(7).plusMinutes(24);
            final Duration difference = duration.minus(expectedDuration);
            differenceString = TimeDurationFormatter.formatDuration(difference);
            differenceColor = difference.isNegative()? negativeColor: positiveColor;
            differenceColor = difference.isZero()? neutralColor: differenceColor;

            accumulatedDifferenceColor = neutralColor;
        }

        view.<TextView>findViewById(R.id.duration).setText(durationString);
        view.<TextView>findViewById(R.id.difference).setTextColor(differenceColor);
        view.<TextView>findViewById(R.id.difference).setText(differenceString);
        view.<TextView>findViewById(R.id.accumulated_difference).setText(accumulatedDifferenceString);
        view.<TextView>findViewById(R.id.accumulated_difference).setTextColor(accumulatedDifferenceColor);
    }

    private void navigateToTable() {
        NavHostFragment.findNavController(AddOrEditEntryFragment.this).navigate(R.id.action_back_to_table);
    }
}
