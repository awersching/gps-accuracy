package awersching.gammelgps.location

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import awersching.gammelgps.R
import awersching.gammelgps.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.Executors

class GPSService : Service() {

    companion object {
        private val TAG = GPSService::class.java.simpleName

        private const val LOCATION_UPDATE = "LOCATION_UPDATE"
        private const val INTERVAL: Long = 1000
    }

    private var locationClient: FusedLocationProviderClient? = null
    private var pendingIntent: PendingIntent? = null

    private var calculation = Calculation()

    private var started = false
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate() {
        super.onCreate()

        locationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            GPS.START -> start()
            LOCATION_UPDATE -> executorService.submit {
                updateLocation(intent)
            }
            GPS.STOP -> stop(intent.extras!!.getBoolean(GPS.SAVE))
        }
        return Service.START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun start() {
        if (started) {
            Log.i(TAG, "Already started")
            return
        }
        started = true

        Log.i(TAG, "Starting location requests with interval $INTERVAL")
        val locationRequest = LocationRequest()
                .setInterval(INTERVAL)
                .setFastestInterval(INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val intent = Intent(this, GPSService::class.java)
                .setAction(LOCATION_UPDATE)
        pendingIntent = PendingIntent
                .getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        locationClient!!.requestLocationUpdates(locationRequest, pendingIntent)
        startForeground()
    }

    private fun startForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = Notification.Builder(this)
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentIntent(pendingIntent).build()
        startForeground(1337, notification)
    }

    private fun updateLocation(locationUpdate: Intent) {
        val location = LocationResult.extractResult(locationUpdate).lastLocation
        if (location == null) {
            return
        }

        val data = calculation.calculate(location)
        val intent = Intent(GPS.BROADCAST)
                .putExtra(GPS.DATA, data)
        sendBroadcast(intent)
    }

    private fun stop(save: Boolean) {
        locationClient!!.removeLocationUpdates(pendingIntent)
        if (save) {
            save()
        }
        executorService.shutdownNow()
        stopSelf()
        started = false
        Log.i(TAG, "Stopped GPS service")
    }

    private fun save() {
        val csv = CSV(calculation.locations, calculation.lastData!!)
        csv.write()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
