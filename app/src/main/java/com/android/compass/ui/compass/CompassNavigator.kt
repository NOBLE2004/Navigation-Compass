package com.android.compass.ui.compass

import com.android.compass.util.ui.Navigator

/**
 * Created by Noble on 7/27/2019.
 */
interface CompassNavigator : Navigator {
    fun showNavigateLatLngDialog()
    fun askForLocationPermission()
    fun checkLocationPermission(): Boolean
    fun showErrorLocationSetting()
    fun showDistanceToDestinationText(distance: Float, isGettingCloser: Boolean)
    fun showDistanceCalculationNotReady()

}