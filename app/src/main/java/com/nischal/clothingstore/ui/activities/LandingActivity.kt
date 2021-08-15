package com.nischal.clothingstore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.nischal.clothingstore.databinding.ActivityLandingBinding
import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.utils.Status
import org.koin.androidx.viewmodel.ext.android.viewModel

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.activeCustomer()
        setupObservers()
    }

    private fun setupObservers() {
        authViewModel.activeCustomerQueryMediator.observe(this@LandingActivity, Observer {
            when (it.status) {
                Status.LOADING -> {
                    // todo do nothing
                }
                Status.SUCCESS -> {
                    startActivity(MainActivity.getInstance(this))
                    finishAffinity()
                }
                Status.ERROR -> {
                    authViewModel.clearPreferences()
                    startActivity(AuthActivity.getInstance(this))
                    finishAffinity()
                }
            }
        })
    }
}