package me.erikhennig.worktracks.ui.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Stream;

import me.erikhennig.worktracks.AppRepository;
import me.erikhennig.worktracks.WorkTracksApp;
import me.erikhennig.worktracks.model.csv.CSVWorkTime;

public class ImportActivity extends FragmentActivity {
    private static final String TAG = ImportActivity.class.getName();

    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 6;

    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.repository = ((WorkTracksApp) this.getApplication()).getRepository();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent = Intent.createChooser(intent, "Import work times");

        this.startActivityForResult(intent, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = resultData.getData();
                this.importData(uri);
            } else {
                Log.i(TAG, String.format("Activity result is [%s] instead of ok.", resultCode));
            }
        }

        this.finish();
    }

    private void importData(Uri uri) {
        Log.i(TAG, String.format("Starting to import data from URI [%s]", uri));

        if (this.repository == null) {
            Log.e(TAG, "Repository is null. Cannot import.");
            return;
        }

        try (BufferedReader br = this.readFromUri(uri)) {

            Stream<CSVWorkTime> wts = CSVWorkTime.read(br);

            wts.forEach(wt -> {
                this.repository.insert(wt);
                Log.d(TAG, String.format("Imported [%s]", wt));
            });

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Could not find file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Could not read file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, String.format("RunTimeException; [%s]", e.getMessage()));
            Toast.makeText(this, "Unknown error occurred.", Toast.LENGTH_LONG).show();
        }
    }

    private BufferedReader readFromUri(Uri uri) throws FileNotFoundException {
        ContentResolver resolver = this.getContentResolver();
        InputStream stream = resolver.openInputStream(uri);

        if (stream == null) {
            Log.e(TAG, String.format("Input stream was null for uri [%s]", uri));
        }

        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));
    }
}

