package com.android.compass.util.ui

import androidx.lifecycle.ViewModel

/**
 * Created by Noble on 7/26/2019.
 */
open class BaseViewModel<N : Navigator> : ViewModel() {
    lateinit var navigator: N
}