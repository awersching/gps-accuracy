package awersching.gammelgps.location

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import awersching.gammelgps.R
import awersching.gammelgps.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.Executors

class GpsService : Service() {
    companion object {
        private val TAG = GpsService::class.java.simpleName
        private const val INTERVAL: Long = 1000
    }

    enum class Actions {
        LOCATION_UPDATE
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
            Gps.Actions.START.toString() -> start()
            Actions.LOCATION_UPDATE.toString() -> executorService.submit { updateLocation(intent) }
            Gps.Actions.STOP.toString() ->
                stop(intent.extras!!.getBoolean(Gps.Actions.SAVE.toString()))
        }
        return START_NOT_STICKY
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

        val intent = Intent(this, GpsService::class.java)
            .setAction(Actions.LOCATION_UPDATE.toString())
        pendingIntent = PendingIntent
            .getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        locationClient!!.requestLocationUpdates(locationRequest, pendingIntent)
        startForeground()
    }

    private fun startForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val channelId = createNotificationChannel()

        val notification = Notification.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1337, notification)
    }

    private fun createNotificationChannel(): String {
        val name = resources.getString(R.string.app_name)
        val chan = NotificationChannel(name, name, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return name
    }

    private fun updateLocation(locationUpdate: Intent) {
        val location = LocationResult.extractResult(locationUpdate).lastLocation ?: return
        val data = calculation.calculate(location)
        val intent = Intent(Gps.Actions.BROADCAST.toString())
            .putExtra(Gps.Actions.DATA.toString(), data)
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
        val csv = CSV(this)
        csv.write(calculation.locations, calculation.lastData!!)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
