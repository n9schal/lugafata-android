package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentHomeBinding
import com.nischal.clothingstore.ui.adapters.HomeCategoriesAdapter
import com.nischal.clothingstore.ui.models.HomeCategory
import com.nischal.clothingstore.ui.models.Image
import com.nischal.clothingstore.ui.models.Product
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var homeCategoriesAdapter: HomeCategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setupToolbar()
        setupLists()
    }

    private fun setupLists() {
        // * test data
        homeCategoriesAdapter = HomeCategoriesAdapter(
            arrayListOf(),
            mainViewModel
        )
        binding?.rvHomeProductLists?.layoutManager = LinearLayoutManager(context)
        binding?.rvHomeProductLists?.adapter = homeCategoriesAdapter
        binding?.swipeRefresh?.setOnRefreshListener {
            // todo reset list and fetch again
        }

    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.GONE
        if (mainViewModel.isUserLoggedIn()) {
            binding?.includedToolbar?.tvTitle?.text =
                "Welcome ${mainViewModel.getProfileInfoFromPrefs().firstName}"
        } else {
            binding?.includedToolbar?.tvTitle?.text = getString(R.string.text_toolbar_title)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}