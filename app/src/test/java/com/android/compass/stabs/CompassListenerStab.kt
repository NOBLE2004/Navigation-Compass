package com.android.compass.stabs

import  com.android.compasst.util.ui.compass.CompassSensorsService
import org.jetbrains.annotations.TestOnly

/**
 * Created by Noble on 8/03/2019.
 */
@TestOnly
class CompassListenerStab : CompassSensorsService.CompassListener {
    var updatedAzimuth: Float? = null
    var isSensorEventOccurred: Boolean = false

    override fun onCompassSensorsUpdate(azimuth: Float) {
        updatedAzimuth = azimuth
        isSensorEventOccurred = true
    }

    fun resetResults() {
        updatedAzimuth = null
        isSensorEventOccurred = false
    }
}