package awersching.gammelgps.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import awersching.gammelgps.location.Round.round
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class CSV(private val ctx: Context) {
    companion object {
        private val TAG = CSV::class.java.simpleName
    }

    fun write(locations: List<Location>, stats: Data?) {
        if (locations.isEmpty() || stats == null) {
            Log.i(TAG, "No data to write")
            return
        }
        Log.i(TAG, "Writing CSV")
        write(locations)
        write(stats)
        Log.i(TAG, "Wrote CSV")
    }

    private fun write(locations: List<Location>) {
        try {
            val path = ctx.getExternalFilesDir(null)?.path
            val file = File(path + File.separator + "locations.csv")
            val outputStreamWriter = OutputStreamWriter(FileOutputStream(file, false))
            outputStreamWriter.write("Speed\tAccuracy\tDistance\tAltitude\n")

            var prev: Location? = if (locations.isEmpty()) null else locations[0]
            for (location in locations) {
                outputStreamWriter.write(
                    location.speed.toString() + "\t" +
                            location.accuracy + "\t" +
                            prev!!.distanceTo(location) + "\t" +
                            location.altitude + "\n"
                )
                prev = location
            }
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun write(stats: Data) {
        try {
            val path = ctx.getExternalFilesDir(null)?.path
            val file = File(path + File.separator + "stats.csv")
            val fileExists = file.exists()
            val outputStreamWriter = OutputStreamWriter(FileOutputStream(file, true))
            if (!fileExists) {
                outputStreamWriter.write("Date\tDistance\tTime\tMax\tAverage\tUp\tDown\n")
            }

            val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
            outputStreamWriter.write(
                dateFormatter.format(Date()) + "\t" +
                        round(stats.distance) + "\t" +
                        stats.time + "\t" +
                        round(stats.maxSpeed) + "\t" +
                        round(stats.averageSpeed) + "\t" +
                        round(stats.up) + "\t" +
                        round(stats.down) + "\n"
            )
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        }
    }
}
