package awersching.gammelgps.location

import android.location.Location
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class Calculation {
    companion object {
        private val TAG = Calculation::class.java.simpleName
    }

    private var speedSum = 0.0
    private var speedCount = 0.0
    private var maxSpeed = 0.0
    private var distance = 0.0
    private var up = 0.0
    private var down = 0.0
    private val startTime = System.currentTimeMillis()

    val locations = ArrayList<Location>()
    var lastData: Data? = null

    fun calculate(location: Location): Data {
        location.speed = location.speed * 3.6f
        setAverageSpeed(location)
        setMaxSpeed(location)
        setDistance(location)
        setUpDown(location)

        val data = Data()
        data.currentSpeed = location.speed.toDouble()
        data.averageSpeed = if (speedCount == 0.0) 0.0 else speedSum / speedCount
        data.maxSpeed = maxSpeed
        data.distance = distance
        data.up = up
        data.down = down
        data.time = time()

        Log.i(TAG, "New data: $data")
        locations.add(location)
        lastData = data
        return data
    }

    private fun setAverageSpeed(location: Location) {
        if (location.speed > 1) {
            speedSum += location.speed.toDouble()
            speedCount++
        }
    }

    private fun setMaxSpeed(location: Location) {
        if (maxSpeed < location.speed) {
            maxSpeed = location.speed.toDouble()
        }
    }

    private fun setDistance(location: Location) {
        if (locations.isNotEmpty()) {
            distance += (locations[locations.size - 1].distanceTo(location) / 1000).toDouble()
        }
    }

    private fun setUpDown(location: Location) {
        if (locations.isEmpty()) {
            return
        }
        if (location.altitude == 0.0 || locations[locations.size - 1].altitude == 0.0) {
            return
        }

        val altitudeDiff = location.altitude - locations[locations.size - 1].altitude
        if (altitudeDiff > 0) {
            up += altitudeDiff
        } else {
            down += abs(altitudeDiff)
        }
    }

    private fun time(): String {
        val newTime = System.currentTimeMillis() - startTime
        val hours = TimeUnit.MILLISECONDS.toHours(newTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(newTime) - hours * 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(newTime) - hours * 60 * 60 - minutes * 60
        return String.format("%d:%d:%d", hours, minutes, seconds)
    }
}
