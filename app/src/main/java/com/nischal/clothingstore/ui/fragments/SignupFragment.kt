package com.nischal.clothingstore.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentSignupBinding
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.models.RegisterRequest
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.StringUtils
import com.nischal.clothingstore.utils.extensions.setupUI
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import com.nischal.clothingstore.utils.viewUtils.ProgressDialogHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private var binding: FragmentSignupBinding? = null
    private val authViewModel: AuthViewModel by viewModel()
    private var progressDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)
        requireActivity().setupUI(view)
        setupViews()
        setupClicks()
        setupObservers()
    }

    private fun setupObservers(){
        with(authViewModel){
            alertDialogEvent.observe(viewLifecycleOwner, Observer {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    title = it.title,
                    message = it.message,
                    negativeBtnText = null
                )
            })

            registerCustomerAccountMutationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading("registering...")
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        it.data?.let { loginRequest ->
                            Timber.d("Registration success")
                            authViewModel.loginMutation(loginRequest)
                        }
                    }
                    Status.ERROR -> {
                        hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })

            loginMutationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading("logging in...")
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        it.data?.login?.asCurrentUser?.let {
                            authViewModel.activeCustomer()
                        }
                    }
                    Status.ERROR -> {
                        hideLoading()
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
                        showLoading("fetching profile...")
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        // goto main activity
                        startActivity(MainActivity.getInstance(requireContext()))
                        requireActivity().finishAffinity()
                    }
                    Status.ERROR -> {
                        hideLoading()
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

    private fun setupClicks() {
        binding?.btnAlreadyHaveAccount?.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        binding?.btnSignup?.setOnClickListener {
            val registerRequest = RegisterRequest(
                firstName = binding?.etFirstName?.text.toString(),
                lastName = binding?.etLastName?.text.toString(),
                mobileNumber = StringUtils.addNepalCountryCode(binding?.etMobileNumber?.text.toString()),
                email = binding?.etEmail?.text.toString(),
                password = binding?.etPassword?.text.toString(),
                rePassword = binding?.etRepeatPassword?.text.toString()
            )
            authViewModel.registerCustomerAccountMutation(registerRequest)
        }
    }

    private fun setupTextFieldsValidation() {
        binding?.textInputLayoutFirstName?.editText?.doOnTextChanged { inputText, _, _, _ ->
            when {
                inputText.isNullOrBlank() -> {
                    binding?.textInputLayoutFirstName?.error =
                        Constants.ValidationErrorMessages.ERR_EMPTY_FIRST_NAME
                }
                else -> binding?.textInputLayoutFirstName?.error = null
            }
        }
        binding?.textInputLayoutLastName?.editText?.doOnTextChanged { inputText, _, _, _ ->
            when {
                inputText.isNullOrBlank() -> {
                    binding?.textInputLayoutLastName?.error =
                        Constants.ValidationErrorMessages.ERR_EMPTY_LAST_NAME
                }
                else -> binding?.textInputLayoutLastName?.error = null
            }
        }
        binding?.textInputLayoutMobileNumber?.editText?.doOnTextChanged { inputText, _, _, _ ->
            when {
                inputText.isNullOrBlank() -> {
                    binding?.textInputLayoutMobileNumber?.error =
                        Constants.ValidationErrorMessages.ERR_EMPTY_MOBILE_NUMBER
                }
                !StringUtils.addNepalCountryCode(inputText.toString())
                    .matches(Constants.ValidationRegex.VALID_PHONE_NUMBER.toRegex()) -> {
                    binding?.textInputLayoutMobileNumber?.error =
                        Constants.ValidationErrorMessages.ERR_INVALID_MOBILE_NUMBER
                }
                else -> binding?.textInputLayoutMobileNumber?.error = null
            }
        }
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
                !inputText.trim()
                    .matches(Constants.ValidationRegex.ATLEAST_SIX_CHARACTERS.toRegex()) -> {
                    binding?.textInputLayoutPassword?.error =
                        Constants.ValidationErrorMessages.ERR_INVALID_PASSWORD
                }
                else -> binding?.textInputLayoutPassword?.error = null
            }
        }
        binding?.etRepeatPassword?.doOnTextChanged { inputText, _, _, _ ->
            when {
                inputText.isNullOrBlank() -> {
                    binding?.textInputLayoutRepeatPassword?.error =
                        Constants.ValidationErrorMessages.ERR_INVALID_REPASSWORD
                }
                inputText.toString().trim() != binding?.textInputLayoutPassword?.editText?.text.toString() -> {
                    binding?.textInputLayoutRepeatPassword?.error =
                        Constants.ValidationErrorMessages.ERR_INVALID_REPASSWORD
                }
                else -> binding?.textInputLayoutRepeatPassword?.error = null
            }
        }
    }

    private fun hideLoading() {
        if (progressDialog != null)
            progressDialog!!.dismiss()
    }

    private fun showLoading(message: String) {
        progressDialog = ProgressDialogHelper.progressDialog(requireContext(), message)
        progressDialog!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}