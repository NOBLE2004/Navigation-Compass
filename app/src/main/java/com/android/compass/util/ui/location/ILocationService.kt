package com.android.compass.util.ui.location

import android.location.Location
import androidx.annotation.RequiresPermission

/**
 * Created by Noble on 7/29/2019.
 */
interface ILocationService {
    @RequiresPermission(value = "android.permission.ACCESS_FINE_LOCATION")
    fun startLocationUpdates(listener: LocationService.LocationServiceListener)

    fun stopLocationsUpdates()
    fun convertLocationToString(loc: Location): String
    fun getDefaultLocationOb(): Location
}