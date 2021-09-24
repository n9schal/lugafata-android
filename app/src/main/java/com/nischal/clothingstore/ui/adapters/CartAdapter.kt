package com.nischal.clothingstore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.LayoutCartItemBinding
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.ui.viewmodels.MainViewModel

class CartAdapter(
    private val productVariants: MutableList<ProductVariant>,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutCartItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun addItems(productVariantList: MutableList<ProductVariant>) {
        this.productVariants.addAll(productVariantList)
        notifyDataSetChanged()
    }

    fun clear() {
        productVariants.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = productVariants.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(productVariants[position])
    }

    inner class ViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductVariant) {
            Glide.with(binding.ivProductImage.context)
                .load(item.productVariantFeaturedAsset.src.replace("\\", "/"))
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                )
                .into(binding.ivProductImage)
            binding.tvProductName.text = item.productVariantName
            binding.tvPriceValue.text = item.productVariantPrice.toString()
            binding.tvQuantityValue.text = item.qtyInCart.toString()
            binding.tvSizeValue.text = item.options[0].optionName

            binding.ivClose.setOnClickListener {
                mainViewModel.productDeleteClicked.value = item
            }
        }
    }
}

