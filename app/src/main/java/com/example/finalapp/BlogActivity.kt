package com.example.finalapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BlogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        // Back button functionality
        findViewById<ImageView>(R.id.back_button)?.setOnClickListener {
            finish()
        }

        // Placeholder for blog functionality
        Toast.makeText(this, "Blog - Coming Soon!", Toast.LENGTH_SHORT).show()
        
        // Setup navigation bar
        NavigationHelper.setupNavigation(this, "blog")
    }
}
