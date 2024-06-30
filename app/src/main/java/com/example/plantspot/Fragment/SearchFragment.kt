package com.example.plantspot.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantspot.Adapter.AdapterProduct
import com.example.plantspot.CartListener
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.FragmentSearchBinding
import com.example.plantspot.databinding.ItemViewProductBinding
import com.example.plantspot.model.Product
import com.example.plantspot.roomdb.CartProductTable

import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

private lateinit var binding: FragmentSearchBinding
private lateinit var adapterProduct: AdapterProduct
private var cartListener: CartListener? = null
 val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)

        searchProduct()
        getAllTheProducts()
        backToHomeFragment()
        return binding.root
    }

    private fun searchProduct() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SuspiciousIndentation")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.filter?.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
    private fun backToHomeFragment() {
        binding.searchEt.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)

        }

    }



    private fun getAllTheProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts().collect {

                if (it.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>

                binding.shimmerViewContainer.visibility = View.GONE

            }
        }

    }



//    private fun onAddButtonClicked(product: Product, productBinding:ItemViewProductBinding)
//    {
//        productBinding.tvAdd.visibility = View.GONE
//        productBinding.llProductCount.visibility = View.VISIBLE
//
//
//        var itemCount = productBinding.tvProductCount.text.toString().toInt()
//        itemCount++
//        productBinding.tvProductCount.text = itemCount.toString()
//
//
//        cartListener?.showCartLayout(1)
//
//        //step 2
//        cartListener?.savingCartItemCount(1)
//
//    }
private fun onAddButtonClicked(product: Product, productBinding:ItemViewProductBinding)
{
    productBinding.tvAdd.visibility = View.GONE
    productBinding.llProductCount.visibility = View.VISIBLE


    var itemCount = productBinding.tvProductCount.text.toString().toInt()
    itemCount++

    productBinding.tvProductCount.text = itemCount.toString()


    cartListener?.showCartLayout(1)


    //step 2
    product.itemCount = itemCount
    lifecycleScope.launch {
        cartListener?.savingCartItemCount(1)
        saveProductInRoomDb(product)
        viewModel.updateItemCount(product, itemCount)
    }



}

//
//    fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
//
//        var itemCountIncrement = productBinding.tvProductCount.text.toString().toInt()
//        itemCountIncrement++
//        productBinding.tvProductCount.text = itemCountIncrement.toString()
//
//        cartListener?.showCartLayout(1)
//
//        cartListener?.savingCartItemCount(1)
//
//
//    }
//
//
fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){

    var itemCountIncrement = productBinding.tvProductCount.text.toString().toInt()
    itemCountIncrement++

    if (product.productStock!! + 1 > itemCountIncrement)
    {
        productBinding.tvProductCount.text = itemCountIncrement.toString()

        cartListener?.showCartLayout(1)

//        cartListener?.savingCartItemCount(1)
        product.itemCount = itemCountIncrement
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCountIncrement)
        }

    }
    else{
        Toast.makeText(requireContext(), "Can't add more item  of this", Toast.LENGTH_SHORT).show()
    }
}


//    fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
//        var itemCountDecrement = productBinding.tvProductCount.text.toString().toInt()
//        itemCountDecrement--
//
//        if (itemCountDecrement > 0)
//        {
//            productBinding.tvProductCount.text = itemCountDecrement.toString()
//
//        }
//
//        else{
//            productBinding.tvAdd.visibility = View.VISIBLE
//            productBinding.llProductCount.visibility = View.GONE
//            productBinding.tvProductCount.text = "0"
//        }
//
//
//        cartListener?.showCartLayout(-1)
//
//        cartListener?.savingCartItemCount(-1)
//
//    }
//

    fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountDecrement = productBinding.tvProductCount.text.toString().toInt()
        itemCountDecrement--


        product.itemCount = itemCountDecrement
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)

        }
        if (itemCountDecrement > 0)
        {
            productBinding.tvProductCount.text = itemCountDecrement.toString()

        }

        else {
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productRandomId!!)
                productBinding.tvAdd.visibility = View.VISIBLE
                productBinding.llProductCount.visibility = View.GONE
                productBinding.tvProductCount.text = "0"
                viewModel.updateItemCount(product, itemCountDecrement)
            }
        }

        cartListener?.showCartLayout(-1)

//        cartListener?.savingCartItemCount(-1)


    }



    private fun saveProductInRoomDb(product: Product) {
        val  cartProducts = CartProductTable(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹$product.productPrice",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProducts)

        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CartListener)
        {
            cartListener = context
        }

        else{
            throw  ClassCastException("Please implement cart listener")
        }
    }




}