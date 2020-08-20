package com.timenoteco.timenote.androidView

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled

internal fun MaterialDialog.invalidateInputMaxLength(allowEmpty: Boolean) {
    val currentLength = getInputField().text?.length ?: 0
    if (!allowEmpty && currentLength == 0) {
        return
    }
    val maxLength = getInputLayout().counterMaxLength
    if (maxLength > 0) {
        setActionButtonEnabled(POSITIVE, currentLength <= maxLength)
    }
}

internal fun MaterialDialog.showKeyboardIfApplicable() {
    getInputField().postRun {
        requestFocus()
        val imm =
            windowContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

internal inline fun <T : View> T.postRun(crossinline exec: T.() -> Unit) = this.post {
    this.exec()
}