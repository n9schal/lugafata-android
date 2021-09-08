package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentSubcategoriesBinding
import com.nischal.clothingstore.ui.adapters.ProductsAdapter
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.PaginationScrollListener
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubCategoriesFragment : Fragment(R.layout.fragment_subcategories) {
    private var binding: FragmentSubcategoriesBinding? = null
    private val args: SubCategoriesFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var productsAdapter: ProductsAdapter

    private var currentPage: Int = PaginationScrollListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false

    private lateinit var currentSubCategoryId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubcategoriesBinding.bind(view)
        currentSubCategoryId = args.selectedCategory.categoryId

        setupToolbar()
        setupList()
        setupViews()
        setupObservers()
        fetchProducts()
    }

    private fun setupObservers() {
        with(mainViewModel) {
            fetchSubCategoriesMediator.observe(viewLifecycleOwner, Observer {
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
                        it.data?.let { products ->
                            // * add items if not empty
                            if (products.isEmpty()) {
                                if (currentPage == PaginationScrollListener.PAGE_START) {
                                    binding?.tvEmptyProducts?.visibility = View.VISIBLE
                                }
                                isLastPage = true
                            } else {
                                productsAdapter.addItems(products)
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
                findNavController().navigate(R.id.action_subCategoriesFragment_to_productDetailsFragment)
            })
        }
    }

    private fun setupList() {
        productsAdapter = ProductsAdapter(
            arrayListOf(),
            mainViewModel
        )
        binding?.rvProducts?.adapter = productsAdapter

        // * add scroll listener for pagination
        binding?.rvProducts?.addOnScrollListener(object :
            PaginationScrollListener(binding?.rvProducts?.layoutManager as GridLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                mainViewModel.fetchSubCategories(
                    currentPage = currentPage,
                    pageSize = PAGE_SIZE,
                    subCategoryId = currentSubCategoryId
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

    private fun setupViews() {
        binding?.chipAll?.setOnCheckedChangeListener { selectedChip, isChecked ->
            if (isChecked) {
                // * change currently selected id for pagination later
                currentSubCategoryId = args.selectedCategory.categoryId
                // * fetch all sub-categories products
                fetchProducts()
            }
        }
        args.selectedCategory.subCategories.forEach { subCategory ->
            val chip = Chip(requireContext())
            chip.text = subCategory.subCategoryName
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { selectedChip, isChecked ->
                if (isChecked) {
                    // * change currently selected id for pagination later
                    currentSubCategoryId = subCategory.subCategoryId
                    // * fetch only selected sub-category
                    fetchProducts()
                }
            }
            binding?.chipGroupSubCategories?.addView(chip)
        }
    }

    private fun fetchProducts() {
        if (currentSubCategoryId.isNotBlank()) {
            currentPage = PaginationScrollListener.PAGE_START
            isLastPage = false
            isLoading = false
            productsAdapter.clear()
            mainViewModel.fetchSubCategories(
                currentPage = currentPage,
                pageSize = PaginationScrollListener.PAGE_SIZE,
                subCategoryId = currentSubCategoryId
            )
        }
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.VISIBLE
        binding?.includedToolbar?.ivSearch?.visibility = View.VISIBLE
        binding?.includedToolbar?.tvTitle?.text =
            args.demography + "'s " + args.selectedCategory.categoryName

        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding?.includedToolbar?.ivSearch?.setOnClickListener {
            //todo navigate to search page
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}