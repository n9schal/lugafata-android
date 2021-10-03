package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentSearchBinding
import com.nischal.clothingstore.ui.adapters.ProductsAdapter
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.PaginationScrollListener
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.hideSoftKeyboard
import com.nischal.clothingstore.utils.extensions.setupUI
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {
    private var binding: FragmentSearchBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var productsAdapter: ProductsAdapter

    private var currentPage: Int = PaginationScrollListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var searchTerm: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        setupToolbar()
        setupList()
        setupObservers()
        fetchProducts()
    }

    private fun setupObservers() {
        with(mainViewModel) {
            fetchSearchedProductsMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        if (currentPage == PaginationScrollListener.PAGE_START) {
                            binding?.pbLoading?.visibility = View.VISIBLE
                        } else {
                            binding?.loadMoreProgress?.visibility = View.VISIBLE
                        }
                        binding?.tvEmptyProducts?.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        binding?.pbLoading?.visibility = View.GONE
                        binding?.loadMoreProgress?.visibility = View.GONE
                        isLoading = false
                        if (currentPage == PaginationScrollListener.PAGE_START) {
                            productsAdapter.clear()
                        }
                        it.data?.let { searchResponse ->
                            // * add items if not empty
                            if (searchResponse.products.isEmpty()) {
                                if (currentPage == PaginationScrollListener.PAGE_START) {
                                    binding?.tvEmptyProducts?.visibility = View.VISIBLE
                                }
                                isLastPage = true
                            } else {
                                productsAdapter.addItems(searchResponse.products)
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding?.pbLoading?.visibility = View.GONE
                        binding?.loadMoreProgress?.visibility = View.GONE
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })

            productClickedEvent.observe(viewLifecycleOwner, Observer {
                val bundle = Bundle().apply {
                    putSerializable("product", it)
                }
                findNavController().navigate(
                    R.id.action_searchFragment_to_productDetailsFragment,
                    bundle
                )
            })
        }
    }

    private fun setupList() {
        productsAdapter = ProductsAdapter(
            arrayListOf(),
            mainViewModel
        )
        binding?.rvSearchedProducts?.adapter = productsAdapter

        // * add scroll listener for pagination
        binding?.rvSearchedProducts?.addOnScrollListener(object :
            PaginationScrollListener(binding?.rvSearchedProducts?.layoutManager as GridLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                mainViewModel.fetchSearchedProducts(
                    currentPage = currentPage,
                    pageSize = PAGE_SIZE,
                    term = searchTerm
                )
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.VISIBLE

        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding?.includedToolbar?.etSearch?.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // update searchterm
                searchTerm = textView.text.toString().trim()
                requireActivity().hideSoftKeyboard()
                fetchProducts()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun fetchProducts() {
        currentPage = PaginationScrollListener.PAGE_START
        isLastPage = false
        isLoading = false
        productsAdapter.clear()
        mainViewModel.fetchSearchedProducts(
            currentPage = currentPage,
            pageSize = PaginationScrollListener.PAGE_SIZE,
            term = searchTerm
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}