package awersching.gammelgps.location;

import java.io.Serializable;

public class Data implements Serializable {

    private double currentSpeed;
    private double averageSpeed;
    private double maxSpeed;
    private double distance;
    private String time;

    @Override
    public String toString() {
        return "Current speed: " + currentSpeed + "\n" +
                "Average speed: " + averageSpeed + "\n" +
                "Max speed: " + maxSpeed + "\n" +
                "Distance: " + distance + "\n" +
                "Time: " + time;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public Data setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
        return this;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public Data setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
        return this;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public Data setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public Data setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Data setTime(String time) {
        this.time = time;
        return this;
    }
}
