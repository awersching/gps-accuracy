package awersching.gammelgps.location;

public class Data {

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

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
