package com.nischal.clothingstore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.LayoutCategoriesItemBinding
import com.nischal.clothingstore.ui.models.Category
import com.nischal.clothingstore.ui.viewmodels.MainViewModel

class CategoriesAdapter(
    private val categories: MutableList<Category>,
    private val mainViewModel: MainViewModel
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutCategoriesItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = categories.size

    fun addItems(categories: ArrayList<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.categories.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(categories[position])
    }

    inner class ViewHolder(val binding: LayoutCategoriesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            Glide.with(binding.ivCategory.context)
                .load(item.image.src.replace("\\", "/"))
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                )
                .into(binding.ivCategory)
            binding.tvCategoryTitle.text = item.categoryName

            binding.clCategory.setOnClickListener {
                mainViewModel.categoryClickedEvent.value = item
            }
        }
    }
}