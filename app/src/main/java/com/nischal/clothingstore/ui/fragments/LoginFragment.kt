package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo.api.ApolloExperimental
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentLoginBinding
import com.nischal.clothingstore.ui.activities.AuthActivity
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.models.LoginRequest
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.setupUI
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment: Fragment(R.layout.fragment_login) {
    private var binding: FragmentLoginBinding? = null
    private val authViewModel: AuthViewModel by viewModel()

    @ApolloExperimental
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        requireActivity().setupUI(binding!!.root)
        setupViews()
        setupClicks()
        setupObservers()
    }

    private fun setupObservers() {
        with(authViewModel){
            alertDialogEvent.observe(viewLifecycleOwner, Observer {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    title = it.title,
                    message = it.message,
                    negativeBtnText = null
                )
            })

            loginMutationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (activity as AuthActivity).showLoading("logging in...")
                    }
                    Status.SUCCESS -> {
                        (activity as AuthActivity).hideLoading()
                        it.data?.login?.asCurrentUser?.let {
                            authViewModel.activeCustomer()
                        }
                    }
                    Status.ERROR -> {
                        (activity as AuthActivity).hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })

            activeCustomerQueryMediator.observe(viewLifecycleOwner, Observer {
                when(it.status){
                    Status.LOADING -> {
                        (activity as AuthActivity).showLoading("fetching profile...")
                    }
                    Status.SUCCESS -> {
                        (activity as AuthActivity).hideLoading()
                        // goto main activity
                        startActivity(MainActivity.getInstance(requireContext()))
                        requireActivity().finishAffinity()
                    }
                    Status.ERROR -> {
                        (activity as AuthActivity).hideLoading()
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
        binding?.etPassword?.doOnTextChanged { inputText, _, _, _ ->
            when {
                inputText.isNullOrBlank() -> {
                    binding?.textInputLayoutPassword?.error =
                        Constants.ValidationErrorMessages.ERR_EMPTY_PASSWORD
                }
                else -> binding?.textInputLayoutPassword?.error = null
            }
        }
    }

    @ApolloExperimental
    private fun setupClicks() {
        binding?.btnGotoSignup?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        binding?.btnForgotPassword?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        binding?.btnLogin?.setOnClickListener {
            val loginRequest = LoginRequest(
                email = binding?.etEmail?.text.toString(),
                password = binding?.etPassword?.text.toString()
            )
            authViewModel.loginMutation(loginRequest)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}