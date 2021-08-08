package com.nischal.clothingstore.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}