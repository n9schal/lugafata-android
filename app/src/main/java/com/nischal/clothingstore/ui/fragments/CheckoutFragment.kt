package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentCheckoutBinding

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {
    private var binding: FragmentCheckoutBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckoutBinding.bind(view)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.VISIBLE
        binding?.includedToolbar?.tvTitle?.text =
            getString(R.string.text_checkout_title)
        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}