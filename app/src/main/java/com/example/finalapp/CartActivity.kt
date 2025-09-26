package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.adapters.CartAdapter
import com.example.finalapp.models.CartItem
import com.example.finalapp.utils.CartManager

class CartActivity : AppCompatActivity() {
    
    private lateinit var cartItemsRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartState: LinearLayout
    private lateinit var summaryCard: androidx.cardview.widget.CardView
    private lateinit var subtotalText: TextView
    private lateinit var shippingText: TextView
    private lateinit var totalText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var shopNowButton: Button
    private lateinit var promoCodeEditText: EditText
    private lateinit var applyPromoButton: Button
    private lateinit var cartManager: CartManager
    
    private val cartItems = mutableListOf<CartItem>()
    private var subtotal = 0.0
    private var shipping = 0.0
    private var discount = 0.0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadCartItems()
        
        // Setup navigation bar
        NavigationHelper.setupNavigation(this, "shop")
    }
    
    private fun initializeViews() {
        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView)
        emptyCartState = findViewById(R.id.emptyCartState)
        summaryCard = findViewById(R.id.summaryCard)
        subtotalText = findViewById(R.id.subtotalText)
        shippingText = findViewById(R.id.shippingText)
        totalText = findViewById(R.id.totalText)
        checkoutButton = findViewById(R.id.checkoutButton)
        shopNowButton = findViewById(R.id.shopNowButton)
        promoCodeEditText = findViewById(R.id.promoCodeEditText)
        applyPromoButton = findViewById(R.id.applyPromoButton)
        cartManager = CartManager()
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems,
            onQuantityChanged = { item, newQuantity ->
                updateCartItemQuantity(item, newQuantity)
            },
            onItemDeleted = { item ->
                deleteCartItem(item)
            },
            onItemEdited = { item ->
                editCartItem(item)
            }
        )
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        cartItemsRecyclerView.adapter = cartAdapter
    }
    
    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
        
        findViewById<ImageView>(R.id.addMoreButton).setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }
        
        shopNowButton.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }
        
        checkoutButton.setOnClickListener {
            proceedToCheckout()
        }
        
        applyPromoButton.setOnClickListener {
            applyPromoCode()
        }
    }
    
    private fun loadCartItems() {
        cartManager.getCartItems(object : CartManager.CartListener {
            override fun onCartUpdated(items: List<CartItem>) {
                cartItems.clear()
                cartItems.addAll(items)
                cartAdapter.notifyDataSetChanged()
                updateUI()
                calculateTotals()
            }
            
            override fun onError(error: String) {
                Toast.makeText(this@CartActivity, error, Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun updateUI() {
        if (cartItems.isEmpty()) {
            emptyCartState.visibility = View.VISIBLE
            cartItemsRecyclerView.visibility = View.GONE
            summaryCard.visibility = View.GONE
        } else {
            emptyCartState.visibility = View.GONE
            cartItemsRecyclerView.visibility = View.VISIBLE
            summaryCard.visibility = View.VISIBLE
        }
    }
    
    private fun calculateTotals() {
        subtotal = cartItems.sumOf { it.price * it.quantity }
        shipping = if (subtotal > 500) 0.0 else 70.0 // Free shipping over Rs 500
        
        val total = subtotal + shipping - discount
        
        subtotalText.text = "Rs ${String.format("%.0f", subtotal)}"
        shippingText.text = if (shipping == 0.0) "Free" else "Rs ${String.format("%.0f", shipping)}"
        totalText.text = "Rs ${String.format("%.0f", total)}"
        
        // Update shipping text color
        if (shipping == 0.0) {
            shippingText.setTextColor(resources.getColor(R.color.success_green, null))
        } else {
            shippingText.setTextColor(resources.getColor(R.color.text_dark, null))
        }
    }
    
    private fun updateCartItemQuantity(item: CartItem, newQuantity: Int) {
        cartManager.updateCartItem(item.productId, newQuantity) { success, message ->
            runOnUiThread {
                if (!success) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    // Reload cart to revert changes
                    loadCartItems()
                }
            }
        }
    }
    
    private fun deleteCartItem(item: CartItem) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove ${item.name} from your cart?")
            .setPositiveButton("Remove") { _, _ ->
                cartManager.removeFromCart(item.productId) { success, message ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun editCartItem(item: CartItem) {
        // Create a simple quantity edit dialog
        val editText = EditText(this)
        editText.setText(item.quantity.toString())
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Edit Quantity")
            .setMessage("Enter new quantity for ${item.name}:")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newQuantity = editText.text.toString().toIntOrNull()
                if (newQuantity != null && newQuantity > 0) {
                    updateCartItemQuantity(item, newQuantity)
                } else {
                    Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun applyPromoCode() {
        val promoCode = promoCodeEditText.text.toString().trim()
        
        if (promoCode.isEmpty()) {
            Toast.makeText(this, "Please enter a promo code", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Simple promo code validation (you can expand this)
        when (promoCode.uppercase()) {
            "PETPAL10" -> {
                discount = subtotal * 0.10 // 10% discount
                Toast.makeText(this, "✅ 10% discount applied!", Toast.LENGTH_SHORT).show()
                calculateTotals()
            }
            "FREESHIP" -> {
                shipping = 0.0
                Toast.makeText(this, "✅ Free shipping applied!", Toast.LENGTH_SHORT).show()
                calculateTotals()
            }
            "NEWUSER" -> {
                discount = 50.0 // Rs 50 off
                Toast.makeText(this, "✅ Rs 50 discount applied!", Toast.LENGTH_SHORT).show()
                calculateTotals()
            }
            else -> {
                Toast.makeText(this, "❌ Invalid promo code", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        
        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putExtra("subtotal", subtotal)
        intent.putExtra("shipping", shipping)
        intent.putExtra("discount", discount)
        intent.putExtra("total", subtotal + shipping - discount)
        startActivity(intent)
    }
}
