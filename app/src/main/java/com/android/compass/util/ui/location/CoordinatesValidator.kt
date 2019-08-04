package com.android.compass.util.ui.location

/**
 * Created by Noble on 7/28/2019.
 */
class CoordinatesValidator {
    private val mLatitudeRegex = Regex("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$")
    private val mLongitudeRegex = Regex("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$")

    fun isEmpty(coordinate: String) = coordinate.isBlank()

    fun isLatitudeValid(latitude: String): Boolean {
        if (isEmpty(latitude).not()) {
            return mLatitudeRegex.matches(latitude)
        }
        return false
    }

    fun isLongitudeValid(longitude: String): Boolean {
        if (isEmpty(longitude).not()) {
            return mLongitudeRegex.matches(longitude)
        }
        return false
    }
}