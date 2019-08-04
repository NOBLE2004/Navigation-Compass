package com.android.compass.ui.compass

import android.location.Location
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import com.android.compass.util.ui.BaseViewModel
import com.android.compass.util.ui.compass.CompassMode

import com.android.compass.util.ui.compass.ICompassSensorsService
import com.android.compass.util.ui.location.ILocationService
import com.android.compass.util.ui.location.LocationService
import com.android.compasst.util.ui.compass.CompassSensorsService
import timber.log.Timber

/**
 * Created by Noble on 7/27/2019.
 */

class CompassViewModel(private val compassUtil: ICompassSensorsService, private val locationUtil: ILocationService) :
        BaseViewModel<CompassNavigator>(), CompassSensorsService.CompassListener, LocationService.LocationServiceListener {
    val azimuthLd = MutableLiveData<Pair<Float, Float>>()
    val destinationLocationLd = MutableLiveData<String>()

    private var mCurrentLocation = locationUtil.getDefaultLocationOb()
    private var mIsCurrentLocationReady = false
    private var mDestinationLocation = locationUtil.getDefaultLocationOb()
    private var mCompassMode = CompassMode.NORTH

    private var mCoordinatesCalculatedAzimuth = 0f
    private var mPreviousAzimuth = Float.MAX_VALUE
    private var mPreviousDistance = 0f

    //region view
    @UiThread
    fun showNavigateLatLngDialog() {
        if (navigator.checkLocationPermission()) {
            navigator.showNavigateLatLngDialog()
        } else {
            navigator.askForLocationPermission()
        }
    }
    //endregion

    //region compass
    override fun onCompassSensorsUpdate(azimuth: Float) {
        when (mCompassMode) {
            CompassMode.NORTH -> {
                azimuthLd.postValue(Pair(mPreviousAzimuth, azimuth))
                mPreviousAzimuth = azimuth
            }
            CompassMode.COORDINATES -> {
                if (mIsCurrentLocationReady) {
                    mCoordinatesCalculatedAzimuth = calculateCoordinatesAzimuth(azimuth)
                    azimuthLd.postValue(Pair(mPreviousAzimuth, mCoordinatesCalculatedAzimuth))
                    mPreviousAzimuth = mCoordinatesCalculatedAzimuth
                }
            }
        }
    }

    fun startCompassSensors() {
        compassUtil.startListeningSensors(this)
    }

    fun stopCompassSensors() {
        compassUtil.stopListeningSensors()
    }
    //endregion

    //region location
    override fun locationListenerFailure() {
        navigator.showErrorLocationSetting()
    }

    override fun onLocationUpdates(location: Location) {
        if (mIsCurrentLocationReady.not()) {
            mIsCurrentLocationReady = true
        }
        mCurrentLocation = location

        updateDistanceToDestinationViews(location)
    }

    fun startListeningToLocation() {
        Timber.i("startListeningToLocation mCompassMode $mCompassMode")
        if (mCompassMode == CompassMode.COORDINATES && navigator.checkLocationPermission()) {
            locationUtil.startLocationUpdates(this)

            if (mIsCurrentLocationReady.not()) {

                navigator.showDistanceCalculationNotReady()
            }
        }
    }

    fun stopListeningToLocation() {
        locationUtil.stopLocationsUpdates()
        mIsCurrentLocationReady = false

    }

    private fun checkIfDistanceDecreases(currentDistance: Float): Boolean {
        return currentDistance < mPreviousDistance
    }

    private fun updateDistanceToDestinationViews(location: Location) {
        val newDistance = location.distanceTo(mDestinationLocation) / 1000
        navigator.showDistanceToDestinationText(newDistance, checkIfDistanceDecreases(newDistance))
        mPreviousDistance = newDistance
    }
    //endregion

    //region change compassMode
    fun changeCompassModeToNorth() {
        mCompassMode = CompassMode.NORTH
        stopListeningToLocation()
    }

    fun changeCompassModeToCoordinates(location: Location) {
        if (mCompassMode == CompassMode.NORTH) {
            mCompassMode = CompassMode.COORDINATES
            startListeningToLocation()
            mDestinationLocation = location
            destinationLocationLd.postValue(locationUtil.convertLocationToString(location))
        }
    }
    //endregion

    private fun calculateCoordinatesAzimuth(northAzimuth: Float) = compassUtil.calculateCoordinatesAzFromNorthAz(
            northAzimuth,
            mCurrentLocation,
            mDestinationLocation
    )
}