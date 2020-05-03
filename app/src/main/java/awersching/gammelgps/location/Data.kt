package awersching.gammelgps.location

import java.io.Serializable

class Data : Serializable {
    var currentSpeed = 0.0
    var averageSpeed = 0.0
    var maxSpeed = 0.0
    var distance = 0.0
    var up = 0.0
    var down = 0.0
    var time = ""

    override fun toString(): String {
        return "Current speed: $currentSpeed\n" +
                "Average speed: $averageSpeed\n" +
                "Max speed: $maxSpeed\n" +
                "Distance: $distance\n" +
                "Up: $up\n" +
                "Down: $down\n" +
                "Time: $time"
    }
}
