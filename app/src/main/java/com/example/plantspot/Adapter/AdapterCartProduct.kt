package com.example.plantspot.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.plantspot.databinding.ItemViewCartProductBinding
import com.example.plantspot.roomdb.CartProductTable
import kotlinx.coroutines.processNextEventInCurrentThread

class AdapterCartProduct: RecyclerView.Adapter<AdapterCartProduct.CartProductViewHolder>() {
    class CartProductViewHolder(val binding: ItemViewCartProductBinding):ViewHolder(binding.root){

    }

    val diffUtil = object : DiffUtil.ItemCallback<CartProductTable>()
    {
        override fun areItemsTheSame(
            oldItem: CartProductTable,
            newItem: CartProductTable
        ): Boolean {
          return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: CartProductTable,
            newItem: CartProductTable
        ): Boolean {
            return oldItem == newItem
        }

    }
     val differ = AsyncListDiffer(this, diffUtil)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
 return CartProductViewHolder(ItemViewCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
      val product = differ.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImage).into(ivProductImage)
            tvproducttitle.text = product.productTitle
            tvProductQuantity.text = product.productQuantity
            tvproductprice.text = product.productPrice
            tvProductCount.text = product.productCount.toString()


        }

    }
}