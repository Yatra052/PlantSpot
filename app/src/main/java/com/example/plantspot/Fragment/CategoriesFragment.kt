package com.example.plantspot.Fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantspot.Adapter.AdapterProduct
import com.example.plantspot.CartListener
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.FragmentCategoriesBinding
import com.example.plantspot.databinding.ItemViewProductBinding
import com.example.plantspot.model.Product
import com.example.plantspot.roomdb.CartProductTable
import kotlinx.coroutines.launch


class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private  var category: String ?= null
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)

        setStatusBarColor()
        getProductCategory()
        setToolbarTitle()
        onsearchMenuClick()
        fetchCategoryProduct()
        onNavigationIconClick()

        return binding.root

       }

    private fun onNavigationIconClick() {
        binding.tbSearchFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_categoriesFragment_to_homeFragment)
        }
    }

    private fun onsearchMenuClick() {
    binding.tbSearchFragment.setOnMenuItemClickListener{ menuItem ->
        when(menuItem.itemId){
             R.id.searchMenu -> {
               findNavController().navigate(R.id.action_categoriesFragment_to_searchFragment)
              true
             }

            else -> {false}
        }

    }
    }

    private fun fetchCategoryProduct() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
      viewModel.getCategoryProduct(category!!).collect{
          if (it.isEmpty()) {
              binding.rvProducts.visibility = View.GONE
              binding.tvText.visibility = View.VISIBLE
          } else {
              binding.rvProducts.visibility = View.VISIBLE
              binding.tvText.visibility = View.GONE
          }
          adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)
          binding.rvProducts.adapter = adapterProduct
          adapterProduct.differ.submitList(it)
//          adapterProduct.originalList = it as ArrayList<Product>

          binding.shimmerViewContainer.visibility = View.GONE


      }

      }
    }

    private fun setToolbarTitle() {
      binding.tbSearchFragment.title = category
    }

    private fun getProductCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }




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



    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.green)
            statusBarColor = statusBarColors

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
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