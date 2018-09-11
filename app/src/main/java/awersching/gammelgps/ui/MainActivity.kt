package awersching.gammelgps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import awersching.gammelgps.R
import awersching.gammelgps.location.Data
import awersching.gammelgps.location.GPS
import awersching.gammelgps.util.Util.round
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var gps: GPS? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()
        gps = GPS(this)
    }

    override fun onPause() {
        gps!!.pause()
        super.onPause()
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        gps!!.start().subscribe {
            this.updateUI(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_menu_item -> {
                gps!!.stop(false)
                finish()
                return true
            }
            R.id.save_exit_menu_item -> {
                gps!!.stop(true)
                finish()
                return true
            }
        }
        return false
    }

    private fun getPermissions() {
        if (!permissionsGranted()) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    private fun permissionsGranted(): Boolean {
        val location = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val writeStorage = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return location && writeStorage
    }

    private fun updateUI(data: Data) {
        currentSpeedTV.text = round(data.currentSpeed)
        averageSpeedTV.text = round(data.averageSpeed)
        maxSpeedTV.text = round(data.maxSpeed)
        distanceTV.text = round(data.distance)
        timeTV.text = data.time
    }
}
