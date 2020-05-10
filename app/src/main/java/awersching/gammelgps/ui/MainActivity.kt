package awersching.gammelgps.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import awersching.gammelgps.R
import awersching.gammelgps.location.Data
import awersching.gammelgps.location.GpsService
import awersching.gammelgps.location.Round.round
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var gpsService: GpsService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as GpsService.GpsBinder
            gpsService = binder.get()
            val intent = Intent(this@MainActivity, GpsService::class.java)
            startForegroundService(intent)
            gpsService.locationUpdates().subscribe { data -> runOnUiThread { updateUI(data) } }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Permissions(this).request()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, GpsService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(connection)
        super.onStop()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_menu_item -> {
                gpsService.stop(false)
                finish()
                return true
            }
            R.id.save_exit_menu_item -> {
                gpsService.stop(true)
                finish()
                return true
            }
        }
        return false
    }

    private fun updateUI(data: Data) {
        currentSpeedTV.text = round(data.currentSpeed)
        averageSpeedTV.text = round(data.averageSpeed)
        maxSpeedTV.text = round(data.maxSpeed)
        distanceTV.text = round(data.distance)
        timeTV.text = data.time
    }
}
