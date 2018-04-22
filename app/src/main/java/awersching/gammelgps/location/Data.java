package awersching.gammelgps.location;

public class Data {

    private float currentSpeed;
    private float averageSpeed;
    private float maxSpeed;
    private float distance;
    private String time;

    @Override
    public String toString() {
        return "Current speed: " + currentSpeed + "\n" +
                "Average speed: " + averageSpeed + "\n" +
                "Max speed: " + maxSpeed + "\n" +
                "Distance: " + distance + "\n" +
                "Time: " + time;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
