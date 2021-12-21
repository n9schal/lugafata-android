package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentChangePasswordBinding
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.models.ChangePasswordRequest
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.setupUI
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] to change password
 */
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private var binding: FragmentChangePasswordBinding? = null
    private val authViewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangePasswordBinding.bind(view)
        requireActivity().setupUI(binding?.root)
        setupToolbar()
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

            updateCustomerPasswordMutationMediator.observe(viewLifecycleOwner, Observer {
                when(it.status){
                    Status.LOADING -> {
                        (activity as MainActivity).showLoading("")
                    }
                    Status.SUCCESS -> {
                        (activity as MainActivity).hideLoading()
                        Toast.makeText(
                            requireContext(),
                            "Password changed successfully.",
                            Toast.LENGTH_LONG
                        ).show()
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
        }
    }

    private fun setupClicks() {
        binding?.btnChangePassword?.setOnClickListener {
            val passwordChangeReq = ChangePasswordRequest(
                currentPassword = binding?.etCurrentPassword?.text.toString().trim(),
                newPassword = binding?.etNewPassword?.text.toString().trim(),
                repeatPassword = binding?.etRepeatPassword?.text.toString().trim()
            )
            authViewModel.updateCustomerPassword(passwordChangeReq)
        }
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding?.includedToolbar?.tvTitle?.text = getString(R.string.toolbar_title_change_password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}