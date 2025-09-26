package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        setupClickListeners()
        
        // Setup navigation bar
        NavigationHelper.setupNavigation(this, "home")
    }

    private fun setupClickListeners() {
        // Profile icon click
        findViewById<ImageView>(R.id.profile_icon)?.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        // Quick Actions navigation
        findViewById<LinearLayout>(R.id.shop_action)?.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.vet_action)?.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.trainers_action)?.setOnClickListener {
            startActivity(Intent(this, TrainersActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.daycare_action)?.setOnClickListener {
            startActivity(Intent(this, DaycareActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.grooming_action)?.setOnClickListener {
            startActivity(Intent(this, GroomingActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.chatbot_action)?.setOnClickListener {
            startActivity(Intent(this, BlogActivity::class.java))
        }

        // Reorder button click
        findViewById<Button>(R.id.reorder_button)?.setOnClickListener {
            startActivity(Intent(this, PurchaseItemActivity::class.java))
        }

        // Book service buttons
        findViewById<Button>(R.id.book_vet_button)?.setOnClickListener {
            startActivity(Intent(this, BookServiceActivity::class.java))
        }

        findViewById<Button>(R.id.book_grooming_button)?.setOnClickListener {
            startActivity(Intent(this, BookServiceActivity::class.java))
        }
    }
}
