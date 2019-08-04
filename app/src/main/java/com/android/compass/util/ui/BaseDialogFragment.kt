package com.android.compass.util.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import com.android.compass.util.extentions.hideKeyboardFrom
import com.google.android.material.textfield.TextInputEditText

/**
 * Created by Noble on 7/26/2019.
 */
abstract class BaseDialogFragment : DialogFragment() {
    abstract var items: Array<TextInputEditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackClick()
        initializeEditTextViews()
    }

    fun onBack() {
        hideKeyboardAndCursors()
        dismiss()
    }

    fun hideKeyboardAndCursors() {
        items.forEach {
            it.isFocusable = false
        }
        items.firstOrNull()?.let { view ->
            context?.hideKeyboardFrom(view)
        }
    }

    private fun initializeEditTextViews() {
        items.forEach {
            it.setOnTouchListener { v, event ->
                it.isFocusableInTouchMode = true
                return@setOnTouchListener false
            }
        }
    }

    private fun handleBackClick() {
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onBack()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }
}