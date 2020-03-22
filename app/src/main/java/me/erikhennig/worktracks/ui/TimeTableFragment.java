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

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.model.Week;
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

        view.<FloatingActionButton>findViewById(R.id.addOrEdit).setOnClickListener(this.buttonClick);

        //TextView date = view.findViewById(R.id.date);
        //workTimeViewModel.getWorkTimes(displayedWeek).observe(getViewLifecycleOwner(), x -> date.setText(x.get(0).getDate().toString()));
    }

    private View.OnClickListener buttonClick = clickedView ->
            NavHostFragment.findNavController(TimeTableFragment.this).navigate(R.id.action_to_add_or_edit);
}
