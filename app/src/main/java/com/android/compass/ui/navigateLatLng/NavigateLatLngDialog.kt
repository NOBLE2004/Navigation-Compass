package com.android.compass.ui.navigateLatLng

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.compass.R
import com.android.compass.databinding.DialogNavigationLatLngBinding
import com.android.compass.util.extentions.addTextWatcher
import com.android.compass.util.ui.BaseDialogFragment
import com.google.android.material.textfield.TextInputEditText
import org.jetbrains.anko.longToast
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Created by Noble on 7/27/2019.
 */

class NavigateLatLngDialog : BaseDialogFragment(), NavigateLatLngNavigator {
    override var items: Array<TextInputEditText> = emptyArray()
    internal lateinit var mActivityCallback: OnNavigationChangedListener

    private lateinit var mBinding: DialogNavigationLatLngBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_navigation_lat_lng, container, false)
        with(mBinding) {
            lifecycleOwner = this@NavigateLatLngDialog
            viewModel = getViewModel<NavigateLatLngViewModel>().apply {
                navigator = this@NavigateLatLngDialog
            }

            items = arrayOf(navigateLatitudeTied, navigateLongitudeTied)
            executePendingBindings()
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        initInputViewsTextWatchers()
    }

    //region viewModel navigation
    override fun finishFillingInputs() = hideKeyboardAndCursors()

    override fun getLatitudeInputText() = mBinding.navigateLatitudeTied.text?.toString() ?: ""

    override fun getLongitudeInputText() = mBinding.navigateLongitudeTied.text?.toString() ?: ""

    override fun showErrorLatitudeEmpty() {
        mBinding.navigateLatitudeTil.error = context?.getString(R.string.navigate_error_empty_input)
    }

    override fun showErrorLatitudeWrongInput() {
        mBinding.navigateLatitudeTil.error = context?.getString(R.string.navigate_error_wrong_input)
    }

    override fun hideLatitudeErrorText() {
        mBinding.navigateLatitudeTil.error = null
    }

    override fun showErrorLongitudeEmpty() {
        mBinding.navigateLongitudeTil.error = context?.getString(R.string.navigate_error_empty_input)
    }

    override fun showErrorLongitudeWrongInput() {
        mBinding.navigateLongitudeTil.error = context?.getString(R.string.navigate_error_wrong_input)
    }

    override fun hideLongitudeErrorText() {
        mBinding.navigateLongitudeTil.error = null
    }

    override fun setCompassModeNorth() {
        mActivityCallback.setCompassModeNorth()
        dismiss()
    }

    override fun setCompassModeCoordinates(location: Location) {
        mActivityCallback.setCompassModeCoordinates(location)
        dismiss()
    }

    override fun showErrorParsingLatLng() {
        activity?.longToast(R.string.navigate_error_parsing_lat_lng)
    }
    //endregion

    override fun back() {
        onBack()
    }

    //region init
    private fun initInputViewsTextWatchers() {
        with(mBinding) {
            navigateLatitudeTied.addTextWatcher(afterChanged = { viewModel?.validateLatitudeInput(it.toString()) })
            navigateLongitudeTied.addTextWatcher(afterChanged = { viewModel?.validateLongitudeInput(it.toString()) })
        }
    }
    //endregion

    interface OnNavigationChangedListener {
        fun setCompassModeNorth()
        fun setCompassModeCoordinates(location: Location)
    }
}