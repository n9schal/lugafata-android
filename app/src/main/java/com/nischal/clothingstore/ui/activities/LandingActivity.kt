package com.nischal.clothingstore.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.ActivityLandingBinding
import com.nischal.clothingstore.databinding.ActivityMainBinding

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}