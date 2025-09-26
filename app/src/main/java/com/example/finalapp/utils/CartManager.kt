package com.example.finalapp.utils

import com.example.finalapp.models.CartItem
import com.example.finalapp.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartManager {
    
    private val database = FirebaseDatabase.getInstance("https://petpal-87c13-default-rtdb.firebaseio.com/")
    private val auth = FirebaseAuth.getInstance()
    
    interface CartListener {
        fun onCartUpdated(items: List<CartItem>)
        fun onError(error: String)
    }
    
    interface OrderListener {
        fun onOrderPlaced(orderId: String)
        fun onError(error: String)
    }
    
    fun addToCart(item: CartItem, callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }
        
        val cartRef = database.reference.child("cart").child(userId).child("items")
        
        // Check if item already exists in cart
        cartRef.child(item.productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Update quantity
                    val existingItem = snapshot.getValue(CartItem::class.java)
                    val updatedItem = existingItem?.copy(quantity = existingItem.quantity + item.quantity)
                    cartRef.child(item.productId).setValue(updatedItem)
                        .addOnSuccessListener { callback(true, "Cart updated") }
                        .addOnFailureListener { callback(false, it.message ?: "Failed to update cart") }
                } else {
                    // Add new item
                    cartRef.child(item.productId).setValue(item)
                        .addOnSuccessListener { callback(true, "Item added to cart") }
                        .addOnFailureListener { callback(false, it.message ?: "Failed to add item") }
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message)
            }
        })
    }
    
    fun updateCartItem(productId: String, quantity: Int, callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }
        
        val itemRef = database.reference.child("cart").child(userId).child("items").child(productId)
        
        if (quantity <= 0) {
            removeFromCart(productId, callback)
            return
        }
        
        itemRef.child("quantity").setValue(quantity)
            .addOnSuccessListener { callback(true, "Quantity updated") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to update quantity") }
    }
    
    fun removeFromCart(productId: String, callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }
        
        database.reference.child("cart").child(userId).child("items").child(productId).removeValue()
            .addOnSuccessListener { callback(true, "Item removed from cart") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to remove item") }
    }
    
    fun getCartItems(listener: CartListener) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            listener.onError("User not logged in")
            return
        }
        
        database.reference.child("cart").child(userId).child("items")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<CartItem>()
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(CartItem::class.java)
                        item?.let { items.add(it) }
                    }
                    listener.onCartUpdated(items)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    listener.onError(error.message)
                }
            })
    }
    
    fun clearCart(callback: (Boolean, String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }
        
        database.reference.child("cart").child(userId).child("items").removeValue()
            .addOnSuccessListener { callback(true, "Cart cleared") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to clear cart") }
    }
    
    fun placeOrder(order: Order, listener: OrderListener) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            listener.onError("User not logged in")
            return
        }
        
        val ordersRef = database.reference.child("orders").child(userId)
        val orderId = ordersRef.push().key
        
        if (orderId == null) {
            listener.onError("Failed to generate order ID")
            return
        }
        
        val orderWithId = order.copy(id = orderId, userId = userId)
        
        ordersRef.child(orderId).setValue(orderWithId)
            .addOnSuccessListener {
                // Clear cart after successful order
                clearCart { success, _ ->
                    if (success) {
                        listener.onOrderPlaced(orderId)
                    } else {
                        listener.onOrderPlaced(orderId) // Still consider order placed even if cart clear fails
                    }
                }
            }
            .addOnFailureListener { 
                listener.onError(it.message ?: "Failed to place order")
            }
    }
    
    fun getCartItemCount(callback: (Int) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(0)
            return
        }
        
        database.reference.child("cart").child(userId).child("items")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalCount = 0
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(CartItem::class.java)
                        totalCount += item?.quantity ?: 0
                    }
                    callback(totalCount)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(0)
                }
            })
    }
}
