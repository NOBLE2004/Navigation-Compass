package com.android.compass

import android.app.Application
import com.android.compass.BuildConfig
import com.android.compass.di.allModules
import com.github.bskierys.pine.Pine
import org.koin.android.ext.android.startKoin
import timber.log.Timber

/**
 * Created by Noble on 7/26/2019.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initializeKoin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            val pine = Pine.Builder()
            pine.setTagFormatter { "CustomLog $it" }
            Timber.plant(pine.grow())
        }
    }

    private fun initializeKoin() {
        startKoin(this, allModules(this))
    }
}