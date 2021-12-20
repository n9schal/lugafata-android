package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentProfileBinding
import com.nischal.clothingstore.ui.activities.AuthActivity
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var binding: FragmentProfileBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        setupViews()
        setupClicks()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        if (!mainViewModel.isUserLoggedIn()) {
            showUserNotLoggedIn()
        }
    }

    private fun setupObservers() {
        with(authViewModel) {
            logoutMutationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (requireActivity() as MainActivity).showLoading("Logging out...")
                    }
                    Status.SUCCESS -> {
                        (requireActivity() as MainActivity).hideLoading()
                        startActivity(AuthActivity.getInstance(requireContext()))
                        requireActivity().finishAffinity()
                    }
                    Status.ERROR -> {
                        (requireActivity() as MainActivity).hideLoading()
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
        binding?.includedLogout?.clOptionContainer?.setOnClickListener {
            requireActivity().showCustomAlertDialog(
                context = requireActivity(),
                message = getString(R.string.logout_confirmation_message),
                positiveBtnClicked = {
                    authViewModel.logoutMutation()
                }
            )
        }

        binding?.includedMyOrders?.clOptionContainer?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myOrdersFragment)
        }

        binding?.includedEditProfile?.clOptionContainer?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    private fun setupViews() {
        setupProfileViews()
    }

    private fun showUserNotLoggedIn() {
        requireActivity().showCustomAlertDialog(
            context = requireActivity(),
            message = getString(R.string.not_logged_in_message),
            positiveBtnClicked = {
                startActivity(AuthActivity.getInstance(requireContext()))
            },
            negativeBtnClicked = {
                requireActivity().onBackPressed()
            }
        )
    }

    private fun setupProfileViews() {
        binding?.includedMyOrders?.tvOption?.text = getString(R.string.option_title_text_my_orders)
        binding?.includedEditProfile?.tvOption?.text =
            getString(R.string.option_title_text_edit_profile)
        binding?.includedChangePassword?.tvOption?.text =
            getString(R.string.option_title_text_change_password)
        binding?.includedAbout?.tvOption?.text = getString(R.string.option_title_text_about)
        binding?.includedLogout?.tvOption?.text = getString(R.string.option_title_text_logout)

        if (mainViewModel.isUserLoggedIn()) {
            binding?.tvFullName?.text =
                mainViewModel.getProfileInfoFromPrefs().firstName + " " + mainViewModel.getProfileInfoFromPrefs().lastName
            binding?.tvEmail?.text = mainViewModel.getProfileInfoFromPrefs().email
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}