package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentEditProfileBinding
import com.nischal.clothingstore.ui.activities.AuthActivity
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.models.RegisterRequest
import com.nischal.clothingstore.ui.models.UserDetails
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.StringUtils
import com.nischal.clothingstore.utils.extensions.setupUI
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] to edit profile details
 */
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private var binding: FragmentEditProfileBinding? = null
    private val authViewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        requireActivity().setupUI(binding?.root)
        setupToolbar()
        setupViews()
        setupClicks()
        setupObservers()
        authViewModel.activeCustomer()
    }

    private fun setupObservers() {
        with(authViewModel){
            activeCustomerQueryMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (activity as MainActivity).showLoading("fetching profile...")
                    }
                    Status.SUCCESS -> {
                        (activity as MainActivity).hideLoading()
                        setupViews()
                    }
                    Status.ERROR -> {
                        (activity as MainActivity).hideLoading()
                        // todo show a dialog
                        startActivity(AuthActivity.getInstance(requireContext()))
                        requireActivity().finishAffinity()
                    }
                }
            })

            updateCustomerMutationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (activity as MainActivity).showLoading("saving profile...")
                    }
                    Status.SUCCESS -> {
                        (activity as MainActivity).hideLoading()
                        Toast.makeText(
                            requireContext(),
                            "Profile updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        setupViews()
                    }
                    Status.ERROR -> {
                        (activity as MainActivity).hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })

            alertDialogEvent.observe(this@EditProfileFragment, Observer {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    title = it.title,
                    message = it.message,
                    negativeBtnText = null
                )
            })
        }
    }

    private fun setupClicks() {
        binding?.btnSaveProfile?.setOnClickListener {
            val userDetails = UserDetails(
                firstName = binding?.etFirstName?.text.toString(),
                lastName = binding?.etLastName?.text.toString(),
                phoneNumber = StringUtils.addNepalCountryCode(binding?.etMobileNumber?.text.toString())
            )
            authViewModel.updateCustomer(userDetails)
        }

    }

    private fun setupViews() {
        authViewModel.getProfileInfoFromPrefs().let { userDetails ->
            binding?.etFirstName?.setText(userDetails.firstName)
            binding?.etLastName?.setText(userDetails.lastName)
            binding?.etMobileNumber?.setText(
                StringUtils.removeNepalCountryCode(userDetails.phoneNumber ?: "")
            )
            binding?.tvEmail?.setText(userDetails.email)
        }

        setupTextFieldsValidation()
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
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding?.includedToolbar?.tvTitle?.text = getString(R.string.toolbar_title_edit_profile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}