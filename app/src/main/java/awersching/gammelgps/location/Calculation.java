package awersching.gammelgps.location;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Calculation {

    private static String TAG = Calculation.class.getSimpleName();

    private double speedSum = 0;
    private double speedCount = 0;
    private double maxSpeed = 0;
    private double distance = 0;
    private long startTime = System.currentTimeMillis();

    private ArrayList<Location> locations = new ArrayList<>();
    private Data lastData;

    public Data calculate(Location location) {
        Data data = new Data();
        location.setSpeed(location.getSpeed() * 3.6f);
        locations.add(location);

        data.setCurrentSpeed(location.getSpeed());
        data.setAverageSpeed(calculateAverageSpeed(location));
        if (maxSpeed < location.getSpeed()) {
            maxSpeed = location.getSpeed();
        }
        data.setMaxSpeed(maxSpeed);
        data.setDistance(calculateDistance(location));
        data.setTime(calculateTime());

        Log.i(TAG, "New data: " + data.toString());
        lastData = data;
        return data;
    }

    private double calculateAverageSpeed(Location location) {
        speedSum += location.getSpeed();
        speedCount++;
        return speedSum / speedCount;
    }

    private double calculateDistance(Location location) {
        if (locations.size() >= 2) {
            distance += locations.get(locations.size() - 2).distanceTo(location) / 1000;
            return distance;
        }
        return 0;
    }

    private String calculateTime() {
        long newTime = System.currentTimeMillis() - startTime;
        long hours = TimeUnit.MILLISECONDS.toHours(newTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(newTime) - hours * 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(newTime) - hours * 60 * 60 - minutes * 60;
        return String.format("%d:%d:%d", hours, minutes, seconds);
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public Data getLastData() {
        return lastData;
    }
}
