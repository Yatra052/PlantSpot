package com.example.plantspot.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.example.plantspot.Adapter.AdapterCartProduct
import com.example.plantspot.CartListener
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.ActivityUserMainBinding
import com.example.plantspot.databinding.BsCartProductBinding
import com.example.plantspot.roomdb.CartProductTable
import com.google.android.material.bottomsheet.BottomSheetDialog

class UserMainActivity : AppCompatActivity() , CartListener {
    private lateinit var binding: ActivityUserMainBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartProductList: List<CartProductTable>
    private lateinit var adapterCartProduct: AdapterCartProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        getTotalItemCountInCart()
        onNextButtonClicked()

        onCartClicked()

    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
          startActivity(Intent(this, OrderPlacedActivity::class.java))

        }
    }


    private fun getAllCartProducts()
        {
            viewModel.getAll().observe(this){
                    cartProductList = it


            }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener{
         val bsCartProductBinding = BsCartProductBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductBinding.root)

            bsCartProductBinding.tvNumberofprodCount.text = binding.tvNumberofprodCount.text

            bsCartProductBinding.btnNext.setOnClickListener{
                startActivity(Intent(this, OrderPlacedActivity::class.java))
            }
            adapterCartProduct = AdapterCartProduct()
          bsCartProductBinding.rvProductsItem.adapter = adapterCartProduct
            adapterCartProduct.differ.submitList(cartProductList)

            bs.show()
        }

    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalItemCount().observe(this){

            if (it > 0)
            {
                binding.llcart.visibility = View.VISIBLE
                binding.tvNumberofprodCount.text = it.toString()
            }
            else{
               binding.llcart.visibility = View.GONE

            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun showCartLayout(itemCount: Int) {
     val previousCount =  binding.tvNumberofprodCount.text.toString().toInt()
       val updatedCount  = previousCount + itemCount

         if (updatedCount > 0)
         {
             binding.llcart.visibility = View.VISIBLE
             binding.tvNumberofprodCount.text = updatedCount.toString()

         }
        else
         {
             binding.llcart.visibility = View.GONE
             binding.tvNumberofprodCount.text = "0"
         }
    }

    override fun savingCartItemCount(itemCount: Int) {
//       val  previousCount =  binding.tvNumberofprodCount.text.toString().toInt()

        viewModel.fetchTotalItemCount().observe(this){
            viewModel.savingCartItemCount(it + itemCount)

        }



    }

    override fun hideCartLayout() {
       binding.llcart.visibility = View.GONE
        binding.tvNumberofprodCount.text = "0"
    }


}