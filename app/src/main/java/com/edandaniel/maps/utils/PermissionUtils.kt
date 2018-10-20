package com.edandaniel.maps.utils


import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object PermissionUtils {

    fun validatePermission(permissons: Array<String>, activity: Activity, requestCode: Int): Boolean {
        if(Build.VERSION.SDK_INT >= 23) {

            val permissionList = ArrayList<String>()

            for(permission in permissons) {
                val hasNoPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                if(!hasNoPermission ) permissionList.add(permission)
            }

            if(permissionList.isEmpty()) return true
            val newPermissions = permissionList.toTypedArray()//arrayOfNulls<String>(permissionList.size)
            permissionList.toTypedArray()
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode)
        }
        return true
    }
}
