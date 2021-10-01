package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentHomeBinding
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.adapters.HomeCategoriesAdapter
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import kotlinx.android.synthetic.main.fragment_home.*
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
        setupObservers()
        fetchData()
    }

    private fun setupObservers() {
        with(mainViewModel) {
            fetchHomePageContentsMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        if (homeCategoriesAdapter.itemCount == 0) {
                            pbLoading.visibility = View.VISIBLE
                            rvHomeProductLists.visibility = View.GONE
                            binding?.swipeRefresh?.isRefreshing = false
                        }
                    }
                    Status.SUCCESS -> {
                        pbLoading.visibility = View.GONE
                        rvHomeProductLists.visibility = View.VISIBLE
                        binding?.swipeRefresh?.isRefreshing = false
                        it.data?.let { data ->
                            homeCategoriesAdapter.addItems(data)
                        }
                    }
                    Status.ERROR -> {
                        pbLoading.visibility = View.GONE
                        rvHomeProductLists.visibility = View.VISIBLE
                        binding?.swipeRefresh?.isRefreshing = false
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

    private fun fetchData() {
        mainViewModel.fetchHomePageContents()
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
            homeCategoriesAdapter.clearItems()
            mainViewModel.fetchHomePageContents()
        }

    }

    private fun setupToolbar() {
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(
                appBarLayout: AppBarLayout,
                verticalOffset: Int
            ) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    // * show search icon
                    ivSearch.visibility = View.VISIBLE
                } else if (isShow) {
                    isShow = false
                    // * hide search icon
                    ivSearch.visibility = View.GONE
                }
            }
        })

        binding?.llSearch?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        binding?.ivSearch?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        if (mainViewModel.isUserLoggedIn()) {
            binding?.tvTitle?.text =
                "Welcome ${mainViewModel.getProfileInfoFromPrefs().firstName}"
        } else {
            binding?.tvTitle?.text = getString(R.string.text_toolbar_home_title)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}