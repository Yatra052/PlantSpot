package com.example.plantspot.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.plantspot.databinding.ItemProductCatBinding
import com.example.plantspot.model.Category

class AdapterCategory(
    val categoryList: ArrayList<Category>,
   val onCategoryIconClicked: (Category) -> Unit

): RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemProductCatBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCategory.CategoryViewHolder {
     return CategoryViewHolder(ItemProductCatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: AdapterCategory.CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
             IvCategory.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }

        holder.itemView.setOnClickListener{
            onCategoryIconClicked(category)
        }
    }

    override fun getItemCount(): Int {
     return categoryList.size
    }

}