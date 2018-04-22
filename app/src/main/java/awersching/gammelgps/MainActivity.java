package awersching.gammelgps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import awersching.gammelgps.location.Data;
import awersching.gammelgps.location.GPS;

public class MainActivity extends AppCompatActivity {

    private static long INTERVAL = 1000;

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
        gps.start(INTERVAL).subscribe(this::updateUI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                gps.stop();
                finish();
                break;
            case R.id.save_exit_menu_item:
                gps.stop();
                gps.save();
                finish();
                break;
        }
        return true;
    }

    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void updateUI(Data data) {
        currentSpeedTV.setText(String.valueOf(data.getCurrentSpeed()));
        averageSpeedTV.setText(String.valueOf(data.getAverageSpeed()));
        maxSpeedTV.setText(String.valueOf(data.getMaxSpeed()));
        distanceTV.setText(String.valueOf(data.getDistance()));
        timeTV.setText(data.getTime());
    }
}
