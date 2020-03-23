package me.erikhennig.worktracks.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.ChronoFormatter;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.WorkTime;
import me.erikhennig.worktracks.viewmodel.WorkWeekViewModel;

public class TimeTableFragment extends Fragment {

    private WorkWeekViewModel workWeekViewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.time_table_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.workWeekViewModel = new ViewModelProvider(requireActivity()).get(WorkWeekViewModel.class);
        this.workWeekViewModel.getWorkTimes().observe(this.getViewLifecycleOwner(), this::updateTimeTable);

        view.<FloatingActionButton>findViewById(R.id.addOrEdit).setOnClickListener(clickedView -> this.navigateToAddOrEdit());
        view.findViewById(R.id.button_next_week).setOnClickListener(clickedView -> this.workWeekViewModel.increaseWeek());
        view.findViewById(R.id.button_previous_week).setOnClickListener(clickedView -> this.workWeekViewModel.decreaseWeek());
        view.findViewById(R.id.text_current_week).setOnClickListener(clickedView -> this.onWeekClick());

        this.getAllElements(R.id.text_difference).forEach(x -> RegexColorDeciderFactory.registerDurationPositiveNegativeDecider((TextView) x));
        this.getAllElements(R.id.text_accumulated_difference).forEach(x -> RegexColorDeciderFactory.registerDurationPositiveNegativeDecider((TextView) x));
    }

    private void onWeekClick() {
        DatePickerDialog picker = new DatePickerDialog(this.requireContext());
        LocalDate dayOfCurrentWeek = this.workWeekViewModel.getWeek().getFirstDayOfWeek();
        picker.updateDate(dayOfCurrentWeek.getYear(), dayOfCurrentWeek.getMonthValue() - 1, dayOfCurrentWeek.getDayOfMonth());
        picker.setOnDateSetListener((clickedView, year, zeroBasedMonth, day) -> this.workWeekViewModel.setWeek(Week.of(LocalDate.of(year, zeroBasedMonth + 1, day))));
        picker.show();
    };

    private List<View> getAllElements(int elementId) {
        final ViewGroup allWeekTimes = this.requireView().findViewById(R.id.linear_layout_all_week_times);
        final ArrayList<View> elements  = new ArrayList<>();

        for (int i = 0; i < allWeekTimes.getChildCount(); ++i) {
            elements.add(allWeekTimes.getChildAt(i).findViewById(elementId));
        }
        return elements;
    }

    private void navigateToAddOrEdit() {
        NavHostFragment.findNavController(TimeTableFragment.this).navigate(R.id.action_to_add_or_edit);
    }

    private void updateTimeTable(List<IWorkTime> timesInWeek) {
        View view = this.requireView();

        this.updateHeader(view);

        List<IWorkTime> timesInWeekWithoutGap = this.workWeekViewModel.getWeek().getDates().stream()
                .map(d -> timesInWeek.stream()
                        .filter(wt -> wt.getDate().isEqual(d))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());

        int i = 0;
        for(View card: this.getAllElements(R.id.card_view_week_day)){
            this.updateCard(card, timesInWeekWithoutGap.get(i++));
        }
    }

    private void updateHeader(View root) {
        TextView week = root.findViewById(R.id.text_current_week);
        TextView weekStart = root.findViewById(R.id.text_week_start);
        TextView weekEnd = root.findViewById(R.id.text_week_end);

        Week currentWeek = this.workWeekViewModel.getWeek();
        LocalDate firstDay = currentWeek.getFirstDayOfWeek();
        LocalDate lastDay = currentWeek.getLastDayOfWeek();

        week.setText(ChronoFormatter.formatWeek(currentWeek));
        weekStart.setText(ChronoFormatter.formatDate(firstDay));
        weekEnd.setText(ChronoFormatter.formatDate(lastDay));
    }

    private void updateCard(View card, IWorkTime workTime) {
        if (workTime == null) {
            card.setVisibility(View.GONE);
            return;
        }
        card.setVisibility(View.VISIBLE);

        WorkTime wt = new WorkTime(workTime);

        String weekDay = ChronoFormatter.formatDayOfWeek(wt.getDate().getDayOfWeek());
        String date = ChronoFormatter.formatDate(wt.getDate());
        String start = ChronoFormatter.formatTime(wt.getStartingTime());
        String end = ChronoFormatter.formatTime(wt.getEndingTime());
        String startTillEnd = String.format(Locale.getDefault(), "%s - %s", start, end);
        String breakDuration = ChronoFormatter.formatDuration(wt.getBreakDuration());
        String duration = ChronoFormatter.formatDuration(wt.getWorkingDuration());

        // TODO extract as parameter
        final Duration workingDuration = wt.getWorkingDuration();
        final Duration expectedDuration = Duration.ofHours(7).plusMinutes(24);
        final Duration differenceDuration = workingDuration.minus(expectedDuration);
        String difference = ChronoFormatter.formatDuration(differenceDuration);

        card.<TextView>findViewById(R.id.text_date).setText(date);
        card.<TextView>findViewById(R.id.text_week_day).setText(weekDay);
        card.<TextView>findViewById(R.id.text_start_till_end).setText(startTillEnd);
        card.<TextView>findViewById(R.id.text_break).setText(breakDuration);
        card.<TextView>findViewById(R.id.text_duration).setText(duration);
        card.<TextView>findViewById(R.id.text_difference).setText(difference);
        card.<TextView>findViewById(R.id.text_accumulated_difference).setText("not impl");
    }
}
