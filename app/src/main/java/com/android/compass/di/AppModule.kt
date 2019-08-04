package com.android.compass.di

import com.android.compass.App
import com.android.compass.util.ui.UiNavigator
import com.android.compass.util.ui.compass.ICompassSensorsService
import com.android.compass.util.ui.location.CoordinatesValidator
import com.android.compass.util.ui.location.ILocationService
import com.android.compass.util.ui.location.LocationService
import com.android.compasst.util.ui.compass.CompassSensorsService
import org.koin.dsl.module.module

/**
 * Created by Noble on 7/27/2019.
 */

fun appModule(app: App) = module {
    single { app }
    factory { CompassSensorsService(app) as ICompassSensorsService }
    factory { LocationService(app) as ILocationService }

}

fun commonsModule() = module {
    factory { CoordinatesValidator() }
    single { UiNavigator() }
}

fun allModules(app: App) = listOf(
        appModule(app),
        viewModules,
        commonsModule()
)

