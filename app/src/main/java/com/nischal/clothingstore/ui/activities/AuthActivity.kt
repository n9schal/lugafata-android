package com.nischal.clothingstore.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.nischal.clothingstore.databinding.ActivityAuthBinding
import com.nischal.clothingstore.utils.viewUtils.ProgressDialogHelper
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    lateinit var navController: NavController
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = navHostFragment.findNavController()
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
            return Intent(context, AuthActivity::class.java)
        }
    }
}