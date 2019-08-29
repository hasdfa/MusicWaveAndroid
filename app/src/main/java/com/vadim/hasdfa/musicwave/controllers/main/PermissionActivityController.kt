package com.vadim.hasdfa.musicwave.controllers.main

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vadim.hasdfa.musicwave.base.IActivityController

class PermissionActivityController(
    activity: Activity,
    val permissions: Array<String>
): IActivityController(activity) {

    val isPermissionNotGranted
        get() = !isPermissionGranted

    val isPermissionGranted
        get() = permissions.all { permissionGranted(it) }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity, permissions,200
        )
    }

    fun permissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}