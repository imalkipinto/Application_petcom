package com.example.finalapp.models

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val brand: String = "",
    val inStock: Boolean = true,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
)

data class CartItem(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val quantity: Int = 1
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending",
    val shippingAddress: ShippingAddress? = null,
    val paymentMethod: String = ""
)

data class ShippingAddress(
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val phone: String = ""
)
