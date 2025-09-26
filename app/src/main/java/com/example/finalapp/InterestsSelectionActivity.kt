package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InterestsSelectionActivity : AppCompatActivity() {

    private val selectedInterests = mutableSetOf<String>()
    private lateinit var continueButton: Button
    private lateinit var database: DatabaseReference
    private val tickViews = mutableMapOf<String, ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_selection)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance("https://petpal-87c13-default-rtdb.firebaseio.com/").reference

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        continueButton = findViewById(R.id.continue_button)
        
        // Initialize tick views map
        tickViews["dog_food"] = findViewById(R.id.dog_food_tick)
        tickViews["cat_food"] = findViewById(R.id.cat_food_tick)
        tickViews["pet_toys"] = findViewById(R.id.pet_toys_tick)
        tickViews["accessories"] = findViewById(R.id.accessories_tick)
        tickViews["grooming"] = findViewById(R.id.grooming_tick)
        tickViews["pet_clothing"] = findViewById(R.id.pet_clothing_tick)
        tickViews["training"] = findViewById(R.id.training_tick)
        tickViews["health"] = findViewById(R.id.health_tick)
        
        // Initially disable continue button until at least one interest is selected
        continueButton.isEnabled = false
        continueButton.alpha = 0.5f
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        // Interest cards
        setupInterestCard(R.id.dog_food_card, "dog_food")
        setupInterestCard(R.id.cat_food_card, "cat_food")
        setupInterestCard(R.id.pet_toys_card, "pet_toys")
        setupInterestCard(R.id.accessories_card, "accessories")
        setupInterestCard(R.id.grooming_card, "grooming")
        setupInterestCard(R.id.pet_clothing_card, "pet_clothing")
        setupInterestCard(R.id.training_card, "training")
        setupInterestCard(R.id.health_card, "health")

        // Continue button
        continueButton.setOnClickListener {
            proceedToMainApp()
        }
    }

    private fun setupInterestCard(cardId: Int, interestKey: String) {
        val card = findViewById<FrameLayout>(cardId)
        
        card.setOnClickListener {
            toggleInterestSelection(card, interestKey)
        }
    }

    private fun toggleInterestSelection(card: FrameLayout, interestKey: String) {
        val tickView = tickViews[interestKey]
        
        if (selectedInterests.contains(interestKey)) {
            // Deselect
            selectedInterests.remove(interestKey)
            card.alpha = 1.0f
            card.elevation = 0f
            tickView?.visibility = android.view.View.GONE
        } else {
            // Select
            selectedInterests.add(interestKey)
            card.alpha = 0.8f
            card.elevation = 8f
            tickView?.visibility = android.view.View.VISIBLE
        }
        
        updateContinueButton()
    }

    private fun updateContinueButton() {
        if (selectedInterests.isNotEmpty()) {
            continueButton.isEnabled = true
            continueButton.alpha = 1.0f
        } else {
            continueButton.isEnabled = false
            continueButton.alpha = 0.5f
        }
    }

    private fun proceedToMainApp() {
        // Save selected interests to Firebase and SharedPreferences
        saveSelectedInterestsToFirebase()
        saveSelectedInterests()
        
        // Navigate to login screen after interests selection
        val intent = Intent(this, LoginActivity::class.java)
        intent.putStringArrayListExtra("selected_interests", ArrayList(selectedInterests))
        startActivity(intent)
        finish()
    }

    private fun saveSelectedInterestsToFirebase() {
        // Generate a unique user ID or use existing one
        val userId = getSharedPreferences("PetPalPrefs", MODE_PRIVATE)
            .getString("user_id", null) ?: generateUserId()
        
        // Save user ID for future use
        getSharedPreferences("PetPalPrefs", MODE_PRIVATE)
            .edit()
            .putString("user_id", userId)
            .apply()
        
        // Create user interests data
        val userInterests = mapOf(
            "interests" to selectedInterests.toList(),
            "timestamp" to System.currentTimeMillis(),
            "interestCount" to selectedInterests.size
        )
        
        // Save to Firebase
        database.child("users").child(userId).child("interests").setValue(userInterests)
            .addOnSuccessListener {
                Toast.makeText(this, "Interests saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save interests: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun generateUserId(): String {
        return "user_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun saveSelectedInterests() {
        val sharedPreferences = getSharedPreferences("PetPalPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("user_interests", selectedInterests)
        editor.putBoolean("interests_selected", true)
        editor.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // You might want to go back to the last onboarding screen instead
        finish()
    }
}
