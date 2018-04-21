package awersching.gammelgps.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Observable;

public class GPS {

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;

    public GPS(Context context) {
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public Observable<Data> start(long interval) {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(interval)
                .setFastestInterval(interval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Observable<Data> observable = Observable.create(subscriber -> {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    subscriber.onNext(new Data(locationResult.getLocations()));
                }
            };
        });
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        return observable;
    }

    public void stop() {
        locationClient.removeLocationUpdates(locationCallback);
    }
}
