package me.erikhennig.worktracks.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.erikhennig.worktracks.R;
import me.erikhennig.worktracks.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.findViewById(R.id.button_add_or_edit).setOnClickListener(clickedView -> this.navigateToAddOrEdit());
    }

    @Override
    public void onStart() {
        super.onStart();
        Navigation.findNavController(this, R.id.nav_host_fragment).addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d(TAG, "Changing visibility of button_add_or_edit.");
            View button = this.findViewById(R.id.button_add_or_edit);
            switch (destination.getId()){
                case R.id.AddOrEditEntryFragment:
                    button.setVisibility(View.GONE);
                    break;
                case R.id.TimeTableFragment:
                default:
                    button.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToAddOrEdit() {
        Log.i(TAG, "Swapping to add or edit fragment.");
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_to_add_or_edit);
    }

}
