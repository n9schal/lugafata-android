package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentForgotPasswordBinding
import com.nischal.clothingstore.utils.extensions.setupUI

class ForgotPasswordFragment: Fragment(R.layout.fragment_forgot_password) {
    private var binding: FragmentForgotPasswordBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setupUI(binding!!.root)
        binding = FragmentForgotPasswordBinding.bind(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}