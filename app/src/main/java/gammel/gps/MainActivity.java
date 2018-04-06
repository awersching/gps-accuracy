package gammel.gps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final long INTERVAL = 1000;

    private TextView currentSpeedTV;
    private TextView averageSpeedTV;
    private TextView maxSpeedTV;
    private TextView distanceTV;
    private TextView timeTV;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private ArrayList<Location> locations = new ArrayList<>();
    private float maxSpeed = 0;
    private float distance = 0;
    private long startTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentSpeedTV = (TextView) findViewById(R.id.current_speed_tv);
        averageSpeedTV = (TextView) findViewById(R.id.average_speed_tv);
        maxSpeedTV = (TextView) findViewById(R.id.max_speed_tv);
        distanceTV = (TextView) findViewById(R.id.distance_tv);
        timeTV = (TextView) findViewById(R.id.time_tv);

        getPermissions();

        locationRequest = new LocationRequest()
                .setInterval(INTERVAL)
                .setFastestInterval(INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
        writeLocations();
        writeStats();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exit_menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_menu_item:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        location.setSpeed(Math.round((location.getSpeed() * 3.6) * 100) / 100f);
        if (location.getSpeed() > 0) {
            locations.add(location);

            // current speed
            currentSpeedTV.setText(String.valueOf(location.getSpeed()));

            // average speed
            float sum = 0;
            for (Location loc : locations) {
                sum += loc.getSpeed();
            }
            averageSpeedTV.setText(String.valueOf(
                    Math.round((sum / locations.size()) * 100) / 100f));

            // max speed
            if (maxSpeed < location.getSpeed()) {
                maxSpeed = location.getSpeed();
                maxSpeedTV.setText(String.valueOf(maxSpeed));
            }

            // distance
            if (locations.size() >= 2) {
                distance += locations.get(locations.size() - 2).distanceTo(location) / 1000;
                distanceTV.setText(String.valueOf(
                        Math.round(distance * 100) / 100f));
            }
        }

        // time
        long newTime = System.currentTimeMillis() - startTime;
        long hours = TimeUnit.MILLISECONDS.toHours(newTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(newTime) - hours * 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(newTime) - hours * 60 * 60 - minutes * 60;
        timeTV.setText(String.format("%d:%d:%d", hours, minutes, seconds));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
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

    private void writeLocations() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() +
                    File.separator + "locations.csv");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(file, false));

            outputStreamWriter.write("Speed\tAccuracy\tDistance\tAltitude\n");

            Location prev = locations.isEmpty() ? null : locations.get(0);
            for (Location location : locations) {
                outputStreamWriter.write(location.getSpeed() + "\t" +
                        location.getAccuracy() + "\t" +
                        prev.distanceTo(location) + "\t" +
                        location.getAltitude() + "\n");

                prev = location;
            }

            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStats() {
        if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime) < 30) {
            return;
        }

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() +
                    File.separator + "stats.csv");
            boolean fileExists = file.exists();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(file, true));

            if (!fileExists) {
                outputStreamWriter.write("Date\tDistance\tTime\tMax\tAverage\n");
            }
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            outputStreamWriter.write(dateFormatter.format(new Date()) + "\t" +
                    distanceTV.getText() + "\t" +
                    timeTV.getText() + "\t" +
                    maxSpeedTV.getText() + "\t" +
                    averageSpeedTV.getText() + "\n");

            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
