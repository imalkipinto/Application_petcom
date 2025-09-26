package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics

class OnboardingActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("OnboardingActivity1", "onCreate called")
        setContentView(R.layout.activity_onboarding_1)
        Log.d("OnboardingActivity1", "Layout set successfully")

        // Log onboarding screen view
        FirebaseAnalytics.getInstance(this).logEvent("onboarding_1_view", null)

        // Skip button functionality
        val skipButton = findViewById<TextView>(R.id.skip_button)
        if (skipButton != null) {
            Log.d("OnboardingActivity1", "Skip button found")
            skipButton.setOnClickListener {
                Log.d("OnboardingActivity1", "Skip button clicked")
                FirebaseAnalytics.getInstance(this).logEvent("onboarding_skip", null)
                navigateToInterestsSelection()
            }
        } else {
            Log.e("OnboardingActivity1", "Skip button not found!")
        }

        // Next button functionality
        val nextButton = findViewById<Button>(R.id.next_button)
        if (nextButton != null) {
            Log.d("OnboardingActivity1", "Next button found")
            nextButton.setOnClickListener {
                Log.d("OnboardingActivity1", "Next button clicked")
                FirebaseAnalytics.getInstance(this).logEvent("onboarding_1_next", null)
                try {
                    val intent = Intent(this, OnboardingActivity2::class.java)
                    Log.d("OnboardingActivity1", "Starting OnboardingActivity2")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("OnboardingActivity1", "Error starting OnboardingActivity2", e)
                }
            }
        } else {
            Log.e("OnboardingActivity1", "Next button not found!")
        }
    }

    private fun navigateToInterestsSelection() {
        startActivity(Intent(this, InterestsSelectionActivity::class.java))
        finish()
    }
}
