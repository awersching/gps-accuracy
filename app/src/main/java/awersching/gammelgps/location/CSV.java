package awersching.gammelgps.location;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CSV {

    private static String TAG = CSV.class.getSimpleName();

    private List<Location> locations;
    private long startTime;
    private Data lastData;

    public CSV(List<Location> locations, long startTime, Data lastData) {
        this.locations = locations;
        this.startTime = startTime;
        this.lastData = lastData;
    }

    public void write() {
        Log.i(TAG, "Writing CSV");
        writeLocations();
        writeStats();
        Log.i(TAG, "Wrote CSV");
    }

    private void writeLocations() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() +
                    File.separator + "locations.csv");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(file, false));

            outputStreamWriter.write("Speed\tAccuracy\tDistance\tAltitude\n");

            Location prev = locations.isEmpty() ? null : locations.get(0);
            for (Location location : locations) {
                outputStreamWriter.write(location.getSpeed() + "\t" +
                        location.getAccuracy() + "\t" +
                        prev.distanceTo(location) + "\t" +
                        location.getAltitude() + "\n");

                prev = location;
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStats() {
        if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime) < 30) {
            return;
        }


        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() +
                    File.separator + "stats.csv");
            boolean fileExists = file.exists();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(file, true));

            if (!fileExists) {
                outputStreamWriter.write("Date\tDistance\tTime\tMax\tAverage\n");
            }
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            outputStreamWriter.write(dateFormatter.format(new Date()) + "\t" +
                    lastData.getDistance() + "\t" +
                    lastData.getTime() + "\t" +
                    lastData.getMaxSpeed() + "\t" +
                    lastData.getAverageSpeed() + "\n");
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
