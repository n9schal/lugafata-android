package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentCartBinding
import com.nischal.clothingstore.ui.adapters.CartAdapter
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : Fragment(R.layout.fragment_cart) {
    private var binding: FragmentCartBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)

        setupToolbar()
        setupList()
        setupObservers()
    }

    private fun setupObservers() {
        with(mainViewModel) {
            getShoppingListFromDb().observe(viewLifecycleOwner, Observer { productVariants ->
                if (!productVariants.isNullOrEmpty()) {
                    cartAdapter.clear()
                    cartAdapter.addItems(productVariants as MutableList<ProductVariant>)
                } else {
                    cartAdapter.clear()
                }
                updateBottomSection(productVariants)
            })

            productDeleteClicked.observe(viewLifecycleOwner, Observer {
                deleteShoppingListItemFromDb(it)
            })
        }
    }

    private fun updateBottomSection(productVariants: List<ProductVariant>?) {
        // * if list is empty -> hide bottom section
        // * if list is not empty -> show bottom section
        var total: Int = 0
        if (!productVariants.isNullOrEmpty()) {
            productVariants.forEach { variant ->
                total += variant.productVariantPrice * variant.qtyInCart
            }
            binding?.clBottomSection?.visibility = View.VISIBLE
            binding?.tvEmptyProducts?.visibility = View.GONE
            binding?.tvProductPrice?.text = total.toString()
        } else {
            binding?.clBottomSection?.visibility = View.GONE
            binding?.tvEmptyProducts?.visibility = View.VISIBLE
        }
    }

    private fun setupList() {
        cartAdapter = CartAdapter(
            arrayListOf(),
            mainViewModel
        )
        binding?.rvCart?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvCart?.adapter = cartAdapter
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.GONE
        binding?.includedToolbar?.tvTitle?.text =
            getString(R.string.text_cart_title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}