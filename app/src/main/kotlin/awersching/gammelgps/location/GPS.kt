package awersching.gammelgps.location

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.github.karczews.rxbroadcastreceiver.RxBroadcastReceivers
import io.reactivex.Observable

class GPS(private val context: Context) {

    companion object {
        const val START = "START"
        const val STOP = "STOP"
        const val SAVE = "SAVE"

        const val BROADCAST = "BROADCAST"
        const val DATA = "DATA"
    }

    fun start(): Observable<Data> {
        val intent = Intent(context, GPSService::class.java)
                .setAction(START)
        context.startService(intent)

        return RxBroadcastReceivers.fromIntentFilter(context, IntentFilter(BROADCAST))
                .map { it.extras.get(DATA) as Data }
    }

    fun stop(save: Boolean) {
        val intent = Intent(context, GPSService::class.java)
                .setAction(STOP)
                .putExtra(SAVE, save)
        context.startService(intent)
    }
}
