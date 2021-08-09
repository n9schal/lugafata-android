package com.nischal.clothingstore.utils.extensions

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity

/**
 * Method to hide softKeyboard
 */
fun FragmentActivity.hideSoftKeyboard() {
    // Check if no view has focus: and hide the soft keyboard
    val view = currentFocus
    //Checking if view!=null
    // to prevent java.lang.NullPointerException: Attempt to invoke virtual method 'android.os.IBinder android.view.View.getWindowToken()' on a null object reference
    view?.let {
        view.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Method for removing the keyboard if touched outside the editable view.
 *
 * @param view root view
 */
fun FragmentActivity.setupUI(view: View?) {
    view?.run {
        //Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                view.clearFocus()
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }
}