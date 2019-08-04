package com.android.compass.util.ui.compass

import android.location.Location
import com.android.compasst.util.ui.compass.CompassSensorsService

/**
 * Created by Noble on 7/27/2019.
 */
interface ICompassSensorsService {
    fun startListeningSensors(listener: CompassSensorsService.CompassListener)
    fun stopListeningSensors()
    fun calculateCoordinatesAzFromNorthAz(
            azimuth: Float,
            startLocation: Location,
            destinationLocation: Location
    ): Float
}