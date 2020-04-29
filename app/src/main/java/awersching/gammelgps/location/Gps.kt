package awersching.gammelgps.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.rxjava3.core.Observable

class Gps(private val context: Context) {
    enum class Actions {
        START,
        STOP,
        SAVE,
        BROADCAST,
        DATA
    }

    private var receiver: BroadcastReceiver? = null

    fun start(): Observable<Data> {
        val intent = Intent(context, GpsService::class.java)
            .setAction(Actions.START.toString())
        context.startForegroundService(intent)

        return Observable.create { subscriber ->
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    subscriber.onNext(intent.extras!!.get(Actions.DATA.toString()) as Data)
                }
            }
            context.registerReceiver(receiver, IntentFilter(Actions.BROADCAST.toString()))
        }
    }

    fun stop(save: Boolean) {
        val intent = Intent(context, GpsService::class.java)
            .setAction(Actions.STOP.toString())
            .putExtra(Actions.SAVE.toString(), save)
        context.startForegroundService(intent)
    }

    fun pause() {
        context.unregisterReceiver(receiver)
    }
}
