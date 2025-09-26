package com.example.finalapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DaycareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daycare)

        // Back button functionality
        findViewById<ImageView>(R.id.back_button)?.setOnClickListener {
            finish()
        }

        // Placeholder for daycare functionality
        Toast.makeText(this, "Daycare - Coming Soon!", Toast.LENGTH_SHORT).show()
    }
}
