package com.example.finalapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.R
import com.example.finalapp.models.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemDeleted: (CartItem) -> Unit,
    private val onItemEdited: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        val decreaseButton: ImageView = itemView.findViewById(R.id.decreaseButton)
        val increaseButton: ImageView = itemView.findViewById(R.id.increaseButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        
        holder.productName.text = item.name
        holder.productPrice.text = "Rs ${item.price}"
        holder.quantityText.text = item.quantity.toString()
        
        // Set placeholder image based on product name/category
        val imageResource = when {
            item.name.contains("food", ignoreCase = true) -> R.drawable.placeholder_product_1
            item.name.contains("toy", ignoreCase = true) -> R.drawable.placeholder_product_2
            else -> R.drawable.placeholder_product_3
        }
        holder.productImage.setImageResource(imageResource)
        
        // Quantity controls
        holder.decreaseButton.setOnClickListener {
            if (item.quantity > 1) {
                onQuantityChanged(item, item.quantity - 1)
            }
        }
        
        holder.increaseButton.setOnClickListener {
            onQuantityChanged(item, item.quantity + 1)
        }
        
        // Action buttons
        holder.deleteButton.setOnClickListener {
            onItemDeleted(item)
        }
        
        holder.editButton.setOnClickListener {
            onItemEdited(item)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
