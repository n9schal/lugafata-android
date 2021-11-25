package com.nischal.clothingstore.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentForgotPasswordBinding
import com.nischal.clothingstore.ui.activities.AuthActivity
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.setupUI
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import com.nischal.clothingstore.utils.viewUtils.ProgressDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    private var binding: FragmentForgotPasswordBinding? = null
    private val authViewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgotPasswordBinding.bind(view)
        requireActivity().setupUI(binding!!.root)
        setupViews()
        setupClicks()
        setupObservers()
    }

    private fun setupObservers() {
        with(authViewModel) {
            alertDialogEvent.observe(viewLifecycleOwner, Observer {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    title = it.title,
                    message = it.message,
                    negativeBtnText = null
                )
            })

            requestPasswordResetMediator.observe(viewLifecycleOwner, Observer {
                when(it.status){
                    Status.LOADING -> {
                        (requireActivity() as AuthActivity).showLoading("")
                    }
                    Status.SUCCESS -> {
                        (requireActivity() as AuthActivity).hideLoading()
                        Toast.makeText(
                            requireContext(),
                            "Password reset link sent successfully. Please check your email.",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }
                    Status.ERROR -> {
                        (requireActivity() as AuthActivity).hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })
        }
    }

    private fun setupClicks() {
        binding?.btnSend?.setOnClickListener {
            authViewModel.requestPasswordReset(binding?.etEmail?.text.toString())
        }
        binding?.btnBackToLogin?.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupViews() {
        setupTextFieldsValidation()
    }

    private fun setupTextFieldsValidation() {
        binding?.textInputLayoutEmail?.editText?.doOnTextChanged { inputText, _, _, _ ->
            when {
                inputText.isNullOrBlank() -> {
                    binding?.textInputLayoutEmail?.error =
                        Constants.ValidationErrorMessages.ERR_EMPTY_EMAIL
                }
                !Patterns.EMAIL_ADDRESS.matcher(inputText.trim()).matches() -> {
                    binding?.textInputLayoutEmail?.error =
                        Constants.ValidationErrorMessages.ERR_INVALID_EMAIL
                }
                else -> binding?.textInputLayoutEmail?.error = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}