package com.android.compass.ui.navigateLatLng

import android.location.Location
import com.android.compass.util.ui.Navigator

/**
 * Created by Noble on 7/27/2019.
 */
interface NavigateLatLngNavigator : Navigator {
    fun finishFillingInputs()
    fun showErrorLongitudeEmpty()
    fun showErrorLatitudeEmpty()
    fun showErrorLatitudeWrongInput()
    fun showErrorLongitudeWrongInput()
    fun hideLatitudeErrorText()
    fun hideLongitudeErrorText()
    fun getLongitudeInputText(): String
    fun getLatitudeInputText(): String
    fun setCompassModeNorth()
    fun setCompassModeCoordinates(location: Location)
    fun showErrorParsingLatLng()
}