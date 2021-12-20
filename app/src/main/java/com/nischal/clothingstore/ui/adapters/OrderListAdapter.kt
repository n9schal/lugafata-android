package com.nischal.clothingstore.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.LayoutOrderItemBinding
import com.nischal.clothingstore.ui.models.OrderDetails
import com.nischal.clothingstore.ui.viewmodels.OrdersViewModel
import com.nischal.clothingstore.utils.Constants.AppOrderStates.DELIVERY_CANCELLED
import com.nischal.clothingstore.utils.Constants.AppOrderStates.DELIVERY_COMPLETE
import com.nischal.clothingstore.utils.Constants.AppOrderStates.DELIVERY_STARTED
import com.nischal.clothingstore.utils.Constants.AppOrderStates.ORDER_CONFIRMED
import com.nischal.clothingstore.utils.Constants.AppOrderStates.ORDER_PLACED
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.CANCELLED
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.DELIVERED
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.PARTIALLY_SHIPPED
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.PAYMENT_AUTHORIZED
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.PAYMENT_SETTLED
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.SHIPPED
import com.nischal.clothingstore.utils.DateUtils

class OrderListAdapter(
    private val orders: MutableList<OrderDetails>,
    private val ordersViewModel: OrdersViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutOrderItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(orders[position], position)
    }

    fun addItems(trackedOrderList: MutableList<OrderDetails>) {
        this.orders.addAll(trackedOrderList)
        notifyDataSetChanged()
    }

    fun clear() {
        this.orders.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderDetails, position: Int) {
            binding.tvOrderNumber.text = "Order No. ${item.orderNumber}"
            val formattedDate = DateUtils.getFormattedDate(item.createdAt)
            binding.tvDateValue.text = formattedDate
            binding.tvTotalAmountValue.text = item.total.toString()
            setupDeliveryStatusText(item.vendureOrderState)
            binding.clOrderItem.setOnClickListener {
                ordersViewModel.orderItemClickEvent.value = item
            }
        }

        /**
         * method to change order status and colors according to status
         * @param vendureState  order state from server side.
         * */
        private fun setupDeliveryStatusText(vendureState: String) {
            when (vendureState) {
                PAYMENT_AUTHORIZED -> {
                    binding.tvStatus.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.text = ORDER_PLACED
                }
                PAYMENT_SETTLED -> {
                    binding.tvStatus.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.text = ORDER_CONFIRMED
                }
                PARTIALLY_SHIPPED, SHIPPED -> {
                    binding.tvStatus.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.text = DELIVERY_STARTED
                }
                DELIVERED -> {
                    binding.tvStatus.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.setTextColor(Color.parseColor("#72ddb5")) // color accent
                    binding.tvStatusValue.text = DELIVERY_COMPLETE
                }
                else -> {
                    binding.tvStatus.setTextColor(Color.parseColor("#dd7272")) // color error
                    binding.tvStatusValue.setTextColor(Color.parseColor("#dd7272")) // color error
                    binding.tvStatusValue.text = DELIVERY_CANCELLED
                }
            }
        }
    }
}