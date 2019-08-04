package com.android.compass.di

import com.android.compass.ui.compass.CompassViewModel
import com.android.compass.ui.navigateLatLng.NavigateLatLngViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by Noble on 7/27/2019.
 */

val viewModules = module {
    viewModel { CompassViewModel(get(), get()) }
    viewModel { NavigateLatLngViewModel(get()) }
}