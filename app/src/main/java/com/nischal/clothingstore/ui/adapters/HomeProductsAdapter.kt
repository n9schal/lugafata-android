package com.nischal.clothingstore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.LayoutHomeProductsItemBinding
import com.nischal.clothingstore.ui.models.Product
import com.nischal.clothingstore.ui.viewmodels.MainViewModel

class HomeProductsAdapter(
    private val products: MutableList<Product>,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutHomeProductsItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(products[position])
    }

    inner class ViewHolder(val binding: LayoutHomeProductsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) {
            Glide.with(binding.ivProduct.context)
                .load(item.productFeaturedAsset.src.replace("\\", "/"))
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                )
                .into(binding.ivProduct)
            binding.tvProductName.text = item.productName
            binding.tvPrice.text = item.productPrice.toString()
        }
    }
}