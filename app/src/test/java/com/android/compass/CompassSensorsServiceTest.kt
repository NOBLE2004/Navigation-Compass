package com.android.compass

import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import com.android.compass.stabs.CompassListenerStab
import  com.android.compasst.util.ui.compass.CompassSensorsService
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSensorManager

/**
 * Created by Noble on 8/03/2019.
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CompassSensorsServiceTest {
    private lateinit var mContext: Context
    private lateinit var mCompassSensorsService: CompassSensorsService
    private lateinit var mShadowSensorManager: ShadowSensorManager
    private lateinit var mCompassListenerStab: CompassListenerStab

    private val mNorthAzimuth = 0.0f
    private val mKrakowLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 50.0647
        longitude = 19.9450
    }
    private val mKievLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 50.4501
        longitude = 30.5234
    }

    @Before
    fun setUp() {
        mContext = RuntimeEnvironment.application.applicationContext
        mShadowSensorManager = shadowOf(mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        mCompassSensorsService = CompassSensorsService(mContext)
        mCompassListenerStab = CompassListenerStab()
    }

    @After
    fun tearDown() {
    }

    /**
     * Not fully precise test, expected azimuth for those 2 location was calculated on trusted7777 website.
     */
    @Test
    fun `calculate coordinates azimuth between two coordinates with north azimuth`() {
        val expectedMaxResultKrakowKievAz = -86.44f + 5
        val expectedMinResultKrakowKievAz = -86.44f - 5
        val result = mCompassSensorsService.calculateCoordinatesAzFromNorthAz(
            mNorthAzimuth,
            mKrakowLocation,
            mKievLocation
        )
        assertThat(
            "azimuth for krakow kiev location calculation results are wrong",
            result,
            allOf(greaterThanOrEqualTo(expectedMinResultKrakowKievAz), lessThanOrEqualTo(expectedMaxResultKrakowKievAz))
        )
    }

    @Test
    fun `check if sensors listeners created`() {
        mCompassSensorsService.startListeningSensors(mCompassListenerStab)
        val isAnyListenerActive = mShadowSensorManager.hasListener(mCompassSensorsService)
        assertTrue("Zero listener are active", isAnyListenerActive)
    }

    @Test
    fun `stop listening to compass sensors`() {
        mCompassSensorsService.startListeningSensors(mCompassListenerStab)
        val isAnyListenerActive = mShadowSensorManager.hasListener(mCompassSensorsService)
        if (isAnyListenerActive) {
            mCompassSensorsService.stopListeningSensors()
            val isNoListenersAreActive = mShadowSensorManager.hasListener(mCompassSensorsService)
            assertFalse("Failed to unregister all listeners", isNoListenersAreActive)
        }
    }

    /**
     * Test, Listener must not get triggered, as ShadowSensorManager createSensorEvent() - creates empty event without type.
     * As in CompassSensorsService we are listening for only chosen sensor events, by logging in onSensorChanged() we can that
     * listener gets triggered.
     */
    @Test
    fun `test if compass listeners gets triggered`() {
        mCompassSensorsService.startListeningSensors(mCompassListenerStab)
        if (mCompassListenerStab.isSensorEventOccurred) {
            mCompassListenerStab.resetResults()
            println("compass listener was reset")
        }
        val sensorEvent = mShadowSensorManager.createSensorEvent()
        mShadowSensorManager.sendSensorEventToListeners(sensorEvent)

        assertFalse("SensorEvent wasn't captured and send to listener", mCompassListenerStab.isSensorEventOccurred)
    }
}