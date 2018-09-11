package awersching.gammelgps.location

import android.location.Location
import android.os.Environment
import android.util.Log
import awersching.gammelgps.util.Util.round
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class CSV(private val locations: List<Location>, private val lastData: Data) {

    companion object {
        private val TAG = CSV::class.java.simpleName
    }

    fun write() {
        if (locations.isEmpty()) {
            Log.i(TAG, "No data to write")
            return
        }
        Log.i(TAG, "Writing CSV")
        writeLocations()
        writeStats()
        Log.i(TAG, "Wrote CSV")
    }

    private fun writeLocations() {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).path +
                    File.separator + "locations.csv")
            val outputStreamWriter = OutputStreamWriter(
                    FileOutputStream(file, false))

            outputStreamWriter.write("Speed\tAccuracy\tDistance\tAltitude\n")

            var prev: Location? = if (locations.isEmpty()) null else locations[0]
            for (location in locations) {
                outputStreamWriter.write(location.speed.toString() + "\t" +
                        location.accuracy + "\t" +
                        prev!!.distanceTo(location) + "\t" +
                        location.altitude + "\n")
                prev = location
            }
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        }

    }

    private fun writeStats() {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).path +
                    File.separator + "stats.csv")
            val fileExists = file.exists()
            val outputStreamWriter = OutputStreamWriter(
                    FileOutputStream(file, true))

            if (!fileExists) {
                outputStreamWriter.write("Date\tDistance\tTime\tMax\tAverage\n")
            }
            val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
            outputStreamWriter.write(dateFormatter.format(Date()) + "\t" +
                    round(lastData.distance) + "\t" +
                    lastData.time + "\t" +
                    round(lastData.maxSpeed) + "\t" +
                    round(lastData.averageSpeed) + "\n")
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        }

    }
}
