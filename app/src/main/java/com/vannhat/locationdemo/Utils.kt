package com.vannhat.locationdemo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by VanNhat on 16/07/2019.
 */
object Utils {

    fun createToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun createLog(message: String) {
        Log.d("ccccc", message)
    }

    fun isPermissionGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) return true

        return ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }

    fun requestAppPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            createToast(activity, "explain why we have this permission")
        } else {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    private const val PERMISSION_REQUEST_CODE = 96
}
