package com.example.plantspot.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantspot.Adapter.AdapterCartProduct
import com.example.plantspot.Adapter.AdapterOrders
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.FragmentOrderDetailBinding
import kotlinx.coroutines.launch


class OrderDetailFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentOrderDetailBinding
    private var status = 0
    private var orderId = ""
    private lateinit var adapterCartProduct: AdapterCartProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override  fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        getValues()
        settingStatus()
        onbackbuttonclicked()

        lifecycleScope.launch {
            getOrderedProducts()

        }
        return (binding.root)
    }

    private fun onbackbuttonclicked() {
        binding.tbOrderDetailFragment.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
        }
    }

    suspend fun getOrderedProducts() {
            viewModel.getOrderedProducts(orderId).collect {cartList ->
             adapterCartProduct = AdapterCartProduct()
                binding.rvProductsItem.adapter  =adapterCartProduct
                adapterCartProduct.differ.submitList(cartList)

        }
    }

    private fun settingStatus() {
        val statusToViews =  mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1, binding.iv2, binding.view1),
            2 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2),
            3 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2, binding.iv4, binding.view3)

            )


        val viewsToInt = statusToViews.getOrDefault(status, emptyList())

        for (view in  viewsToInt)
        {
            view.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)

        }
    }

    private fun getValues() {
      val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()


    }


}