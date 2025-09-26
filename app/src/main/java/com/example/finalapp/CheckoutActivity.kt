package com.example.finalapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.finalapp.models.CartItem
import com.example.finalapp.models.Order
import com.example.finalapp.models.ShippingAddress
import com.example.finalapp.utils.CartManager

class CheckoutActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageView
    private lateinit var changeAddressButton: TextView
    private lateinit var creditCardOption: CardView
    private lateinit var paypalOption: CardView
    private lateinit var creditCardRadio: RadioButton
    private lateinit var paypalRadio: RadioButton
    private lateinit var cardDetailsSection: LinearLayout
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private lateinit var cvvEditText: EditText
    private lateinit var cardHolderNameEditText: EditText
    private lateinit var standardDeliveryOption: CardView
    private lateinit var expressDeliveryOption: CardView
    private lateinit var standardDeliveryRadio: RadioButton
    private lateinit var expressDeliveryRadio: RadioButton
    private lateinit var checkoutSubtotal: TextView
    private lateinit var checkoutDiscount: TextView
    private lateinit var checkoutShipping: TextView
    private lateinit var checkoutTotal: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var cartManager: CartManager
    
    private var subtotal = 0.0
    private var shipping = 0.0
    private var discount = 0.0
    private var total = 0.0
    private var selectedPaymentMethod = "Credit/Debit Card"
    private var selectedDeliveryMethod = "Standard"
    private var expressDeliveryFee = 150.0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        
        initializeViews()
        setupClickListeners()
        loadOrderSummary()
        
        // Setup navigation bar
        NavigationHelper.setupNavigation(this, "shop")
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        changeAddressButton = findViewById(R.id.changeAddressButton)
        creditCardOption = findViewById(R.id.creditCardOption)
        paypalOption = findViewById(R.id.paypalOption)
        creditCardRadio = findViewById(R.id.creditCardRadio)
        paypalRadio = findViewById(R.id.paypalRadio)
        cardDetailsSection = findViewById(R.id.cardDetailsSection)
        cardNumberEditText = findViewById(R.id.cardNumberEditText)
        expiryDateEditText = findViewById(R.id.expiryDateEditText)
        cvvEditText = findViewById(R.id.cvvEditText)
        cardHolderNameEditText = findViewById(R.id.cardHolderNameEditText)
        standardDeliveryOption = findViewById(R.id.standardDeliveryOption)
        expressDeliveryOption = findViewById(R.id.expressDeliveryOption)
        standardDeliveryRadio = findViewById(R.id.standardDeliveryRadio)
        expressDeliveryRadio = findViewById(R.id.expressDeliveryRadio)
        checkoutSubtotal = findViewById(R.id.checkoutSubtotal)
        checkoutDiscount = findViewById(R.id.checkoutDiscount)
        checkoutShipping = findViewById(R.id.checkoutShipping)
        checkoutTotal = findViewById(R.id.checkoutTotal)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        cartManager = CartManager()
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        changeAddressButton.setOnClickListener {
            // TODO: Implement address change functionality
            Toast.makeText(this, "Address change feature coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        // Payment method selection
        creditCardOption.setOnClickListener {
            selectPaymentMethod("Credit/Debit Card")
        }
        
        paypalOption.setOnClickListener {
            selectPaymentMethod("PayPal")
        }
        
        creditCardRadio.setOnClickListener {
            selectPaymentMethod("Credit/Debit Card")
        }
        
        paypalRadio.setOnClickListener {
            selectPaymentMethod("PayPal")
        }
        
        // Delivery method selection
        standardDeliveryOption.setOnClickListener {
            selectDeliveryMethod("Standard")
        }
        
        expressDeliveryOption.setOnClickListener {
            selectDeliveryMethod("Express")
        }
        
        standardDeliveryRadio.setOnClickListener {
            selectDeliveryMethod("Standard")
        }
        
        expressDeliveryRadio.setOnClickListener {
            selectDeliveryMethod("Express")
        }
        
        placeOrderButton.setOnClickListener {
            placeOrder()
        }
    }
    
    private fun loadOrderSummary() {
        // Get data from intent
        subtotal = intent.getDoubleExtra("subtotal", 0.0)
        shipping = intent.getDoubleExtra("shipping", 0.0)
        discount = intent.getDoubleExtra("discount", 0.0)
        total = intent.getDoubleExtra("total", 0.0)
        
        updateOrderSummary()
    }
    
    private fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method
        
        when (method) {
            "Credit/Debit Card" -> {
                creditCardRadio.isChecked = true
                paypalRadio.isChecked = false
                cardDetailsSection.visibility = android.view.View.VISIBLE
            }
            "PayPal" -> {
                creditCardRadio.isChecked = false
                paypalRadio.isChecked = true
                cardDetailsSection.visibility = android.view.View.GONE
            }
        }
    }
    
    private fun selectDeliveryMethod(method: String) {
        selectedDeliveryMethod = method
        
        when (method) {
            "Standard" -> {
                standardDeliveryRadio.isChecked = true
                expressDeliveryRadio.isChecked = false
                // Remove express delivery fee if it was added
                if (shipping == expressDeliveryFee) {
                    shipping = if (subtotal > 500) 0.0 else 70.0
                }
            }
            "Express" -> {
                standardDeliveryRadio.isChecked = false
                expressDeliveryRadio.isChecked = true
                // Add express delivery fee
                shipping = expressDeliveryFee
            }
        }
        
        updateOrderSummary()
    }
    
    private fun updateOrderSummary() {
        total = subtotal + shipping - discount
        
        checkoutSubtotal.text = "Rs ${String.format("%.0f", subtotal)}"
        
        if (discount > 0) {
            checkoutDiscount.text = "-Rs ${String.format("%.0f", discount)}"
            checkoutDiscount.visibility = android.view.View.VISIBLE
        } else {
            checkoutDiscount.visibility = android.view.View.GONE
        }
        
        if (shipping == 0.0) {
            checkoutShipping.text = "Free"
            checkoutShipping.setTextColor(resources.getColor(R.color.success_green, null))
        } else {
            checkoutShipping.text = "Rs ${String.format("%.0f", shipping)}"
            checkoutShipping.setTextColor(resources.getColor(R.color.text_dark, null))
        }
        
        checkoutTotal.text = "Rs ${String.format("%.0f", total)}"
    }
    
    private fun placeOrder() {
        // Validate payment information
        if (selectedPaymentMethod == "Credit/Debit Card") {
            if (!validateCardDetails()) {
                return
            }
        }
        
        // Show loading
        placeOrderButton.isEnabled = false
        placeOrderButton.text = "Processing..."
        
        // Get cart items and create order
        cartManager.getCartItems(object : CartManager.CartListener {
            override fun onCartUpdated(items: List<CartItem>) {
                val shippingAddress = ShippingAddress(
                    name = "Janithya Pinto",
                    address = "123 Pet Street, Apt 4B",
                    city = "New York",
                    state = "NY",
                    zipCode = "10001",
                    phone = "(555) 123-4567"
                )
                
                val order = Order(
                    items = items,
                    subtotal = subtotal,
                    shipping = shipping,
                    total = total,
                    shippingAddress = shippingAddress,
                    paymentMethod = selectedPaymentMethod
                )
                
                cartManager.placeOrder(order, object : CartManager.OrderListener {
                    override fun onOrderPlaced(orderId: String) {
                        runOnUiThread {
                            showOrderSuccess(orderId)
                        }
                    }
                    
                    override fun onError(error: String) {
                        runOnUiThread {
                            placeOrderButton.isEnabled = true
                            placeOrderButton.text = "Place Order"
                            Toast.makeText(this@CheckoutActivity, "Order failed: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
            
            override fun onError(error: String) {
                runOnUiThread {
                    placeOrderButton.isEnabled = true
                    placeOrderButton.text = "Place Order"
                    Toast.makeText(this@CheckoutActivity, "Failed to load cart: $error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    
    private fun validateCardDetails(): Boolean {
        val cardNumber = cardNumberEditText.text.toString().trim()
        val expiryDate = expiryDateEditText.text.toString().trim()
        val cvv = cvvEditText.text.toString().trim()
        val cardHolderName = cardHolderNameEditText.text.toString().trim()
        
        when {
            cardNumber.isEmpty() || cardNumber.length < 16 -> {
                cardNumberEditText.error = "Please enter a valid card number"
                cardNumberEditText.requestFocus()
                return false
            }
            expiryDate.isEmpty() || !expiryDate.matches(Regex("\\d{2}/\\d{2}")) -> {
                expiryDateEditText.error = "Please enter expiry date (MM/YY)"
                expiryDateEditText.requestFocus()
                return false
            }
            cvv.isEmpty() || cvv.length < 3 -> {
                cvvEditText.error = "Please enter a valid CVV"
                cvvEditText.requestFocus()
                return false
            }
            cardHolderName.isEmpty() -> {
                cardHolderNameEditText.error = "Please enter card holder name"
                cardHolderNameEditText.requestFocus()
                return false
            }
        }
        
        return true
    }
    
    private fun showOrderSuccess(orderId: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("🎉 Order Placed Successfully!")
            .setMessage("Your order #$orderId has been placed successfully.\n\nYou will receive a confirmation email shortly.\n\nThank you for shopping with PetPal!")
            .setPositiveButton("Continue Shopping") { _, _ ->
                finish()
                startActivity(android.content.Intent(this, ShopActivity::class.java))
            }
            .setNegativeButton("Go to Dashboard") { _, _ ->
                finish()
                startActivity(android.content.Intent(this, DashboardActivity::class.java))
            }
            .setCancelable(false)
            .show()
    }
}
