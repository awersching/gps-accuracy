package awersching.gammelgps.location;

import android.location.Location;

import java.util.List;

public class Data {

    private float currentSpeed;
    private float averageSpeed;
    private float maxSpeed;
    private float distance;
    private String time;

    public Data(List<Location> locations) {
        System.out.println(locations);
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }
}
