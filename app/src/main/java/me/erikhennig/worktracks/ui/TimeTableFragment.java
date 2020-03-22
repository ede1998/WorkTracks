package me.erikhennig.worktracks.ui;

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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.ChronoFormatter;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.WorkTime;
import me.erikhennig.worktracks.viewmodel.WorkTimeViewModel;

public class TimeTableFragment extends Fragment {

    private WorkTimeViewModel workTimeViewModel;
    private Week displayedWeek;

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

        this.workTimeViewModel = new ViewModelProvider(requireActivity()).get(WorkTimeViewModel.class);
        this.displayedWeek = Week.now();

        view.<FloatingActionButton>findViewById(R.id.addOrEdit).setOnClickListener(clickedView -> this.navigateToAddOrEdit());
        this.applyToAllCards(R.id.text_difference, x -> RegexColorDeciderFactory.registerDurationPositiveNegativeDecider((TextView) x));
        this.applyToAllCards(R.id.text_accumulated_difference, x -> RegexColorDeciderFactory.registerDurationPositiveNegativeDecider((TextView) x));

        this.displayedWeek = Week.now();

        workTimeViewModel.getWorkTimes(displayedWeek).observe(this.getViewLifecycleOwner(), this::updateTimeTable);
    }

    @FunctionalInterface
    private interface Method {
        void call(View view);
    }

    private void applyToAllCards(int elementId, Method method) {
        ViewGroup allWeekTimes = this.requireView().findViewById(R.id.linear_layout_all_week_times);
        for (int i = 0; i < allWeekTimes.getChildCount(); ++i) {
            View card = allWeekTimes.getChildAt(i);
            method.call(card.findViewById(elementId));
        }
    }

    private void navigateToAddOrEdit() {
        NavHostFragment.findNavController(TimeTableFragment.this).navigate(R.id.action_to_add_or_edit);
    }

    private void updateTimeTable(List<IWorkTime> timesInWeek) {
        View view = this.requireView();

        this.updateHeader(view);

        List<IWorkTime> timesInWeekWithoutGap = this.displayedWeek.getDates().stream()
                .map(d -> timesInWeek.stream()
                        .filter(wt -> wt.getDate().isEqual(d))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());

        ViewGroup allWeekTimes = view.findViewById(R.id.linear_layout_all_week_times);
        for (int i = 0; i < allWeekTimes.getChildCount(); ++i) {
            this.updateCard(allWeekTimes.getChildAt(i), timesInWeekWithoutGap.get(i));
        }
    }

    private void updateHeader(View root) {
        TextView week = root.findViewById(R.id.text_current_week);
        TextView weekStart = root.findViewById(R.id.text_week_start);
        TextView weekEnd = root.findViewById(R.id.text_week_end);

        Week currentWeek = this.displayedWeek;
        LocalDate firstDay = this.displayedWeek.getFirstDayOfWeek();
        LocalDate lastDay = this.displayedWeek.getLastDayOfWeek();

        week.setText(ChronoFormatter.formatWeek(currentWeek));
        weekStart.setText(ChronoFormatter.formatDate(firstDay));
        weekEnd.setText(ChronoFormatter.formatDate(lastDay));
    }

    private void updateCard(View card, IWorkTime workTime) {
        if (workTime == null) {
            card.setVisibility(View.GONE);
            return;
        }

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
