package com.nischal.clothingstore.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nischal.clothingstore.ActiveOrderQuery
import com.nischal.clothingstore.R
import com.nischal.clothingstore.databinding.FragmentCheckoutBinding
import com.nischal.clothingstore.ui.activities.MainActivity
import com.nischal.clothingstore.ui.adapters.OrderSummaryListAdapter
import com.nischal.clothingstore.ui.models.Location
import com.nischal.clothingstore.ui.models.OrderDetails
import com.nischal.clothingstore.ui.models.PaymentOption
import com.nischal.clothingstore.ui.models.ProductVariant
import com.nischal.clothingstore.ui.viewmodels.CheckoutViewModel
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.Status
import com.nischal.clothingstore.utils.extensions.showCustomAlertDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {
    private val checkoutViewModel: CheckoutViewModel by sharedViewModel()
    private val args: CheckoutFragmentArgs by navArgs()
    private var binding: FragmentCheckoutBinding? = null

    private lateinit var orderSummaryCartAdapter: OrderSummaryListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckoutBinding.bind(view)

        setupToolbar()
        setupViewsAndClicks()
        resetLiveDataStates()
        setupObservers()

        checkoutViewModel.orderDetails = args.orderDetailsFromCart
        checkoutViewModel.orderDetailsChangedEvent.value = checkoutViewModel.orderDetails
        checkoutViewModel.activeOrderQuery()
    }

    private fun resetLiveDataStates() {
        checkoutViewModel.activeOrderQueryMediator =
            MediatorLiveData<Resource<ActiveOrderQuery.ActiveOrder>>()
        checkoutViewModel.placeOrderOperationMediator = MediatorLiveData<Resource<Any?>>()
    }

    private fun setupObservers() {
        with(checkoutViewModel) {

            orderDetailsChangedEvent.observe(viewLifecycleOwner, Observer {
                setupOrderDetails(it)
            })

            /** this toast should never show up */
            notLoggedInEvent.observe(viewLifecycleOwner, Observer {
                Toast.makeText(
                    requireContext(),
                    "Not logged in. Please login to continue.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().onBackPressed()
            })

            alertDialogEvent.observe(viewLifecycleOwner, Observer {
                requireActivity().showCustomAlertDialog(
                    context = requireActivity(),
                    title = it.title,
                    message = it.message,
                    negativeBtnText = null
                )
            })

            activeOrderQueryMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (activity as MainActivity).showLoading("")
                    }
                    Status.SUCCESS -> {
                        (activity as MainActivity).hideLoading()
                        it.data?.let { activeOrder ->
                            if (checkoutViewModel.orderDetails != null) {
                                // * sync fetched data with current orderDetails
                                checkoutViewModel.orderDetails!!.id = activeOrder.id
                                checkoutViewModel.orderDetails!!.orderNumber = activeOrder.code
                                checkoutViewModel.orderDetails!!.vendureOrderState =
                                    activeOrder.state
                                checkoutViewModel.orderDetails!!.productVariantList =
                                    ProductVariant.parseToProductVariants(activeOrder)
                                checkoutViewModel.orderDetails!!.subTotal =
                                    ProductVariant.removeTrailingZeroInPrice(activeOrder.subTotalWithTax)
                                checkoutViewModel.orderDetails!!.deliveryCharge =
                                    ProductVariant.removeTrailingZeroInPrice(activeOrder.shippingWithTax)
                                checkoutViewModel.orderDetails!!.total =
                                    ProductVariant.removeTrailingZeroInPrice(activeOrder.totalWithTax)
                            } else {
                                checkoutViewModel.orderDetails =
                                    OrderDetails.parseToOrderDetail(activeOrder)
                            }
                            checkoutViewModel.orderDetailsChangedEvent.value =
                                checkoutViewModel.orderDetails
                        }

                    }
                    Status.ERROR -> {
                        (activity as MainActivity).hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null,
                            positiveBtnClicked = {
                                requireActivity().onBackPressed()
                            }
                        )
                    }
                }
            })

            placeOrderOperationMediator.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        (activity as MainActivity).showLoading("")
                    }
                    Status.SUCCESS -> {
                        (activity as MainActivity).hideLoading()
                        checkoutViewModel.clearShoppingList()
                        val bundle = Bundle().apply {
                            putBoolean("isFromCheckoutPage", true)
                        }
                        findNavController().navigate(
                            R.id.action_checkoutFragment_to_homeFragment,
                            bundle
                        )
                    }
                    Status.ERROR -> {
                        (activity as MainActivity).hideLoading()
                        requireActivity().showCustomAlertDialog(
                            context = requireActivity(),
                            message = it.message!!,
                            negativeBtnText = null,
                            positiveBtnClicked = {
                                requireActivity().onBackPressed()
                            }
                        )
                    }
                }
            })
        }
    }

    private fun setupViewsAndClicks() {

        // * add payment options
        val paymentOptions = arrayListOf<String>("Payment on Delivery")
        val adapter = ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, paymentOptions)
        binding?.etPaymentOptions?.setAdapter(adapter)

        // * setup order summary product list
        orderSummaryCartAdapter = OrderSummaryListAdapter(
            arrayListOf()
        )
        binding?.rvOrderSummary?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvOrderSummary?.adapter = orderSummaryCartAdapter

        // * setup checkout button
        binding?.btnSubmitOrder?.setOnClickListener {
            checkoutViewModel.orderDetails?.deliveryLocation = Location(
                savedLocationName = binding?.etShippingAddress?.text.toString(),
                extraNote = binding?.etExtraNotes?.text.toString()
            )
            checkoutViewModel.orderDetails?.paymentOption = PaymentOption(
                paymentName = binding?.etPaymentOptions?.text.toString()
            )
            if (checkoutViewModel.validateCheckoutDetails() && checkoutViewModel.orderDetails != null) {
                checkoutViewModel.placeOrderOperation(checkoutViewModel.orderDetails!!)
            }
        }
    }

    private fun setupOrderDetails(orderDetails: OrderDetails) {
        binding?.let {
            it.tvOrderPrice.text = orderDetails.subTotal.toString()
            it.tvDeliveryPrice.text = orderDetails.deliveryCharge.toString()
            var total = 0
            total = orderDetails.subTotal + orderDetails.deliveryCharge
            //update the total
            orderDetails.total = total
            orderSummaryCartAdapter.addItems(orderDetails.productVariantList as MutableList<ProductVariant>)
            it.tvSummaryPrice.text = total.toString()
        }
    }

    private fun setupToolbar() {
        binding?.includedToolbar?.ivBack?.visibility = View.VISIBLE
        binding?.includedToolbar?.tvTitle?.text =
            getString(R.string.text_checkout_title)
        binding?.includedToolbar?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}