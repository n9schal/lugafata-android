package com.nischal.clothingstore.ui.dialogs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.DialogAlertBinding
import com.nischal.clothingstore.utils.Constants.Args.DIALOG_CANCELABLE
import com.nischal.clothingstore.utils.Constants.Args.MESSAGE
import com.nischal.clothingstore.utils.Constants.Args.NEGATIVE_BTN_TXT
import com.nischal.clothingstore.utils.Constants.Args.POSITIVE_BTN_TXT
import com.nischal.clothingstore.utils.Constants.Args.TITLE

class AlertDialog : DialogFragment() {
    private lateinit var binding: DialogAlertBinding
    private var isDismissed: Boolean = false
    private var alertDialogListener: AlertDialogListener? = null

    fun setListener(alertDialogListener: AlertDialogListener) {
        this.alertDialogListener = alertDialogListener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BlurDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAlertBinding.inflate(inflater, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun dismiss() {
        if (isDismissed)
            return
        try {
            super.dismiss()
        } catch (e: IllegalArgumentException) {
        }
        isDismissed = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        binding.tvTitle.text = bundle.getString(TITLE)
        binding.tvBody.text = bundle.getString(MESSAGE)
        binding.tvBtnOk.text = bundle.getString(POSITIVE_BTN_TXT)
        isCancelable = bundle.getBoolean(DIALOG_CANCELABLE)

        if (bundle.getString(NEGATIVE_BTN_TXT).isNullOrBlank()) {
            binding.viewVerticalSeparator.visibility = View.GONE
            binding.tvBtnCancel.visibility = View.GONE
        } else {
            binding.viewVerticalSeparator.visibility = View.VISIBLE
            binding.tvBtnCancel.visibility = View.VISIBLE
            binding.tvBtnCancel.text = bundle.getString(NEGATIVE_BTN_TXT)
        }

        binding.tvBtnOk.setOnClickListener {
            dialog?.dismiss()
            alertDialogListener?.onOkBtnClicked()
        }

        binding.tvBtnCancel.setOnClickListener {
            dialog?.dismiss()
            alertDialogListener?.onCancelBtnClicked()
        }

    }

    companion object {
        fun showBlurDialog(fragmentManager: FragmentManager,
                           title: String,
                           msg: String,
                           tag: String,
                           positiveBtnText: String,
                           negativeBtnText: String?,
                           cancelable: Boolean,
                           positiveBtnClicked: () -> Unit = {},
                           negativeBtnClicked: () -> Unit = {}
        ) {
            val blurDialog = AlertDialog()
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            bundle.putString(MESSAGE, msg)
            bundle.putString(POSITIVE_BTN_TXT, positiveBtnText)
            bundle.putString(NEGATIVE_BTN_TXT, negativeBtnText)
            bundle.putBoolean(DIALOG_CANCELABLE, cancelable)
            blurDialog.arguments = bundle
            blurDialog.setListener(object : AlertDialogListener {
                override fun onOkBtnClicked() {
                    positiveBtnClicked()
                }

                override fun onCancelBtnClicked() {
                    negativeBtnClicked()
                }
            })
            if (fragmentManager.findFragmentByTag(tag) != null) {
                (fragmentManager.findFragmentByTag(tag) as DialogFragment).dismiss()
            }
            blurDialog.show(fragmentManager, tag)
        }
    }

    interface AlertDialogListener {
        fun onOkBtnClicked()
        fun onCancelBtnClicked()
    }
}