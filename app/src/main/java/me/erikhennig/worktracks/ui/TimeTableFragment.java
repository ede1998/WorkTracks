package me.erikhennig.worktracks.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.PreferenceUtils;
import me.erikhennig.worktracks.model.WorkTimeConfiguration;
import me.erikhennig.worktracks.model.chronoformatter.ChronoFormatter;
import me.erikhennig.worktracks.model.Week;
import me.erikhennig.worktracks.model.WorkTimeWithTimeStatus;
import me.erikhennig.worktracks.ui.colordecider.RegexColorDeciderFactory;
import me.erikhennig.worktracks.viewmodel.WorkWeekViewModel;

public class TimeTableFragment extends Fragment {

    private static final String TAG = TimeTableFragment.class.getName();

    private WorkWeekViewModel workWeekViewModel;
    private ChronoFormatter chronoFormatter;

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

        this.chronoFormatter = ChronoFormatter.getInstance();
        this.workWeekViewModel = new ViewModelProvider(this.requireActivity()).get(WorkWeekViewModel.class);
        this.workWeekViewModel.updateConfiguration(WorkTimeConfiguration.fromPreferences(this.requireContext()));
        this.workWeekViewModel.getWorkTimes().observe(this.getViewLifecycleOwner(), this::updateTimeTable);
        PreferenceUtils.setOnChangeWeeklyWorkDuration(duration -> WorkTimeConfiguration.fromPreferences(this.requireContext()));
        PreferenceUtils.setOnChangeWorkingDays(duration -> WorkTimeConfiguration.fromPreferences(this.requireContext()));

        view.findViewById(R.id.button_next_week).setOnClickListener(clickedView -> this.workWeekViewModel.increaseWeek());
        view.findViewById(R.id.button_previous_week).setOnClickListener(clickedView -> this.workWeekViewModel.decreaseWeek());
        view.findViewById(R.id.text_current_week).setOnClickListener(clickedView -> this.gotoCurrentWeek());
        view.findViewById(R.id.text_current_week).setOnLongClickListener(clickedView -> {
            this.selectWeek();
            return true;
        });

        this.getAllElements(R.id.text_difference).forEach(x -> RegexColorDeciderFactory.registerDurationPositiveNegativeDecider((TextView) x));
        this.getAllElements(R.id.text_accumulated_difference).forEach(x -> RegexColorDeciderFactory.registerDurationPositiveNegativeDecider((TextView) x));
    }

    private void gotoCurrentWeek() {
        Log.i(TAG, "Changing overview back to current week");
        Week week = Week.now();
        this.workWeekViewModel.setWeek(week);
    }

    private void selectWeek() {
        Log.i(TAG, "Opening week selection pop up.");
        DatePickerDialog picker = new DatePickerDialog(this.requireContext());
        LocalDate dayOfCurrentWeek = this.workWeekViewModel.getWeek().getFirstDayOfWeek();
        picker.updateDate(dayOfCurrentWeek.getYear(), dayOfCurrentWeek.getMonthValue() - 1, dayOfCurrentWeek.getDayOfMonth());
        picker.setOnDateSetListener((clickedView, year, zeroBasedMonth, day) -> {
            Week week = Week.of(LocalDate.of(year, zeroBasedMonth + 1, day));
            Log.i(TAG, String.format("Week [%s] was selected.", week));
            this.workWeekViewModel.setWeek(week);
        });
        picker.show();
    }

    private List<View> getAllElements(int elementId) {
        final ViewGroup allWeekTimes = this.requireView().findViewById(R.id.linear_layout_all_week_times);
        final ArrayList<View> elements = new ArrayList<>();

        for (int i = 0; i < allWeekTimes.getChildCount(); ++i) {
            elements.add(allWeekTimes.getChildAt(i).findViewById(elementId));
        }
        return elements;
    }

    private void updateTimeTable(List<WorkTimeWithTimeStatus> timesInWeek) {
        Log.i(TAG, "Updating fragment");
        View view = this.requireView();

        this.updateHeader(view);

        List<WorkTimeWithTimeStatus> timesInWeekWithoutGap = this.workWeekViewModel.getWeek().getDates().stream()
                .map(d -> timesInWeek.stream()
                        .filter(wt -> wt.getDate().isEqual(d))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());

        int i = 0;
        for (View card : this.getAllElements(R.id.card_view_week_day)) {
            this.updateCard(card, timesInWeekWithoutGap.get(i++));
        }
    }

    private void updateHeader(View root) {
        Log.i(TAG, "Updating header line");
        TextView week = root.findViewById(R.id.text_current_week);
        TextView weekStart = root.findViewById(R.id.text_week_start);
        TextView weekEnd = root.findViewById(R.id.text_week_end);

        Week currentWeek = this.workWeekViewModel.getWeek();
        LocalDate firstDay = currentWeek.getFirstDayOfWeek();
        LocalDate lastDay = currentWeek.getLastDayOfWeek();

        week.setText(this.chronoFormatter.formatWeek(currentWeek));
        weekStart.setText(this.chronoFormatter.formatDate(firstDay));
        weekEnd.setText(this.chronoFormatter.formatDate(lastDay));
    }

    private void updateCard(View card, WorkTimeWithTimeStatus wt) {
        if (wt == null) {
            Log.i(TAG, String.format("Setting visibility of card [%s] to GONE", card.getId()));
            card.setVisibility(View.GONE);
            return;
        }
        Log.i(TAG, String.format("Making card [%s] VISIBLE and updating it.", card.getId()));
        card.setVisibility(View.VISIBLE);

        String weekDay = this.chronoFormatter.formatDayOfWeek(wt.getDate().getDayOfWeek());
        String date = this.chronoFormatter.formatDate(wt.getDate());
        String start = this.chronoFormatter.formatTime(wt.getStartingTime());
        String end = this.chronoFormatter.formatTime(wt.getEndingTime());
        String startTillEnd = String.format(Locale.getDefault(), "%s - %s", start, end);
        String breakDuration = this.chronoFormatter.formatDuration(wt.getBreakDuration());
        String comment = wt.getComment();

        String duration = this.chronoFormatter.formatDuration(wt.getDuration());
        String difference = this.chronoFormatter.formatDuration(wt.getDifference());
        String accumulatedDifference = this.chronoFormatter.formatDuration(wt.getAccumulatedDifference());

        card.<TextView>findViewById(R.id.text_date).setText(date);
        card.<TextView>findViewById(R.id.text_week_day).setText(weekDay);
        card.<TextView>findViewById(R.id.text_start_till_end).setText(startTillEnd);
        card.<TextView>findViewById(R.id.text_break).setText(breakDuration);
        card.<TextView>findViewById(R.id.text_duration).setText(duration);
        card.<TextView>findViewById(R.id.text_difference).setText(difference);
        card.<TextView>findViewById(R.id.text_accumulated_difference).setText(accumulatedDifference);

        int cardColor = wt.getIgnore() ?
                ContextCompat.getColor(card.getContext(), R.color.colorDisabled) :
                ContextCompat.getColor(card.getContext(), R.color.design_default_color_background);

        card.setBackgroundColor(cardColor);

        TextView commentView = card.findViewById(R.id.text_comment);
        if (comment != null && !comment.equals("")) {
            commentView.setText(comment);
            commentView.setVisibility(View.VISIBLE);
        } else {
            commentView.setVisibility(View.GONE);
        }
    }
}
