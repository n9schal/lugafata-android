package com.nischal.clothingstore.ui.adapters.viewpagers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nischal.clothingstore.ui.fragments.ImageSliderPagerFragment
import com.nischal.clothingstore.ui.models.Image

class ImageSliderPagerAdapter(activity: FragmentActivity, private val images: ArrayList<Image>): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return images.size
    }

    override fun createFragment(position: Int): Fragment {
        return ImageSliderPagerFragment.getInstance(images[position].src)
    }
}