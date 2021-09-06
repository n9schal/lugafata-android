package com.nischal.clothingstore.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Supporting only LinearLayoutManager and GridLayoutManager for now.
 */
abstract class PaginationScrollListener() :
    RecyclerView.OnScrollListener() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var isLinearLayoutManager = true

    constructor(linearLayoutManager: LinearLayoutManager) : this() {
        layoutManager = linearLayoutManager
        isLinearLayoutManager = true
    }
    constructor(gridLayoutManager: GridLayoutManager): this(){
        layoutManager = gridLayoutManager
        isLinearLayoutManager = false
    }

    override fun onScrolled(
        recyclerView: RecyclerView,
        dx: Int,
        dy: Int
    ) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount

        val firstVisibleItemPosition = if(isLinearLayoutManager){
            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }else{
            (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        }

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE
            ) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean

    companion object {
        const val PAGE_START = 0

        /**
         * Set scrolling threshold here
         */
        const val PAGE_SIZE = 8
    }

}