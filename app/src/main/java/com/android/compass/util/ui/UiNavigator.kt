package com.android.compass.util.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Created by Noble on 7/27/2019.
 */
class UiNavigator {
    fun getPermissionSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", com.android.compass.BuildConfig.APPLICATION_ID, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}