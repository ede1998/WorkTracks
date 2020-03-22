package me.erikhennig.worktracks.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.IWorkTime;
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
            this.workTime.setDate(LocalDate.of(year, zeroBasedMonth + 1, day));
            this.updateInputFields();
        });

        view.<Button>findViewById(R.id.cancel).setOnClickListener(clickedView -> this.navigateToTable());
        view.<Button>findViewById(R.id.save).setOnClickListener(clickedView -> this.trySaveInput());

        view.<EditText>findViewById(R.id.start).setOnFocusChangeListener(this);
        view.<EditText>findViewById(R.id.end).setOnFocusChangeListener(this);
        view.<EditText>findViewById(R.id.break_duration).setOnFocusChangeListener(this);

        this.workTime = new WorkTime();
        this.workTime.setDate(LocalDate.now());
        this.updateInputFields();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) return;

        EditText text = (EditText) view;

        LocalTime time = parseTime(text);

        switch (text.getId())
        {
            case R.id.break_duration:
                Duration breakDuration = null;
                if (time != null) {
                    breakDuration = Duration.between(LocalTime.of(0, 0), time);
                }
                this.workTime.setBreakDuration(breakDuration);
                break;
            case R.id.start:
                this.workTime.setStartingTime(time);
                break;
            case R.id.end:
                this.workTime.setEndingTime(time);
                break;
        }

        this.updateTextViews();
    }

    private static LocalTime parseTime(EditText textField) {
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
            return LocalTime.parse(textField.getText().toString());
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
    };

    private void updateInputFields() {
        View view = requireView();

        String startingTime = "";
        String endingTime = "";
        String duration = "";
        boolean ignore = false;

        IWorkTime wt = this.workTimeViewModel.getWorkTime(this.workTime.getDate());
        if (wt != null) {
            this.workTime = new WorkTime(wt);
            startingTime = wt.getStartingTime().toString();
            endingTime = wt.getEndingTime().toString();
            final long durationInMinutes = wt.getBreakDuration().toMinutes();
            duration = String.format(Locale.getDefault(), "%02d:%02d", durationInMinutes / 60, durationInMinutes % 60);
            ignore = wt.getIgnore();
        }

        view.<EditText>findViewById(R.id.start).setText(startingTime);
        view.<EditText>findViewById(R.id.end).setText(endingTime);
        view.<EditText>findViewById(R.id.break_duration).setText(duration);
        view.<Switch>findViewById(R.id.ignoreDay).setChecked(ignore);
    }

    private void updateTextViews() {
        View view = requireView();

    }

    private void navigateToTable() {
        NavHostFragment.findNavController(AddOrEditEntryFragment.this).navigate(R.id.action_back_to_table);
    }
}
