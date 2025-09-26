package com.example.finalapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class PetDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://petpal-87c13-default-rtdb.firebaseio.com/")

        val spinnerPetType: Spinner = findViewById(R.id.spinnerPetType)
        val inputPetBreed: EditText = findViewById(R.id.inputPetBreed)
        val spinnerFeedingSchedule: Spinner = findViewById(R.id.spinnerFeedingSchedule)
        val textVaccinationDate: TextView = findViewById(R.id.textVaccinationDate)
        val inputLocation: EditText = findViewById(R.id.inputLocation)
        val btnContinue: Button = findViewById(R.id.btnContinue)
        val btnSkip: Button = findViewById(R.id.btnSkip)

        // Populate Pet Type Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.pet_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPetType.adapter = adapter
        }

        // Populate Feeding Schedule Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.feeding_schedules,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFeedingSchedule.adapter = adapter
        }

        // Date Picker Logic
        textVaccinationDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, {
                _, selectedYear, selectedMonth, selectedDay ->
                val date = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                textVaccinationDate.text = date
            }, year, month, day).show()
        }

        btnContinue.setOnClickListener {
            savePetDetails()
        }

        btnSkip.setOnClickListener {
            navigateToMain()
        }
    }

    private fun savePetDetails() {
        val petType = findViewById<Spinner>(R.id.spinnerPetType).selectedItem.toString()
        val petBreed = findViewById<EditText>(R.id.inputPetBreed).text.toString().trim()
        val feedingSchedule = findViewById<Spinner>(R.id.spinnerFeedingSchedule).selectedItem.toString()
        val vaccinationDate = findViewById<TextView>(R.id.textVaccinationDate).text.toString().trim()
        val location = findViewById<EditText>(R.id.inputLocation).text.toString().trim()

        if (petType == "Select pet type" || petBreed.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val petDetails = mapOf(
                "petType" to petType,
                "petBreed" to petBreed,
                "feedingSchedule" to feedingSchedule,
                "lastVaccinationDate" to vaccinationDate,
                "location" to location
            )

            database.reference.child("users").child(uid).child("petDetails").setValue(petDetails)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pet details saved!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save details: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "You must be logged in to save details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity() // Clears the back stack
    }
}


