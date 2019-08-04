package com.android.compass.ui.navigateLatLng

import android.location.Location
import android.location.LocationManager
import androidx.annotation.UiThread
import com.android.compass.util.ui.BaseViewModel
import com.android.compass.util.ui.location.CoordinatesValidator
import timber.log.Timber

/**
 *Created by Noble on 7/27/2019.
 */
class NavigateLatLngViewModel(private val validator: CoordinatesValidator) : BaseViewModel<NavigateLatLngNavigator>() {

    //region view
    @UiThread
    fun acceptAndNavigate() {
        Timber.i("acceptAndNavigate")
        val latitude = navigator.getLatitudeInputText()
        val longitude = navigator.getLongitudeInputText()
        if (validateLatitudeInput(latitude) && validateLongitudeInput(longitude)) {
            try {
                val destinationLocation = Location(LocationManager.GPS_PROVIDER).apply {
                    this.latitude = latitude.toDouble()
                    this.longitude = longitude.toDouble()
                }
                navigator.finishFillingInputs()
                navigator.setCompassModeCoordinates(destinationLocation)
            } catch (e: NumberFormatException) {
                Timber.e(e, "Error during parsing lat lng String to Double")
                navigator.showErrorParsingLatLng()
            }
        }
    }

    @UiThread
    fun navigateToNorth() {
        navigator.finishFillingInputs()
        navigator.setCompassModeNorth()
    }

    @UiThread
    fun closeDialog() {
        navigator.back()
    }
    //endregion

    //region validation
    fun validateLatitudeInput(latitude: String): Boolean {
        return when {
            validator.isEmpty(latitude) -> {
                navigator.showErrorLatitudeEmpty()
                false
            }
            validator.isLatitudeValid(latitude).not() -> {
                navigator.showErrorLatitudeWrongInput()
                false
            }
            else -> {
                navigator.hideLatitudeErrorText()
                true
            }
        }
    }

    fun validateLongitudeInput(longitude: String): Boolean {
        return when {
            validator.isEmpty(longitude) -> {
                navigator.showErrorLongitudeEmpty()
                false
            }
            validator.isLongitudeValid(longitude).not() -> {
                navigator.showErrorLongitudeWrongInput()
                false
            }
            else -> {
                navigator.hideLongitudeErrorText()
                true
            }
        }
    }
    //endregion
}