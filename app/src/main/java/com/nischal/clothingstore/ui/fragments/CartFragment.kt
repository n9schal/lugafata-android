package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentCartBinding
import com.nischal.clothingstore.ui.activities.AuthActivity
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.adapters.CartAdapter
import com.nischal.clothingstore.ui.models.OrderDetails
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : Fragment(R.layout.fragment_cart) {
    private var binding: FragmentCartBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var cartAdapter: CartAdapter
    var shoppingList: ArrayList<ProductVariant> = arrayListOf()
    private var orderDetailsForBundle: OrderDetails? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)

        setupToolbar()
        setupList()
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        with(mainViewModel) {
            getShoppingListFromDb().observe(viewLifecycleOwner, Observer { productVariants ->
                if (!productVariants.isNullOrEmpty()) {
                    shoppingList.clear()
                    shoppingList.addAll(productVariants)
                    cartAdapter.clear()
                    cartAdapter.addItems(shoppingList)
                } else {
                    cartAdapter.clear()
                }
                updateBottomSection(productVariants)
            })

            productDeleteClicked.observe(viewLifecycleOwner, Observer {
                deleteShoppingListItemFromDb(it)
            })

            proceedToCheckoutOperationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (requireActivity() as MainActivity).showLoading("checking inventory...")
                    }
                    Status.SUCCESS -> {
                        (requireActivity() as MainActivity).hideLoading()

                        // * navigate to next page
                        handleNavigationToCheckout()
                    }
                    Status.ERROR -> {
                        (requireActivity() as MainActivity).hideLoading()
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

    private fun handleNavigationToCheckout() {
        orderDetailsForBundle?.let {
            val bundle = Bundle().apply {
                putSerializable("orderDetailsFromCart", orderDetailsForBundle)
            }
            findNavController().navigate(
                R.id.action_cartFragment_to_checkoutFragment,
                bundle
            )
        }
    }

    private fun setupViews() {
        binding?.btnCheckout?.setOnClickListener {
            if (mainViewModel.isUserLoggedIn()) {
//                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
                mainViewModel.proceedToCheckoutOperation(
                    shoppingList
                )
            } else {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    message = getString(R.string.not_logged_in_message),
                    positiveBtnClicked = {
                        startActivity(AuthActivity.getInstance(requireContext()))
                    })
            }
        }
    }

    private fun updateBottomSection(productVariants: List<ProductVariant>?) {
        // * if list is empty -> hide bottom section
        // * if list is not empty -> show bottom section
        var subTotal: Int = 0

        if (!productVariants.isNullOrEmpty()) {
            productVariants.forEach { variant ->
                subTotal += variant.productVariantPrice * variant.qtyInCart
            }
            binding?.clBottomSection?.visibility = View.VISIBLE
            binding?.tvEmptyProducts?.visibility = View.GONE
            binding?.tvTotalPrice?.text = subTotal.toString()

            orderDetailsForBundle = OrderDetails(
                productVariantList = productVariants,
                subTotal = subTotal
            )
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