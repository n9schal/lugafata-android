package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentProductDetailsBinding
import com.nischal.clothingstore.ui.adapters.viewpagers.ImageSliderPagerAdapter

class ProductDetailsFragment: Fragment(R.layout.fragment_product_details) {
    private var binding: FragmentProductDetailsBinding? = null

    private var imageSliderPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductDetailsBinding.bind(view)

        setupViewPager()
    }

    private fun setupViewPager() {
        val imageList: ArrayList<String> = arrayListOf(
            "https://assets.adidas.com/images/h_840,f_auto,q_auto:sensitive,fl_lossy,c_fill,g_auto/e725107a3d7041389f94ab220123fbcb_9366/Bravada_Shoes_Black_FV8085_01_standard.jpg",
            "https://assets.adidas.com/images/w_600,f_auto,q_auto/9de5e247c04a45e7a1faab220124efc3_9366/Tenis_Bravada_Negro_FV8097_01_standard.jpg",
            "https://storage.googleapis.com/tradeinn-images/images/products_image/13766/fotos/137668652.jpg",
            "https://assets.adidas.com/images/w_600,f_auto,q_auto/af6bbdfea9214acca9a3ab3800c54d30_9366/Bravada_Shoes_White_FV8086_01_standard.jpg"
        )
        val imageSliderPagerAdapter = ImageSliderPagerAdapter(requireActivity(), imageList)
        binding?.imageSliderViewPager?.adapter = imageSliderPagerAdapter
        binding?.wormDotsIndicator?.setViewPager2(binding?.imageSliderViewPager!!)

        // register viewpager page change callback
        binding?.imageSliderViewPager?.registerOnPageChangeCallback(imageSliderPageChangeCallback)
    }

    override fun onDestroyView() {
        binding?.imageSliderViewPager?.unregisterOnPageChangeCallback(imageSliderPageChangeCallback)
        super.onDestroyView()
        binding = null
    }
}