package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentImageSliderPagerBinding

class ImageSliderPagerFragment : Fragment(R.layout.fragment_image_slider_pager) {
    private var binding: FragmentImageSliderPagerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageSliderPagerBinding.bind(view)

        val imageUrl: String = requireArguments().getString(IMAGE_URL) ?: ""

        binding?.let {
            Glide.with(it.ivImageSlider.context)
                .load(imageUrl.replace("\\", "/"))
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                )
                .into(it.ivImageSlider)
        }
    }

    companion object {
        const val IMAGE_URL = "image_url"
        fun getInstance(imageUrl: String): Fragment {
            val imageSliderPagerFragment = ImageSliderPagerFragment()
            val bundle = Bundle().apply {
                putString(IMAGE_URL, imageUrl)
            }
            imageSliderPagerFragment.arguments = bundle
            return imageSliderPagerFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}