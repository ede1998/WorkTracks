package me.erikhennig.worktracks.ui.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.WorkTracksApp;
import me.erikhennig.worktracks.model.IWorkTime;
import me.erikhennig.worktracks.model.csv.CSVWorkTime;

public class ExportActivity extends FragmentActivity {
    private static final String TAG = ExportActivity.class.getName();

    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 6;

    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.repository = ((WorkTracksApp) this.getApplication()).getRepository();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE,"work-tracks.csv");

        this.startActivityForResult(intent, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = resultData.getData();
                this.exportData(uri);
            } else {
                Log.i(TAG, String.format("Activity result is [%s] instead of ok.", resultCode));
            }
        }

        this.finish();
    }

    private void exportData(Uri uri) {
        Log.i(TAG, String.format("Starting to export data to URI [%s]", uri));

        if (this.repository == null) {
            Log.e(TAG, "Repository is null. Cannot export.");
            return;
        }

        try (BufferedWriter br = this.writeToUri(uri)) {
            List<IWorkTime> wts = this.repository.getAllWorkTimes();

            boolean result = CSVWorkTime.write(br, wts);

            if (!result) {
                Log.e(TAG, "Could not export all work times.");
                Toast.makeText(this, "Exporting all worktimes failed.", Toast.LENGTH_LONG).show();
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Could not find file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Could not read file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private BufferedWriter writeToUri(Uri uri) throws FileNotFoundException {
        ContentResolver resolver = this.getContentResolver();
        OutputStream stream = resolver.openOutputStream(uri);

        if (stream == null) {
            Log.e(TAG, String.format("Output stream was null for uri [%s]", uri));
        }

        return new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(stream)));
    }
}

