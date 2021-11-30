package com.nischal.clothingstore.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.ActivityMainBinding
import com.nischal.clothingstore.utils.viewUtils.ProgressDialogHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.categoriesFragment, R.id.cartFragment, R.id.profileFragment ->{
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    fun hideLoading() {
        if (progressDialog != null)
            progressDialog!!.dismiss()
    }

    fun showLoading(message: String) {
        progressDialog = ProgressDialogHelper.progressDialog(this, message)
        progressDialog!!.show()
    }

    companion object {
        fun getInstance(
            context: Context
        ): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}