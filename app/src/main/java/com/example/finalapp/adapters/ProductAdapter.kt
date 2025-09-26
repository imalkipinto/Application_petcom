package com.example.finalapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.R
import com.example.finalapp.models.Product

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productBrand: TextView = itemView.findViewById(R.id.productBrand)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productRating: TextView = itemView.findViewById(R.id.productRating)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
        val addButton: ImageView = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.productName.text = product.name
        holder.productBrand.text = product.brand
        holder.productDescription.text = product.description
        holder.productPrice.text = "Rs ${product.price}"
        holder.productRating.text = product.rating.toString()
        
        // Set placeholder images based on category
        val imageResource = when (product.category) {
            "Food" -> R.drawable.placeholder_product_1
            "Toys" -> R.drawable.placeholder_product_2
            "Accessories" -> R.drawable.placeholder_product_3
            else -> R.drawable.placeholder_product_1
        }
        holder.productImage.setImageResource(imageResource)
        
        // Handle favorite click
        holder.favoriteIcon.setOnClickListener {
            // Toggle favorite state (you can implement this with SharedPreferences or Firebase)
            val currentTint = holder.favoriteIcon.colorFilter
            if (currentTint == null) {
                holder.favoriteIcon.setColorFilter(android.graphics.Color.RED)
            } else {
                holder.favoriteIcon.clearColorFilter()
            }
        }
        
        // Handle add to cart click
        holder.addButton.setOnClickListener {
            onAddToCartClick(product)
        }
    }

    override fun getItemCount(): Int = products.size
}
