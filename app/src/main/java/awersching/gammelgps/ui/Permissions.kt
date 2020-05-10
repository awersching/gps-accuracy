package awersching.gammelgps.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class Permissions(private val ctx: Activity) {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )

    fun request() {
        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(ctx, permissions, 0)
        }
    }

    private fun permissionsGranted(): Boolean {
        var granted = true
        for (permission in permissions) {
            granted = granted && ActivityCompat.checkSelfPermission(ctx, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
        return granted
    }
}
