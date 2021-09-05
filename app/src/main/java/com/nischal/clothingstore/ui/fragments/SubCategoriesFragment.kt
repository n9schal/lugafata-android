package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentSubcategoriesBinding

class SubCategoriesFragment: Fragment(R.layout.fragment_subcategories) {
    private var binding: FragmentSubcategoriesBinding? = null
    private val args: SubCategoriesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubcategoriesBinding.bind(view)

        setupToolbar()
        setupViews()
    }

    private fun setupViews() {
        args.selectedCategory.subCategories.forEach { subCategory ->
            val chip = Chip(requireContext())
            chip.text = subCategory.subCategoryName
            chip.isCheckable = true
            chip.setOnClickListener {
                if(!chip.isChecked){
                    // * fetch all sub-categories
                    binding?.tvTesttext?.text = "all"
                }
            }
            chip.setOnCheckedChangeListener { selectedChip, isChecked ->
                if(isChecked){
                    // * fetch only selected sub-category
                    binding?.tvTesttext?.text = subCategory.subCategorySlug
                }
            }
            binding?.chipGroupSubCategories?.addView(chip)
        }
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.VISIBLE
        binding?.includedToolbar?.ivSearch?.visibility = View.VISIBLE
        binding?.includedToolbar?.tvTitle?.text = args.demography + "'s " + args.selectedCategory.categoryName

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