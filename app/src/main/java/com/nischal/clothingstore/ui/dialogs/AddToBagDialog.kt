package com.nischal.clothingstore.ui.dialogs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.DialogAddToBagBinding
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Constants.Args.SELECTED_PRODUCT_VARIANT
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddToBagDialog : DialogFragment() {
    private lateinit var binding: DialogAddToBagBinding
    private lateinit var selectedProductVariant: ProductVariant
    private var isDismissed: Boolean = false
    private var addToBagDialogListener: AddToBagDialogListener? = null

    fun setListener(addToBagDialogListener: AddToBagDialogListener){
        this.addToBagDialogListener = addToBagDialogListener
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
        binding = DialogAddToBagBinding.inflate(inflater, container, false)
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
        isCancelable = true
        val bundle = requireArguments()
        selectedProductVariant = bundle.getSerializable(SELECTED_PRODUCT_VARIANT) as ProductVariant
        if(selectedProductVariant.qtyInCart <= 0){
            selectedProductVariant.qtyInCart = 1
        }

        updateViews()
        setupClickListeners()
    }

    private fun updateViews() {
        binding.tvTitle.text = selectedProductVariant.productVariantName
        binding.tvQuantity.text = selectedProductVariant.qtyInCart.toString()
    }

    private fun setupClickListeners() {
        // * setup click listeners
        binding.tvBtnAdd.setOnClickListener {
            addToBagDialogListener?.onAddBtnClicked(selectedProductVariant)
            dialog?.dismiss()
        }
        binding.tvBtnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        binding.tvBtnPlus.setOnClickListener {
            selectedProductVariant.qtyInCart++
            binding.tvQuantity.text = selectedProductVariant.qtyInCart.toString()
        }
        binding.tvBtnMinus.setOnClickListener {
            if(selectedProductVariant.qtyInCart > 1){
                selectedProductVariant.qtyInCart--
                binding.tvQuantity.text = selectedProductVariant.qtyInCart.toString()
            }
        }
    }

    companion object {
        fun showAddToBagDialog(
            fragmentManager: FragmentManager,
            tag: String,
            productVariant: ProductVariant,
            addBtnClicked: (productVariant: ProductVariant) -> Unit
        ) {
            val addToBagDialog = AddToBagDialog()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT_VARIANT, productVariant)
            addToBagDialog.arguments = bundle
            addToBagDialog.setListener(object : AddToBagDialogListener{
                override fun onAddBtnClicked(productVariant: ProductVariant) {
                    addBtnClicked(productVariant)
                }
            })
            if (fragmentManager.findFragmentByTag(tag) != null) {
                (fragmentManager.findFragmentByTag(tag) as DialogFragment).dismiss()
            }
            addToBagDialog.show(fragmentManager, tag)
        }
    }

    interface AddToBagDialogListener{
        fun onAddBtnClicked(productVariant: ProductVariant)
    }
}