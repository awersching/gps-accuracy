package awersching.gammelgps.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import awersching.gammelgps.R;
import awersching.gammelgps.ui.MainActivity;

public class GPSService extends Service {

    private static final String TAG = GPSService.class.getSimpleName();

    private static final String LOCATION_UPDATE = "LOCATION_UPDATE";
    private static final long INTERVAL = 1000;

    private FusedLocationProviderClient locationClient;
    private PendingIntent pendingIntent;

    private Calculation calculation;

    private boolean started = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        calculation = new Calculation();
        locationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case GPS.START:
                start();
                break;
            case LOCATION_UPDATE:
                executorService.submit(() -> {
                    Location location = intent.getExtras()
                            .getParcelable("com.google.android.location.LOCATION");
                    updateLocation(location);
                });
                break;
            case GPS.STOP:
                stop(intent.getExtras().getBoolean(GPS.SAVE));
                break;
        }
        return START_NOT_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void start() {
        if (started) {
            Log.i(TAG, "Already started");
            return;
        }
        started = true;

        Log.i(TAG, "Starting location requests with interval " + INTERVAL);
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(INTERVAL)
                .setFastestInterval(INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Intent intent = new Intent(this, GPSService.class)
                .setAction(LOCATION_UPDATE);
        pendingIntent = PendingIntent
                .getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        locationClient.requestLocationUpdates(locationRequest, pendingIntent);
        startForeground();
    }

    private void startForeground() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(pendingIntent).build();
        startForeground(1337, notification);
    }

    private void updateLocation(Location location) {
        if (location == null) {
            return;
        }

        Data data = calculation.calculate(location);
        Intent intent = new Intent(GPS.BROADCAST)
                .putExtra(GPS.DATA, data);
        sendBroadcast(intent);
    }

    private void stop(boolean save) {
        locationClient.removeLocationUpdates(pendingIntent);
        if (save) {
            save();
        }
        executorService.shutdownNow();
        stopSelf();
        started = false;
        Log.i(TAG, "Stopped GPS service");
    }

    private void save() {
        CSV csv = new CSV(calculation.getLocations(), calculation.getLastData());
        csv.write();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
