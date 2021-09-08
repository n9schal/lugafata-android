package com.nischal.clothingstore.ui.adapters.viewpagers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nischal.clothingstore.ui.fragments.ImageSliderPagerFragment

class ImageSliderPagerAdapter(activity: FragmentActivity, private val imageUrlList: ArrayList<String>): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    override fun createFragment(position: Int): Fragment {
        return ImageSliderPagerFragment.getInstance(imageUrlList[position])
    }
}