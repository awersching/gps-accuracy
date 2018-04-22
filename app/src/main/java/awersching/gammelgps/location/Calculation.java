package awersching.gammelgps.location;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Calculation {

    private static String TAG = Calculation.class.getSimpleName();

    private ArrayList<Location> locations = new ArrayList<>();
    private double maxSpeed = 0;
    private double distance = 0;
    private long startTime = System.currentTimeMillis();
    private Data lastData;

    public Data calculate(Location location) {
        Data data = new Data();
        location.setSpeed(location.getSpeed() * 3.6f);
        locations.add(location);

        data.setCurrentSpeed(round(location.getSpeed()));
        data.setAverageSpeed(calculateAverageSpeed());
        if (maxSpeed < location.getSpeed()) {
            maxSpeed = location.getSpeed();
        }
        data.setMaxSpeed(round(maxSpeed));
        data.setDistance(calculateDistance(location));
        data.setTime(calculateTime());

        Log.i(TAG, "New data: " + data.toString());
        lastData = data;
        return data;
    }

    private double calculateAverageSpeed() {
        double sum = 0;
        for (Location loc : locations) {
            sum += loc.getSpeed();
        }
        return round(sum / locations.size());
    }

    private double calculateDistance(Location location) {
        if (locations.size() >= 2) {
            distance += locations.get(locations.size() - 2).distanceTo(location) / 1000;
            return round(distance);
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

    private double round(double number) {
        return Math.round(number * 100) / 100;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public long getStartTime() {
        return startTime;
    }

    public Data getLastData() {
        return lastData;
    }
}
