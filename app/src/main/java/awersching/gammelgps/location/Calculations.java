package awersching.gammelgps.location;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Calculations {

    private static String TAG = Calculations.class.getSimpleName();

    private ArrayList<Location> locations = new ArrayList<>();
    private float maxSpeed = 0;
    private float distance = 0;
    private long startTime = System.currentTimeMillis();
    private Data lastData;

    public Data calculate(Location location) {
        Data data = new Data();

        location.setSpeed(Math.round((location.getSpeed() * 3.6) * 100) / 100f);
        if (location.getSpeed() > 0) {
            locations.add(location);

            // current speed
            data.setCurrentSpeed(location.getSpeed());

            // average speed
            float sum = 0;
            for (Location loc : locations) {
                sum += loc.getSpeed();
            }
            data.setAverageSpeed(Math.round((sum / locations.size()) * 100) / 100f);

            // max speed
            if (maxSpeed < location.getSpeed()) {
                maxSpeed = location.getSpeed();
                data.setMaxSpeed(maxSpeed);
            }

            // distance
            if (locations.size() >= 2) {
                distance += locations.get(locations.size() - 2).distanceTo(location) / 1000;
                data.setDistance(Math.round(distance * 100) / 100f);
            }
        }

        // time
        long newTime = System.currentTimeMillis() - startTime;
        long hours = TimeUnit.MILLISECONDS.toHours(newTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(newTime) - hours * 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(newTime) - hours * 60 * 60 - minutes * 60;
        data.setTime(String.format("%d:%d:%d", hours, minutes, seconds));

        Log.i(TAG, "New data: " + data.toString());
        lastData = data;
        return data;
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
