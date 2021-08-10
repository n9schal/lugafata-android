package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentLoginBinding
import com.nischal.clothingstore.utils.extensions.setupUI

class LoginFragment: Fragment(R.layout.fragment_login) {
    private var binding: FragmentLoginBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        requireActivity().setupUI(binding!!.root)
        setupClicks()
    }

    private fun setupClicks() {
        binding?.btnGotoSignup?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        binding?.btnForgotPassword?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}