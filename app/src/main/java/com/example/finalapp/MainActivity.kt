package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Log an app_open event on main screen
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        // Get Started button to open onboarding flow
        val getStartedButton = findViewById<Button>(R.id.get_started_button)
        if (getStartedButton != null) {
            Log.d("MainActivity", "Get Started button found")
            getStartedButton.setOnClickListener {
                Log.d("MainActivity", "Get Started button clicked")
                FirebaseAnalytics.getInstance(this).logEvent("start_onboarding", null)
                try {
                    val intent = Intent(this, OnboardingActivity1::class.java)
                    Log.d("MainActivity", "Starting OnboardingActivity1")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting OnboardingActivity1", e)
                }
            }
        } else {
            Log.e("MainActivity", "Get Started button not found!")
        }
    }
}