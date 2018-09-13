package awersching.gammelgps.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

class Permissions(private val context: Activity) {

    fun getPermissions() {
        if (!permissionsGranted()) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(context, permissions, 0)
        }
    }

    fun permissionsGranted(): Boolean {
        val location = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val writeStorage = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return location && writeStorage
    }
}
