package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentMyOrdersBinding
import com.nischal.clothingstore.ui.adapters.OrderListAdapter
import com.nischal.clothingstore.ui.models.OrderDetails
import com.nischal.clothingstore.ui.viewmodels.OrdersViewModel
import com.nischal.clothingstore.utils.PaginationScrollListener
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyOrdersFragment : Fragment(R.layout.fragment_my_orders) {
    private val ordersViewModel: OrdersViewModel by viewModel()
    private var binding: FragmentMyOrdersBinding? = null
    private lateinit var orderListAdapter: OrderListAdapter

    private var currentPage: Int = PaginationScrollListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var isPending = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyOrdersBinding.bind(view)

        setupToolbar()
        resetStates()
        setupList()
        setupViews()
        setupObservers()
        fetchOrders()
    }

    private fun setupObservers() {
        with(ordersViewModel) {
            getOrdersMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        if (currentPage == PaginationScrollListener.PAGE_START) {
                            binding?.pbLoading?.visibility = View.VISIBLE
                            binding?.orderSwipeRefresh?.isRefreshing = false
                        } else {
                            binding?.loadMoreProgress?.visibility = View.VISIBLE
                        }
                    }
                    Status.SUCCESS -> {
                        binding?.pbLoading?.visibility = View.GONE
                        binding?.loadMoreProgress?.visibility = View.GONE
                        binding?.orderSwipeRefresh?.isRefreshing = false
                        isLoading = false

                        if (currentPage == PaginationScrollListener.PAGE_START) {
                            orderListAdapter.clear()
                        }
                        it.data?.let { orderDetailsList ->
                            // * add items if not empty
                            if (orderDetailsList.isEmpty()) {
                                isLastPage = true
                            } else {
                                orderListAdapter.addItems(orderDetailsList)
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding?.pbLoading?.visibility = View.GONE
                        binding?.loadMoreProgress?.visibility = View.GONE
                        binding?.orderSwipeRefresh?.isRefreshing = false
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null
                        )
                    }
                }
            })
        }
    }

    private fun setupList() {
        // setup list adapter
        orderListAdapter = OrderListAdapter(
            arrayListOf(),
            ordersViewModel
        )
        binding?.rvOrders?.adapter = orderListAdapter
        binding?.rvOrders?.layoutManager = LinearLayoutManager(requireContext())
        // setup pagination
        binding?.rvOrders?.addOnScrollListener(object :
            PaginationScrollListener(binding?.rvOrders?.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                ordersViewModel.getOrders(
                    currentPage = currentPage,
                    pageSize = PAGE_SIZE,
                    isPending
                )
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        // setup swipe listener
        binding?.orderSwipeRefresh?.setOnRefreshListener {
            currentPage = PaginationScrollListener.PAGE_START
            isLastPage = false
            isLoading = false
            orderListAdapter.clear()
            ordersViewModel.getOrders(
                currentPage = currentPage,
                pageSize = PaginationScrollListener.PAGE_SIZE,
                isPending
            )
        }
    }

    private fun setupViews() {
        binding?.chipPendingOrders?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                isPending = true
                fetchOrders()
            }
        }
        binding?.chipOrderHistory?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                isPending = false
                fetchOrders()
            }
        }
    }

    private fun fetchOrders() {
        currentPage = PaginationScrollListener.PAGE_START
        isLastPage = false
        isLoading = false
        orderListAdapter.clear()
        ordersViewModel.getOrders(
            currentPage = currentPage,
            pageSize = PaginationScrollListener.PAGE_SIZE,
            isPending
        )
    }

    private fun resetStates() {
        ordersViewModel.getOrdersMediator = MediatorLiveData<Resource<ArrayList<OrderDetails>>>()
        currentPage = PaginationScrollListener.PAGE_START
        isLastPage = false
        isLoading = false
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.VISIBLE
        binding?.includedToolbar?.tvTitle?.text = getString(R.string.toolbar_title_text_my_orders)

        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}