package me.erikhennig.worktracks.ui.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.WorkTracksApp;

public class ResetDialogFragment extends DialogFragment {

    private static final String TAG = ResetDialogFragment.class.getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "Showing reset dialog.");

        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());

        builder.setTitle("Reset");
        builder.setMessage("This action deletes all saved work times. Are you sure?");

        builder.setPositiveButton(R.string.cancel, (dialogInterface, which) -> {
            Log.i(TAG, "Cancelled reset.");
        });
        builder.setNegativeButton("OK", (di, which) -> this.reset());

        return builder.create();
    }

    private void reset() {
        Log.i(TAG, "Following through with reset. Deleting all work times.");

        WorkTracksApp app = (WorkTracksApp) this.requireActivity().getApplication();
        AppRepository repository = app.getRepository();
        repository.deleteAll();

        Log.i(TAG, "Deleted all tracked work times.");
    }


}
