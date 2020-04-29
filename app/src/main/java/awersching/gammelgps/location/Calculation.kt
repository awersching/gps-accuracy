package awersching.gammelgps.location

import android.location.Location
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

class Calculation {
    companion object {
        private val TAG = Calculation::class.java.simpleName
    }

    private var speedSum = 0.0
    private var speedCount = 0.0
    private var maxSpeed = 0.0
    private var distance = 0.0
    private val startTime = System.currentTimeMillis()

    val locations = ArrayList<Location>()
    var lastData: Data? = null

    fun calculate(location: Location): Data {
        val data = Data()
        location.speed = location.speed * 3.6f
        locations.add(location)

        data.currentSpeed = location.speed.toDouble()
        data.averageSpeed = averageSpeed(location)
        if (maxSpeed < location.speed) {
            maxSpeed = location.speed.toDouble()
        }
        data.maxSpeed = maxSpeed
        data.distance = distance(location)
        data.time = time()

        Log.i(TAG, "New data: $data")
        lastData = data
        return data
    }

    private fun averageSpeed(location: Location): Double {
        if (location.speed > 1) {
            speedSum += location.speed.toDouble()
            speedCount++
        }
        if (speedCount == 0.0) {
            return 0.0
        }
        return speedSum / speedCount
    }

    private fun distance(location: Location): Double {
        if (locations.size >= 2) {
            distance += (locations[locations.size - 2].distanceTo(location) / 1000).toDouble()
            return distance
        }
        return 0.0
    }

    private fun time(): String {
        val newTime = System.currentTimeMillis() - startTime
        val hours = TimeUnit.MILLISECONDS.toHours(newTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(newTime) - hours * 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(newTime) - hours * 60 * 60 - minutes * 60
        return String.format("%d:%d:%d", hours, minutes, seconds)
    }
}
