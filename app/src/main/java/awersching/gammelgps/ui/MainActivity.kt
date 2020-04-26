package awersching.gammelgps.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import awersching.gammelgps.R
import awersching.gammelgps.location.Data
import awersching.gammelgps.location.Gps
import awersching.gammelgps.location.Round.round
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val permissions = Permissions(this)
    private val gps = Gps(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions.request()
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        gps.start().subscribe { data ->
            this.updateUI(data)
        }
    }

    override fun onPause() {
        gps.pause()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_menu_item -> {
                gps.stop(false)
                finish()
                return true
            }
            R.id.save_exit_menu_item -> {
                gps.stop(true)
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
