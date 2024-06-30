package com.example.plantspot.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.plantspot.FilteringProduct
import com.example.plantspot.databinding.ItemViewProductBinding
import com.example.plantspot.model.Product
import com.google.firebase.database.core.view.View

class AdapterProduct(
    val onAddButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable{
    class ProductViewHolder(val binding: ItemViewProductBinding) : ViewHolder(binding.root) {

    }

    val diffutil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }


        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffutil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemViewProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]


        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()

            val productImage = product.productImageUris

//            if (productImage != null)
//            {
            if (productImage != null) {
                for (i in 0 until productImage.size) {
                    imageList.add(SlideModel(product.productImageUris!![i].toString()))
                    //                }
                }
            }

            ivImageSlider.setImageList(imageList)
            tvProductTitle.text = product.productTitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantitiy.text = quantity

            tvProductPrice.text = "₹" + product.productPrice


            if (product.itemCount!! > 0)
            {
                tvProductCount.text = product.itemCount.toString()

                tvAdd.visibility = android.view.View.GONE
                 llProductCount.visibility = android.view.View.VISIBLE

            }
            tvAdd.setOnClickListener {
                onAddButtonClicked(product, this)
            }

             tvIncrementCount.setOnClickListener{
                 onIncrementButtonClicked(product, this)
             }

            tvDecrementCount.setOnClickListener{
                onDecrementButtonClicked(product, this)
            }

        }
    }


    val filter: FilteringProduct ?= null
        var originalList = ArrayList<Product>()
        override fun getFilter(): Filter {
          if (filter == null)
          {
              return FilteringProduct(this, originalList)
          }
            return filter
        }
}