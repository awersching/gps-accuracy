package awersching.gammelgps.location

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import awersching.gammelgps.R
import awersching.gammelgps.ui.MainActivity
import com.google.android.gms.location.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.Executors

class GpsService : Service() {
    companion object {
        private val TAG = GpsService::class.java.simpleName
        private const val INTERVAL: Long = 1000
    }

    private var started = false
    private val binder = GpsBinder()
    private var locationClient: FusedLocationProviderClient? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            executorService.submit { updateLocation(locationResult) }
        }
    }
    private val publishSubject = PublishSubject.create<Data>()
    private val calculation = Calculation()
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (started) {
            Log.i(TAG, "Already started")
            return START_STICKY
        }
        started = true

        Log.i(TAG, "Starting location requests with interval $INTERVAL")
        val locationRequest = LocationRequest()
            .setInterval(INTERVAL)
            .setFastestInterval(INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
        startForeground()
        return START_STICKY
    }

    fun stop(save: Boolean) {
        locationClient!!.removeLocationUpdates(locationCallback)
        if (save) {
            val csv = CSV(this)
            csv.write(calculation.locations, calculation.lastData)
        }
        executorService.shutdownNow()
        stopSelf()
        started = false
        Log.i(TAG, "Stopped GPS service")
    }

    fun locationUpdates(): Observable<Data> {
        return publishSubject
    }

    private fun startForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val channelId = createNotificationChannel()

        val notification = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1337, notification)
    }

    private fun createNotificationChannel(): String {
        val name = resources.getString(R.string.app_name)
        val channel = NotificationChannel(name, name, NotificationManager.IMPORTANCE_DEFAULT)
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return name
    }

    private fun updateLocation(locationResult: LocationResult?) {
        if (locationResult != null) {
            val data = calculation.calculate(locationResult.lastLocation)
            publishSubject.onNext(data)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class GpsBinder : Binder() {
        fun get(): GpsService = this@GpsService
    }
}
