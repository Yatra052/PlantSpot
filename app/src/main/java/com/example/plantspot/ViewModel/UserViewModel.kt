package com.example.plantspot.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.plantspot.Constants
import com.example.plantspot.api.ApiUtilities
import com.example.plantspot.model.Notification
import com.example.plantspot.model.NotificationData
import com.example.plantspot.model.Orders
import com.example.plantspot.model.Product
import com.example.plantspot.roomdb.CartProductDAO
import com.example.plantspot.roomdb.CartProductDatabase
import com.example.plantspot.roomdb.CartProductTable
import com.example.plantspot.utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(application:Application):AndroidViewModel(application) {

    val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("My_Pref", MODE_PRIVATE)
    val cartProductDao: CartProductDAO =
        CartProductDatabase.getDatabaseInstance(application).cartProductsDao()


    private val _paymentStatus = MutableStateFlow<Boolean>(false)
    val paymentStatus = _paymentStatus


    suspend fun insertCartProduct(products: CartProductTable) {
        cartProductDao.insertCartProduct(products)
    }

    fun getAll(): LiveData<List<CartProductTable>> {
        return cartProductDao.getAllCartProducts()
    }

    suspend fun updateCartProduct(products: CartProductTable) {
        cartProductDao.updateCartProduct(products)
    }


    suspend fun deleteCartProduct(productId: String) {
        cartProductDao.deleteCartProduct(productId)
    }

    fun fetchAllTheProducts(): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()

                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }

                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }

    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
            .orderByChild("orderStatus")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Orders::class.java)
                    if (order?.orderingUserId ==  utils.getCurrentUserId())
                    {
                        orderList.add(order!!)
                    }
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        db.addValueEventListener(eventListener)
        awaitClose{
            db.removeEventListener(eventListener)
        }
    }

        fun getCategoryProduct(category: String): Flow<List<Product>> = callbackFlow {
            val db = FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductCategory/${category}")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val products = ArrayList<Product>()

                    for (product in snapshot.children) {
                        val prod = product.getValue(Product::class.java)
                        products.add(prod!!)
                    }

                    trySend(products)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }

            db.addValueEventListener(eventListener)

            awaitClose {
                db.removeEventListener(eventListener)
            }


        }


       fun getOrderedProducts(orderId: String): Flow<List<CartProductTable>> = callbackFlow {
      val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
           val eventListener = object : ValueEventListener{
               override fun onDataChange(snapshot: DataSnapshot) {
                  val order = snapshot.getValue(Orders::class.java)
                  trySend(order?.orderList!!)
               }

               override fun onCancelled(error: DatabaseError) {
                   TODO("Not yet implemented")
               }

           }
           db.addValueEventListener(eventListener)
           awaitClose{db.removeEventListener(eventListener)}

       }






        fun updateItemCount(product: Product, itemCount: Int) {
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("AllProducts/${product.productRandomId}")
                .child("itemCount").setValue(itemCount)
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductCategory/${product.productCategory}/${product.productRandomId}")
                .child("itemCount").setValue(itemCount)
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductType/${product.productType}/${product.productRandomId}")
                .child("itemCount").setValue(itemCount)

        }


        fun saveProductsAfterOrder(stock: Int, product: CartProductTable) {
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("AllProducts/${product.productId}")
                .child("itemCount").setValue(0)
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductCategory/${product.productCategory}/${product.productId}")
                .child("itemCount").setValue(0)
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductType/${product.productType}/${product.productId}")
                .child("itemCount").setValue(0)



            FirebaseDatabase.getInstance().getReference("Admins")
                .child("AllProducts/${product.productId}")
                .child("productStock").setValue(stock)
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductCategory/${product.productCategory}/${product.productId}")
                .child("productStock").setValue(stock)
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("ProductType/${product.productType}/${product.productId}")
                .child("itemCount").setValue(stock)


        }

        fun saveUserAddress(address: String) {
            FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
                .child(utils.getCurrentUserId()!!)
                .child("userAddress").setValue(address)

        }

        fun savingCartItemCount(itemCount: Int) {
            sharedPreferences.edit().putInt("itemCount", itemCount).apply()
        }


        fun getUserAddress(callback: (String) -> Unit) {

            val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
                .child(utils.getCurrentUserId()!!)
                .child("userAddress")

            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val address = snapshot.getValue(String::class.java)
                        if (address != null) {
                            callback(address)
                        } else {
                            callback(null.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null.toString())
                }

            })
        }

        fun saveOrdersProducts(orders: Orders) {
            FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
                .child(orders.orderId!!).setValue(orders)

        }

        fun fetchTotalItemCount(): MutableLiveData<Int> {
            val totalItemCount = MutableLiveData<Int>()

            totalItemCount.value = sharedPreferences.getInt("itemCount", 0)

            return totalItemCount

        }


        fun saveAddressStatus() {
            sharedPreferences.edit().putBoolean("addressStatus", true).apply()

        }

        fun getAddressStatus(): MutableLiveData<Boolean> {
            val status = MutableLiveData<Boolean>()
            status.value = sharedPreferences.getBoolean("addressStatus", false)
            return status

        }

        suspend fun deleteCartProducts() {
            cartProductDao.deleteCartProducts()
        }

        //calling api
        suspend fun checkPayment(headers: Map<String, String>) {
            val res = ApiUtilities.statusApi.checkStatus(
                headers,
                Constants.MERCHANTID,
                Constants.merchantTransactionId
            )


            _paymentStatus.value = res.body() != null && res.body()!!.success


        }


        suspend fun sendNotification(adminUid: String, title:String, message:String)
        {
           val getToken =  FirebaseDatabase.getInstance().getReference("Admins").child("AdminInfo").child(adminUid).child("adminToken")
               .get()
            getToken.addOnCompleteListener{task ->

                val token = task.result.getValue(String::class.java)
                val notification = Notification(token, NotificationData(title, message))

                ApiUtilities.notificationApi.sendNotification(notification).enqueue(object: Callback<Notification>{
                    override fun onResponse(
                        call: Call<Notification>,
                        response: Response<Notification>
                    ) {
                        if (response.isSuccessful)
                        {
                            Log.d("ggg", "Sent Notification")
                        }
                    }

                    override fun onFailure(call: Call<Notification>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })

            }


        }






    }



