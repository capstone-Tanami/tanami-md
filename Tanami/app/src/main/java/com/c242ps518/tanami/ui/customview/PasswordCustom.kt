package com.c242ps518.tanami.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewParent
import com.c242ps518.tanami.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordCustom @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val password = s.toString()
                val parentLayout = findParentTextInputLayout()
                when {
                    password.isBlank() -> {
                        parentLayout?.error = null
                        parentLayout?.isErrorEnabled = false
                    }
                    password.length < 8 -> parentLayout?.error = resources.getString(R.string.error_short_password)
                    else -> {
                        parentLayout?.error = null
                        parentLayout?.isErrorEnabled = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun findParentTextInputLayout(): TextInputLayout? {
        var parentView: ViewParent? = parent
        while (parentView != null) {
            if (parentView is TextInputLayout) {
                return parentView
            }
            parentView = (parentView as? ViewParent)?.parent
        }
        return null
    }
}