package com.example.plantspot.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.produceState
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.plantspot.Adapter.AdapterCartProduct
import com.example.plantspot.CartListener
import com.example.plantspot.Constants
import com.example.plantspot.Constants.apiEndPoint
import com.example.plantspot.R
import com.example.plantspot.ViewModel.UserViewModel
import com.example.plantspot.databinding.ActivityOrderPlacedBinding
import com.example.plantspot.databinding.AddressLayoutBinding
import com.example.plantspot.model.Orders
import com.example.plantspot.utils
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.flow.collect


import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class OrderPlacedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlacedBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProduct: AdapterCartProduct
    private lateinit var b2BPGRequest: B2BPGRequest
    private var cartListener: CartListener ? =null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        getAllCartProducts()
        backToUserMainActivity()
        onPlaceOrderClicked()
        initializePhonePay()
    }

    private fun initializePhonePay() {
        val data = JSONObject()
      PhonePe.init(this, PhonePeEnvironment.SANDBOX, Constants.MERCHANTID, "")

        data.put("merchantId", Constants.MERCHANTID)
        data.put("merchantTransactionId", Constants.merchantTransactionId)
        data.put("amount", 200)
        data.put("mobileNumber", 9302291711)
        data.put("callbackUrl", "https://webhook.site/callback-u")


        val paymentInstrument = JSONObject()
        paymentInstrument.put("type", "UPI_INTENT")
        paymentInstrument.put("targetApp", "com.phonepe.simulator")

        data.put("paymentInstrument", paymentInstrument)

        val deviceContext = JSONObject()
        deviceContext.put("deviceOS", "ANDROID")
        data.put("deviceContext", deviceContext)


//        paymentInstrument.put("targetApp", "UPI_INTENT")

        val payloadBase64 = android.util.Base64.encodeToString(
            data.toString().toByteArray(Charset.defaultCharset()), android.util.Base64.NO_WRAP
        )

        val checksum = sha256(payloadBase64 + Constants.apiEndPoint + Constants.SALT_KEY) + "###1";

         b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(Constants.apiEndPoint)
            .build()

    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun onPlaceOrderClicked() {
      binding.btnNext.setOnClickListener {
           viewModel.getAddressStatus().observe(this){status ->
               if (status)
               {
                  //payment work
                   getPaymentView()
               }
               else{
                 val  addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                   alertDialog.show()

                   addressLayoutBinding.btnAdd.setOnClickListener{
                       saveAddress(alertDialog, addressLayoutBinding)
                   }
               }

           }
      }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val phonePayView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        if (it.resultCode == RESULT_OK)
        {
           checkStatus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkStatus() {

        val xVerify =
            sha256("/pg/v1/status/${Constants.MERCHANTID}/${Constants.MERCHANTID}${Constants.SALT_KEY}") + "###1"

        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to Constants.MERCHANTID ,
        )

        lifecycleScope.launch {
            viewModel.checkPayment(headers)
            viewModel.paymentStatus.collect{status ->
                if (status)
                {
                    utils.showToast(this@OrderPlacedActivity, "Payment Done")
                        saveOrder()
                    viewModel.deleteCartProducts()
                    viewModel.savingCartItemCount(0)
                    cartListener?.hideCartLayout()
                        //order save, delete product
                    utils.hideDialog()

                    startActivity(Intent(this@OrderPlacedActivity, UserMainActivity::class.java))
                    finish()
                }
                else{
                    utils.showToast(this@OrderPlacedActivity, "Payment Failed")
                }

            }
        }
    }

//    private fun deleteCartProducts() {
//          viewModel.deleteCartProducts()
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveOrder() {
          viewModel.getAll().observe(this){cartProductsList->
                  if(cartProductsList.isNotEmpty())
                  {
                      viewModel.getUserAddress {address->
                          val orders = Orders(
                              orderId = utils.getRandomId(),
                              orderList = cartProductsList,
                              userAddress = address,
                              orderStatus = 0,
                              orderDate = utils.getCurrentData(),
                              orderingUserId =  utils.getCurrentUserId()


                          )

                          viewModel.saveOrdersProducts(orders)
                          lifecycleScope.launch {
                              viewModel.sendNotification(
                                  cartProductsList[0].adminUid!!,
                                  "Ordered",
                                  "Some products has been ordered"
                              )
                          }

                      }
                  }


              for (products in cartProductsList)
              {
                  val count = products.productCount
//                  products.adminUid
                  val stock = products.productStock?.minus(count!!)
                  if (stock != null) {
                      viewModel.saveProductsAfterOrder(stock, products)

                  }
              }

          }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPaymentView() {
        try {

            PhonePe.getImplicitIntent(this,b2BPGRequest, "com.phonepe.simulator")
                .let {
                 phonePayView.launch(it)

                }
        }
        catch (e: PhonePeInitException)
        {
          utils.showToast(this, e.message.toString())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
            utils.showDialog(this, "Processing...")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDescriptiveAddress.text.toString()

        val address =  "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"
//        utils.showDialog(this, "Saved..")
//
//        alertDialog.dismiss()
//        utils.hideDialog()

//        val users =  Users(userAddress = address)

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()

        }
        utils.showToast(this, "Saved..")
        alertDialog.dismiss()
        utils.hideDialog()

        getPaymentView()


    }

    private fun backToUserMainActivity() {
        binding.tbOrderFragment.setNavigationOnClickListener{
            startActivity(Intent(this, OrderPlacedActivity::class.java))
        }
    }

    private fun setStatusBarColor() {
       window?.apply {
           val statusBarColors = ContextCompat.getColor(this@OrderPlacedActivity, R.color.green)
           statusBarColor = statusBarColors

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
           }

       }

        }


    private fun getAllCartProducts() {
        viewModel.getAll().observe(this) { cartProductsList ->

            adapterCartProduct = AdapterCartProduct()
            binding.rvProductsItems.adapter = adapterCartProduct
            adapterCartProduct.differ.submitList(cartProductsList)


            var totalPrice = 0

            for (products in cartProductsList) {
                val price = products.productPrice?.substring(1)?.toInt()
                val itemCount = products.productCount!!
                totalPrice += (price?.times(itemCount)!!)

            }

            binding.tvSubTotal.text = totalPrice.toString()

            if (totalPrice < 200) {
                binding.tvDeliveryCharge.text = "₹15"
                totalPrice += 15
                binding.tvGrandTotal.text = totalPrice.toString()

            }
        }
    }
}








