package awersching.gammelgps.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import awersching.gammelgps.R;
import awersching.gammelgps.location.Data;
import awersching.gammelgps.location.GPS;

import static awersching.gammelgps.util.Util.round;

public class MainActivity extends AppCompatActivity {

    private TextView currentSpeedTV;
    private TextView averageSpeedTV;
    private TextView maxSpeedTV;
    private TextView distanceTV;
    private TextView timeTV;

    private GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentSpeedTV = findViewById(R.id.current_speed_tv);
        averageSpeedTV = findViewById(R.id.average_speed_tv);
        maxSpeedTV = findViewById(R.id.max_speed_tv);
        distanceTV = findViewById(R.id.distance_tv);
        timeTV = findViewById(R.id.time_tv);

        getPermissions();
        gps = new GPS(this);
    }

    @Override
    protected void onPause() {
        gps.pause();
        super.onPause();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();
        gps.start().subscribe(this::updateUI);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_menu_item:
                gps.stop(false);
                finish();
                return true;
            case R.id.save_exit_menu_item:
                gps.stop(true);
                finish();
                return true;
        }
        return false;
    }

    private void getPermissions() {
        if (!permissionsGranted()) {
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    private boolean permissionsGranted() {
        boolean location = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        boolean writeStorage = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
        return location && writeStorage;
    }

    private void updateUI(Data data) {
        currentSpeedTV.setText(round(data.getCurrentSpeed()));
        averageSpeedTV.setText(round(data.getAverageSpeed()));
        maxSpeedTV.setText(round(data.getMaxSpeed()));
        distanceTV.setText(round(data.getDistance()));
        timeTV.setText(data.getTime());
    }
}
