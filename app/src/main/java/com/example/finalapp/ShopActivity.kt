package com.example.finalapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.models.CartItem
import com.example.finalapp.models.Product
import com.example.finalapp.utils.CartManager
import com.example.finalapp.adapters.ProductAdapter

class ShopActivity : AppCompatActivity() {
    
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchEditText: EditText
    private lateinit var cartIcon: ImageView
    private lateinit var cartBadge: TextView
    private lateinit var cartManager: CartManager
    
    private val allProducts = mutableListOf<Product>()
    private val filteredProducts = mutableListOf<Product>()
    private var selectedCategory = "All"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        initializeViews()
        setupRecyclerView()
        setupSearchFunctionality()
        setupCategoryTabs()
        loadSampleProducts()
        setupCartFunctionality()
        
        // Setup navigation bar
        NavigationHelper.setupNavigation(this, "shop")
    }
    
    private fun initializeViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        cartIcon = findViewById(R.id.cartIcon)
        cartBadge = findViewById(R.id.cartBadge)
        cartManager = CartManager()
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(filteredProducts) { product ->
            showAddToCartDialog(product)
        }
        productsRecyclerView.layoutManager = GridLayoutManager(this, 1)
        productsRecyclerView.adapter = productAdapter
    }
    
    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterProducts(s.toString())
            }
        })
    }
    
    private fun setupCategoryTabs() {
        val categoryAll = findViewById<TextView>(R.id.categoryAll)
        val categoryFood = findViewById<TextView>(R.id.categoryFood)
        val categoryToys = findViewById<TextView>(R.id.categoryToys)
        val categoryAccessories = findViewById<TextView>(R.id.categoryAccessories)
        
        categoryAll.setOnClickListener { selectCategory("All", categoryAll) }
        categoryFood.setOnClickListener { selectCategory("Food", categoryFood) }
        categoryToys.setOnClickListener { selectCategory("Toys", categoryToys) }
        categoryAccessories.setOnClickListener { selectCategory("Accessories", categoryAccessories) }
    }
    
    private fun selectCategory(category: String, selectedView: TextView) {
        selectedCategory = category
        
        // Reset all category buttons
        resetCategoryButtons()
        
        // Highlight selected category
        selectedView.setBackgroundResource(R.drawable.glass_button_background)
        selectedView.setTextColor(resources.getColor(R.color.white, null))
        
        filterProducts(searchEditText.text.toString())
    }
    
    private fun resetCategoryButtons() {
        val categories = listOf(
            findViewById<TextView>(R.id.categoryAll),
            findViewById<TextView>(R.id.categoryFood),
            findViewById<TextView>(R.id.categoryToys),
            findViewById<TextView>(R.id.categoryAccessories)
        )
        
        categories.forEach { category ->
            category.setBackgroundResource(R.drawable.glass_card_background)
            category.setTextColor(resources.getColor(R.color.text_dark, null))
        }
    }
    
    private fun filterProducts(searchQuery: String) {
        filteredProducts.clear()
        
        val filtered = allProducts.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
            
            matchesSearch && matchesCategory
        }
        
        filteredProducts.addAll(filtered)
        productAdapter.notifyDataSetChanged()
    }
    
    private fun loadSampleProducts() {
        allProducts.addAll(listOf(
            Product("1", "Premium Dog Food", "High-quality nutrition for adult dogs", 299.0, "", "Food", "Royal Canin", true, 4.5f, 120),
            Product("2", "Interactive Puzzle", "Mental stimulation toy for cats", 2564.99, "", "Toys", "PetSafe", true, 4.2f, 85),
            Product("3", "Adjustable Leather Leash", "Durable leather leash for dogs", 1800.0, "", "Accessories", "PawPro", true, 4.7f, 200),
            Product("4", "Cat Scratching Post", "Multi-level scratching post", 1299.0, "", "Accessories", "KittyPlay", true, 4.3f, 95),
            Product("5", "Pet Grooming Kit", "Complete grooming set", 4299.0, "", "Accessories", "GroomPro", true, 4.6f, 150),
            Product("6", "Orthopedic Pet Bed", "Memory foam pet bed", 5399.0, "", "Accessories", "ComfyPet", true, 4.8f, 75),
            Product("7", "Dental Chew Treats", "Healthy dental treats for dogs", 345.0, "", "Food", "DentaFresh", true, 4.4f, 180),
            Product("8", "Feather Wand Toy", "Interactive feather toy for cats", 24.99, "", "Toys", "PlayTime", true, 4.1f, 65)
        ))
        
        filteredProducts.addAll(allProducts)
        productAdapter.notifyDataSetChanged()
    }
    
    private fun setupCartFunctionality() {
        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        
        // Update cart badge
        cartManager.getCartItemCount { count ->
            runOnUiThread {
                if (count > 0) {
                    cartBadge.text = count.toString()
                    cartBadge.visibility = View.VISIBLE
                } else {
                    cartBadge.visibility = View.GONE
                }
            }
        }
    }
    
    private fun showAddToCartDialog(product: Product) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_to_cart)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val productImage = dialog.findViewById<ImageView>(R.id.productImage)
        val productName = dialog.findViewById<TextView>(R.id.productName)
        val productBrand = dialog.findViewById<TextView>(R.id.productBrand)
        val productPrice = dialog.findViewById<TextView>(R.id.productPrice)
        val quantityText = dialog.findViewById<TextView>(R.id.quantityText)
        val decreaseButton = dialog.findViewById<ImageView>(R.id.decreaseButton)
        val increaseButton = dialog.findViewById<ImageView>(R.id.increaseButton)
        val backButton = dialog.findViewById<Button>(R.id.backButton)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)
        val viewCartButton = dialog.findViewById<TextView>(R.id.viewCartButton)
        
        var quantity = 1
        
        // Set product details
        productName.text = product.name
        productBrand.text = product.brand
        productPrice.text = "Rs ${product.price}"
        quantityText.text = quantity.toString()
        
        // Quantity controls
        decreaseButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.text = quantity.toString()
            }
        }
        
        increaseButton.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
        }
        
        // Action buttons
        backButton.setOnClickListener {
            dialog.dismiss()
        }
        
        confirmButton.setOnClickListener {
            val cartItem = CartItem(
                id = "",
                productId = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                imageUrl = product.imageUrl,
                quantity = quantity
            )
            
            cartManager.addToCart(cartItem) { success, message ->
                runOnUiThread {
                    if (success) {
                        showSuccessNotification(product.name)
                        setupCartFunctionality() // Refresh cart badge
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        
        viewCartButton.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, CartActivity::class.java))
        }
        
        dialog.show()
    }
    
    private fun showSuccessNotification(productName: String) {
        val toast = Toast.makeText(this, "✅ $productName added to cart", Toast.LENGTH_SHORT)
        toast.show()
        
        // Show suggestion notification after a delay
        Handler(mainLooper).postDelayed({
            Toast.makeText(this, "💡 You might also like similar products!", Toast.LENGTH_LONG).show()
        }, 2000)
    }
}
