package com.example.plantspot.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.plantspot.R
import com.example.plantspot.databinding.ItemviewOrdersBinding
import com.example.plantspot.model.OrderedItems

class AdapterOrders(var context: Context, val onOrderItemViewClicked: (OrderedItems) -> Unit): RecyclerView.Adapter<AdapterOrders.OrdersViewHolder>() {

    class OrdersViewHolder (val binding: ItemviewOrdersBinding): ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>() {
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
           return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
          return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
      return OrdersViewHolder(ItemviewOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {

        val orders = differ.currentList[position]


        holder.binding.apply {
         tvOrderTitles.text = orders.itemTitle
            tvOrderDate.text = orders.itemDate
            tvOrderAmount.text = orders.itemPrice.toString()
             when(orders.itemStatus)
             {
                 0->{
                  tvOrderStatus.text = "Ordered"
                     tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green)
                 }
                 1->{
                    tvOrderStatus.text = "Received"
                     tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
                 }
                 2->{
                     tvOrderStatus.text = "Dispatched"
                     tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue)
                 }

                 3->{
                     tvOrderStatus.text = "Delivered"
                     tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
                 }

             }

        }

        holder.itemView.setOnClickListener{
            onOrderItemViewClicked(orders)
        }
    }
}