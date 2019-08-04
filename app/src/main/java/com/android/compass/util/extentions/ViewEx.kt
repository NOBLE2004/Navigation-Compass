package com.android.compass.util.extentions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by Noble on 7/28/2019.
 */

fun EditText.addTextWatcher(
        afterChanged: ((s: Editable) -> Unit)? = null,
        beforeChanged: ((s: CharSequence, start: Int, count: Int, after: Int) -> Unit)? = null,
        onChanged: ((s: CharSequence, start: Int, before: Int, count: Int) -> Unit)? = null
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            afterChanged?.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onChanged?.invoke(s, start, before, count)
        }

    })
}