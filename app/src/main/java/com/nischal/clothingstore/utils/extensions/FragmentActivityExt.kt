package com.nischal.clothingstore.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.nischal.clothingstore.R
import com.nischal.clothingstore.ui.dialogs.AddToBagDialog
import com.nischal.clothingstore.ui.dialogs.AlertDialog
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.utils.Constants

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

fun FragmentActivity.showCustomAlertDialog(
    context: Activity,
    title: String = context.resources.getString(R.string.app_name),
    message: String,
    tag: String = Constants.Strings.DIALOG_TAG,
    positiveBtnText: String = Constants.Strings.OK,
    negativeBtnText: String? = Constants.Strings.CANCEL,
    cancelable: Boolean = true,
    positiveBtnClicked: () -> Unit = {},
    negativeBtnClicked: () -> Unit = {}
) {
    AlertDialog.showBlurDialog(
        fragmentManager = (context as FragmentActivity).supportFragmentManager,
        title = title,
        msg = message,
        tag = tag,
        positiveBtnText = positiveBtnText,
        negativeBtnText = negativeBtnText,
        cancelable = cancelable,
        positiveBtnClicked = positiveBtnClicked,
        negativeBtnClicked = negativeBtnClicked
    )
}

fun FragmentActivity.showAddToBagDialog(
    context: Activity,
    tag: String = Constants.Strings.DIALOG_TAG,
    productVariant: ProductVariant,
    addBtnClicked: (productVariant: ProductVariant) -> Unit
){
    AddToBagDialog.showAddToBagDialog(
        fragmentManager = (context as FragmentActivity).supportFragmentManager,
        tag = tag,
        productVariant = productVariant,
        addBtnClicked = addBtnClicked
    )
}