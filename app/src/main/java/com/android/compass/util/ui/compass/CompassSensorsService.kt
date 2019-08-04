package com.android.compasst.util.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import com.android.compass.util.ui.compass.ICompassSensorsService

/**
 * Created by Noble on 7/27/2019.
 */
class CompassSensorsService(context: Context) : SensorEventListener,
    ICompassSensorsService {
    companion object {
        const val ALPHA = 0.97f
    }

    private var mListener: CompassListener? = null
    private val mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    //region azimuth calculation variables
    private val mOrientationsResultMatrix = FloatArray(3)
    private val mGravityVector = FloatArray(3)
    private val mGeomagneticVector = FloatArray(3)
    private val mRotationMatrix = FloatArray(9)
    private val mInclinationMatrix = FloatArray(9)
    private var mAzimuth: Double = 0.0
    //endregion

    //region ICompassSensorsService
    override fun startListeningSensors(listener: CompassListener) {
        mListener = listener
        val accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        mSensorManager.run {
            registerListener(
                    this@CompassSensorsService,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI
            )
            registerListener(
                    this@CompassSensorsService,
                    magneticFieldSensor,
                    SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    /**
     * Unregister from all the sensor listeners.
     * can be called to save the battery, when compass is not in foreground.
     */
    override fun stopListeningSensors() {
        mSensorManager.unregisterListener(this)
    }

    override fun calculateCoordinatesAzFromNorthAz(
            azimuth: Float,
            startLocation: Location,
            destinationLocation: Location
    ): Float = (azimuth - startLocation.bearingTo(destinationLocation))
    //endregion


    //region SensorEventListener
    override fun onSensorChanged(event: SensorEvent) {
        //println("For CompassSensorsServiceTest onSensorChanged event.sensor.type ${event.sensor?.type}")
        synchronized(this) {
            when (event.sensor?.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> calculateMagneticField(event.values)
                Sensor.TYPE_ACCELEROMETER -> calculateAcceleration(event.values)
                else -> return
            }

            val isRotationMatrixReady = SensorManager.getRotationMatrix(
                    mRotationMatrix,
                    mInclinationMatrix,
                    mGravityVector,
                    mGeomagneticVector
            )
            if (isRotationMatrixReady) {
                SensorManager.getOrientation(mRotationMatrix, mOrientationsResultMatrix)
                mListener?.onCompassSensorsUpdate(calculateNorthAzimuth(mOrientationsResultMatrix[0]).toFloat())
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit
    //endregion


    private fun calculateNorthAzimuth(orientationAzimuth: Float): Double {
        mAzimuth = Math.toDegrees(orientationAzimuth.toDouble())
        mAzimuth = (mAzimuth + 360) % 360
        return mAzimuth
    }

    /**
     * Measures the ambient magnetic field in the X, Y and Z axis.
     */
    private fun calculateMagneticField(magneticFields: FloatArray) {
        val xAxisMagneticFiled = magneticFields[0]
        val yAxisMagneticFiled = magneticFields[1]
        val zAxisMagneticFiled = magneticFields[2]

        mGeomagneticVector[0] = ALPHA * mGeomagneticVector[0] + (1 - ALPHA) * xAxisMagneticFiled
        mGeomagneticVector[1] = ALPHA * mGeomagneticVector[1] + (1 - ALPHA) * yAxisMagneticFiled
        mGeomagneticVector[2] = ALPHA * mGeomagneticVector[2] + (1 - ALPHA) * zAxisMagneticFiled
    }

    /**
     * Measures the acceleration applied to the device
     */
    private fun calculateAcceleration(accelerations: FloatArray) {
        val xAxisAcceleration = accelerations[0]
        val yAxisAcceleration = accelerations[1]
        val zAxisAcceleration = accelerations[2]

        mGravityVector[0] = ALPHA * mGravityVector[0] + (1 - ALPHA) * xAxisAcceleration
        mGravityVector[1] = ALPHA * mGravityVector[1] + (1 - ALPHA) * yAxisAcceleration
        mGravityVector[2] = ALPHA * mGravityVector[2] + (1 - ALPHA) * zAxisAcceleration
    }

    interface CompassListener {
        fun onCompassSensorsUpdate(azimuth: Float)
    }
}