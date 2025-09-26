package com.example.finalapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GroomingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grooming)

        // Back button functionality
        findViewById<ImageView>(R.id.back_button)?.setOnClickListener {
            finish()
        }

        // Placeholder for grooming functionality
        Toast.makeText(this, "Grooming - Coming Soon!", Toast.LENGTH_SHORT).show()
    }
}
