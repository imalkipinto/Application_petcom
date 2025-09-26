package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics

class OnboardingActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("OnboardingActivity3", "onCreate called")
        setContentView(R.layout.activity_onboarding_3)
        Log.d("OnboardingActivity3", "Layout set successfully")

        // Log onboarding screen view
        FirebaseAnalytics.getInstance(this).logEvent("onboarding_3_view", null)

        // Skip button functionality
        val skipButton = findViewById<TextView>(R.id.skip_button)
        if (skipButton != null) {
            Log.d("OnboardingActivity3", "Skip button found")
            skipButton.setOnClickListener {
                Log.d("OnboardingActivity3", "Skip button clicked")
                FirebaseAnalytics.getInstance(this).logEvent("onboarding_skip", null)
                navigateToInterestsSelection()
            }
        } else {
            Log.e("OnboardingActivity3", "Skip button not found!")
        }

        // Get Started button functionality
        val getStartedButton = findViewById<Button>(R.id.get_started_button)
        if (getStartedButton != null) {
            Log.d("OnboardingActivity3", "Get Started button found")
            getStartedButton.setOnClickListener {
                Log.d("OnboardingActivity3", "Get Started button clicked")
                FirebaseAnalytics.getInstance(this).logEvent("onboarding_3_get_started", null)
                navigateToInterestsSelection()
            }
        } else {
            Log.e("OnboardingActivity3", "Get Started button not found!")
        }
    }

    private fun navigateToInterestsSelection() {
        startActivity(Intent(this, InterestsSelectionActivity::class.java))
        finish()
    }
}
