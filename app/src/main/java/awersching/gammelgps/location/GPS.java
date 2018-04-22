package awersching.gammelgps.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Observable;

public class GPS {

    private static String TAG = GPS.class.getSimpleName();

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;

    private Calculations calculations;

    public GPS(Context context) {
        locationClient = LocationServices.getFusedLocationProviderClient(context);
        calculations = new Calculations();
    }

    @SuppressLint("MissingPermission")
    public Observable<Data> start(long interval) {
        Log.i(TAG, "Starting location requests with interval " + interval);
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(interval)
                .setFastestInterval(interval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return Observable.create(subscriber -> {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Data data = calculations.calculate(locationResult.getLastLocation());
                    subscriber.onNext(data);
                }
            };
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            Log.i(TAG, "Started location requests");
        });
    }

    public void stop() {
        Log.i(TAG, "Stopping location requests");
        locationClient.removeLocationUpdates(locationCallback);
        CSV csv = new CSV(calculations.getLocations(),
                calculations.getStartTime(),
                calculations.getLastData());
        csv.write();
        Log.i(TAG, "Stopped location requests");
    }
}
