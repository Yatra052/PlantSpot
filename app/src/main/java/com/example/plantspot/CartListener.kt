package com.example.plantspot

interface CartListener {

    fun showCartLayout(itemCount: Int)


    fun savingCartItemCount(itemCount: Int)


      fun hideCartLayout()
}