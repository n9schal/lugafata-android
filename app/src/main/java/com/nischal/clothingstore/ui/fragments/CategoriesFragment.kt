package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentCategoriesBinding
import com.nischal.clothingstore.ui.adapters.CategoriesAdapter
import com.nischal.clothingstore.ui.models.Category
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoriesFragment : Fragment(R.layout.fragment_categories) {
    private var binding: FragmentCategoriesBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val unfilteredCategories: ArrayList<Category> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoriesBinding.bind(view)

        setupToolbar()
        setupList()
        setupTabs()
        setupObservers()
        fetchData()
    }

    private fun fetchData() {
        mainViewModel.fetchCategories()
    }

    private fun setupObservers() {
        with(mainViewModel) {
            fetchCategoriesMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        if (categoriesAdapter.itemCount == 0) {
                            binding?.pbLoading?.visibility = View.VISIBLE
                            binding?.rvCategories?.visibility = View.GONE
                        }
                    }
                    Status.SUCCESS -> {
                        binding?.pbLoading?.visibility = View.GONE
                        binding?.rvCategories?.visibility = View.VISIBLE
                        it.data?.let { categories ->
                            unfilteredCategories.clear()
                            unfilteredCategories.addAll(categories)
                            binding?.tlGender?.getTabAt(0)?.select()
                        }
                    }
                    Status.ERROR -> {
                        pbLoading.visibility = View.GONE
                        rvHomeProductLists.visibility = View.VISIBLE
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })

            categoryClickedEvent.observe(viewLifecycleOwner, Observer {
                val demography: String =
                    binding?.tlGender?.getTabAt(binding?.tlGender?.selectedTabPosition!!)?.text.toString()
                val selectedCategory: Category = it
                val bundle = Bundle().apply {
                    putString("demography", demography)
                    putSerializable("selectedCategory", selectedCategory)
                }
                findNavController().navigate(
                    R.id.action_categoriesFragment_to_subCategoriesFragment,
                    bundle
                )
            })
        }
    }

    private fun setupList() {
        categoriesAdapter = CategoriesAdapter(
            arrayListOf(),
            mainViewModel
        )
        binding?.rvCategories?.layoutManager = LinearLayoutManager(context)
        binding?.rvCategories?.adapter = categoriesAdapter
    }

    private fun setupTabs() {
        binding?.tlGender?.let {
            it.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    filterCategories(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    categoriesAdapter.clearItems()
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    filterCategories(tab)
                }
            })
        }
    }

    private fun filterCategories(tab: TabLayout.Tab?) {
        val slug = "categories-" + tab?.text!!
        val filteredCategories = unfilteredCategories.filter { category ->
            category.parentCollectionSlug.contains(
                slug, true
            )
        }
        categoriesAdapter.addItems(filteredCategories as ArrayList<Category>)
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.GONE
        binding?.includedToolbar?.tvTitle?.text = getString(R.string.text_toolbar_categories_title)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}