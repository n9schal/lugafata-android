package com.nischal.clothingstore.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.LayoutHomeCategoriesItemBinding
import com.nischal.clothingstore.ui.models.HomeCategory
import com.nischal.clothingstore.ui.viewmodels.MainViewModel

class HomeCategoriesAdapter(
    private val homeCategories: MutableList<HomeCategory>,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutHomeCategoriesItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, parent.context)
    }

    fun addItems(homeCategories: ArrayList<HomeCategory>) {
        this.homeCategories.clear()
        this.homeCategories.addAll(homeCategories)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.homeCategories.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = homeCategories.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(homeCategories[position])
    }

    inner class ViewHolder(val binding: LayoutHomeCategoriesItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeCategory) {
            if (item.adUrl.isNotBlank()) {
                binding.ivAd.visibility = View.VISIBLE
                Glide.with(binding.ivAd.context).load(item.adUrl)
                    .apply(
                        RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                    )
                    .into(binding.ivAd)
            }
            binding.tvTitle.text = item.title
            binding.tvExplore.setOnClickListener {
                mainViewModel.viewAllClickedEvent.value = item
            }
            with(binding.rvProductList){
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = HomeProductsAdapter(
                    item.productList,
                    mainViewModel
                )
            }
        }
    }
}
