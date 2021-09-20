package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentProductDetailsBinding
import com.nischal.clothingstore.ui.adapters.viewpagers.ImageSliderPagerAdapter
import com.nischal.clothingstore.ui.models.Product
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showAddToBagDialog
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import kotlinx.android.synthetic.main.layout_product_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {
    private var binding: FragmentProductDetailsBinding? = null
    private val args: ProductDetailsFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModel()
    private var selectedProductVariant: ProductVariant? = null
    private var productVariantsFromDb: ArrayList<ProductVariant> = arrayListOf()

    private lateinit var imageSliderPagerAdapter: ImageSliderPagerAdapter
    private lateinit var product: Product

    private var imageSliderPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailsBinding.bind(view)
        product = args.product
        setupToolbar()
        setupViews()
        setupObservers()

        mainViewModel.fetchProductDetail(
            id = product.productId,
            slug = product.productSlug
        )
    }

    private fun setupObservers() {
        with(mainViewModel) {
            fetchProductDetailsMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        it.data?.let { prod ->
                            // * update product
                            product.productFeaturedAsset = prod.productFeaturedAsset
                            product.productAssets.clear()
                            product.productAssets.addAll(prod.productAssets)
                            product.productVariants.clear()
                            product.productVariants.addAll(prod.productVariants)
                            product.optionGroups.clear()
                            product.optionGroups.addAll(prod.optionGroups)
                            updateViews()
                        }
                    }
                    Status.ERROR -> {
                        hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })

            getShoppingListFromDb().observe(viewLifecycleOwner, Observer { productVariants ->
                if (!productVariants.isNullOrEmpty()) {
                    productVariantsFromDb.clear()
                    productVariantsFromDb.addAll(productVariants)

                    // * update selected variant with qty in cart if already added to bag
                    try {
                        if (selectedProductVariant != null) {
                            val matchedVariant: ProductVariant =
                                productVariantsFromDb.first { item -> item.productVariantId == selectedProductVariant!!.productVariantId }
                            selectedProductVariant = matchedVariant
                        }
                    } catch (e: NoSuchElementException) {

                    }
                }
            })
        }
    }

    private fun setupViews() {
        // * setup viewpager
        imageSliderPagerAdapter = ImageSliderPagerAdapter(requireActivity(), arrayListOf())
        binding?.includedLayoutProductDetail?.imageSliderViewPager?.adapter =
            imageSliderPagerAdapter
        // register viewpager page change callback
        binding?.includedLayoutProductDetail?.imageSliderViewPager?.registerOnPageChangeCallback(
            imageSliderPageChangeCallback
        )
        // * setup dot indicator
        binding?.includedLayoutProductDetail?.wormDotsIndicator?.setViewPager2(binding?.includedLayoutProductDetail?.imageSliderViewPager!!)

        binding?.includedLayoutProductDetail?.etOptionSize?.doOnTextChanged { inputText, start, before, count ->
            // * update price
            for (variant in product.productVariants) {
                for (option in variant.options) {
                    if (option.optionName == inputText.toString()) {
                        binding?.includedLayoutProductDetail?.tvProductPrice?.text =
                            variant.productVariantPrice.toString()
                        // * update selected variant with qty in cart if already added to bag
                        selectedProductVariant = try {
                            val matchedVariant: ProductVariant =
                                productVariantsFromDb.first { item -> item.productVariantId == variant.productVariantId }
                            matchedVariant
                        } catch (e: NoSuchElementException) {
                            variant
                        }
                    }
                }
            }
        }

        binding?.includedLayoutProductDetail?.btnAddToBag?.setOnClickListener {
            if (etOptionSize.text.isNotEmpty() && selectedProductVariant != null) {
                requireActivity().showAddToBagDialog(
                    context = requireActivity(),
                    productVariant = selectedProductVariant!!,
                    addBtnClicked = { productVariant ->
                        mainViewModel.updateShoppingListInDb(productVariant)
                        showSnackbar()
                    }
                )
            } else {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    message = "Please select your appropriate size first.",
                    negativeBtnText = null
                )
            }
        }
    }

    private fun showSnackbar() {
        Snackbar.make(requireActivity().window.decorView.findViewById(android.R.id.content), "Product has been added to your bag.", Snackbar.LENGTH_LONG)
            .apply {
                setAction("Show") {
                    // todo navigate to bag page
                    Timber.d("navigated to bag")
                }
                show()
            }
    }

    private fun updateViews() {
        binding?.includedLayoutProductDetail?.tvProductName?.text = product.productName
        binding?.includedLayoutProductDetail?.tvProductPrice?.text = product.productPrice.toString()

        // * show/hide dot indicator
        if (product.productAssets.size <= 1) {
            binding?.includedLayoutProductDetail?.wormDotsIndicator?.visibility = View.GONE
            imageSliderPagerAdapter.addImages(arrayListOf(product.productFeaturedAsset))
        } else {
            binding?.includedLayoutProductDetail?.wormDotsIndicator?.visibility = View.VISIBLE
            imageSliderPagerAdapter.addImages(product.productAssets)
        }

        // * update option
        val optionTexts = arrayListOf<String>()
        if (product.optionGroups.isNotEmpty()) {
            product.optionGroups[0].options.forEach { option -> optionTexts.add(option.optionName) }
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, optionTexts)
        binding?.includedLayoutProductDetail?.etOptionSize?.setAdapter(adapter)
    }

    private fun showLoading() {
        binding?.includedLayoutProductDetail?.clProductDetails?.visibility =
            View.GONE
        binding?.shimmerLayout?.visibility = View.VISIBLE
        binding?.shimmerLayout?.startShimmer()
    }

    private fun hideLoading() {
        binding?.includedLayoutProductDetail?.clProductDetails?.visibility =
            View.VISIBLE
        binding?.shimmerLayout?.visibility = View.GONE
        binding?.shimmerLayout?.stopShimmer()
    }

    private fun setupToolbar() {
        binding?.includedLayoutProductDetail?.includedToolbar?.ivBack?.visibility = View.VISIBLE
        binding?.includedLayoutProductDetail?.includedToolbar?.tvTitle?.text =
            args.product.productName
        binding?.includedLayoutProductDetail?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        binding?.includedLayoutProductDetail?.imageSliderViewPager?.unregisterOnPageChangeCallback(
            imageSliderPageChangeCallback
        )
        super.onDestroyView()
        binding = null
    }
}