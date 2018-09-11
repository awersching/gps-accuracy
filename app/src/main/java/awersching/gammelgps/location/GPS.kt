package awersching.gammelgps.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import io.reactivex.Observable

class GPS(private val context: Context) {

    companion object {
        val START = "START"
        val STOP = "STOP"
        val SAVE = "SAVE"

        val BROADCAST = "BROADCAST"
        val DATA = "DATA"
    }

    private var receiver: BroadcastReceiver? = null

    fun start(): Observable<Data> {
        val intent = Intent(context, GPSService::class.java)
                .setAction(START)
        context.startService(intent)

        return Observable.create { subscriber ->
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    subscriber.onNext(intent.extras!!.get(DATA) as Data)
                }
            }
            context.registerReceiver(receiver, IntentFilter(BROADCAST))
        }
    }

    fun stop(save: Boolean) {
        val intent = Intent(context, GPSService::class.java)
                .setAction(STOP)
                .putExtra(SAVE, save)
        context.startService(intent)
    }

    fun pause() {
        context.unregisterReceiver(receiver)
    }
}
