package awersching.gammelgps.location

import java.io.Serializable

class Data : Serializable {

    var currentSpeed: Double = 0.0
    var averageSpeed: Double = 0.0
    var maxSpeed: Double = 0.0
    var distance: Double = 0.0
    var time: String = ""

    override fun toString(): String {
        return "Current speed: " + currentSpeed + "\n" +
                "Average speed: " + averageSpeed + "\n" +
                "Max speed: " + maxSpeed + "\n" +
                "Distance: " + distance + "\n" +
                "Time: " + time
    }
}
