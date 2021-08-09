package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentSignupBinding
import com.nischal.clothingstore.utils.extensions.setupUI

class SignupFragment: Fragment(R.layout.fragment_signup) {
    private var binding: FragmentSignupBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)
        requireActivity().setupUI(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}