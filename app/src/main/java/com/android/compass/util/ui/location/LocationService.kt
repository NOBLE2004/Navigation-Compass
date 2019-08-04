package com.android.compass.util.ui.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import timber.log.Timber

/**
 * Created by Noble on 7/27/2019.
 */
class LocationService(private val context: Context) : LocationCallback(), ILocationService {
    companion object {
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 1000L

        /**
         * location updates will be received if another app is requesting
         * the locations faster than your app can handle
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500L
    }

    private var mListener: LocationServiceListener? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private val mLocationCallback: LocationCallback = this
    private var mIsLocationActive = false

    init {
        mIsLocationActive = false
        initLocationClient()
    }

    //region LocationCallback
    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        Timber.i("onLocationResult ${locationResult?.lastLocation?.latitude} + ${locationResult?.lastLocation?.longitude}")
        locationResult?.lastLocation?.let {
            mListener?.onLocationUpdates(it)
        }
    }
    //endregion

    //region ILocationService
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun startLocationUpdates(listener: LocationServiceListener) {
        if (mIsLocationActive.not()) {
            mListener = listener
            startListeningLocationUpdates()
        }
    }

    override fun stopLocationsUpdates() {
        if (mIsLocationActive) {
            stopListeningLocationUpdates()
        }
    }

    override fun getDefaultLocationOb() = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 0.0
        longitude = 0.0
    }

    override fun convertLocationToString(loc: Location): String {
        return Location.convert(loc.latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(loc.longitude, Location.FORMAT_DEGREES)
    }
    //endregion

    private fun initLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mSettingsClient = LocationServices.getSettingsClient(context)
        mLocationRequest = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mLocationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .build()
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun startListeningLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener {
                    mIsLocationActive = true
                    mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                    )
                }
                .addOnFailureListener { exception ->
                    when ((exception as ApiException).statusCode) {
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            Timber.e(exception, "Error in listening for location updates")
                            mListener?.locationListenerFailure()
                        }
                        else -> Timber.e(exception, "Error in listening for location updates")
                    }
                }
    }

    private fun stopListeningLocationUpdates() {
        mIsLocationActive = false
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    interface LocationServiceListener {
        fun locationListenerFailure()
        fun onLocationUpdates(location: Location)
    }
}