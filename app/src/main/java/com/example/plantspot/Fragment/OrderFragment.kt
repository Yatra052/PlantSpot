package com.example.plantspot.Fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantspot.Adapter.AdapterOrders
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.FragmentOrderBinding
import com.example.plantspot.model.OrderedItems
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class OrderFragment : Fragment() {
    private lateinit var binding:FragmentOrderBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)
        onbackbuttonclicked()
        getAllOrders()
        setStatusBarColor()
        return binding.root
    }

    private fun getAllOrders() {

        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect {orderList ->

                if (orderList.isNotEmpty())
                {
                    val orderedList = ArrayList<OrderedItems>()
                   for (orders in orderList)
                   {
                        val title = StringBuilder()

                       var totalPrice = 0

                       for (products in orders.orderList!!)
                       {
                           val price = products.productPrice?.substring(1)?.toInt()
                           val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                           title.append("${products.productCategory}")

                       }

                       val orderedItems = OrderedItems(orders.orderId, orders.orderDate, orders.orderStatus, title.toString(), totalPrice)
                        orderedList.add(orderedItems)



                       adapterOrders = AdapterOrders(requireContext(), ::onOrderItemViewClicked)
                       binding.rvOrders.adapter = adapterOrders
                       adapterOrders.differ.submitList(orderedList)
                       binding.shimmerViewContainer.visibility = View.GONE

                   }
                }


            }
        }
    }

    fun onOrderItemViewClicked(orderedItems: OrderedItems)
    {

        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)

       findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle)


    }
    private fun onbackbuttonclicked() {
        binding.tbProfileFragment.setOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_profileeFragment)
        }
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.green)
            statusBarColor = statusBarColors

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}