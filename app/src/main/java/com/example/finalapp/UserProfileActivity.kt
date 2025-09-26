package com.example.finalapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

data class PetData(
    val id: String = "",
    val petType: String = "",
    val petBreed: String = "",
    val feedingSchedule: String = "",
    val lastVaccinationDate: String = "",
    val location: String = ""
)

class UserProfileActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var petAdapter: PetAdapter
    private val petList = mutableListOf<PetData>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://petpal-87c13-default-rtdb.firebaseio.com/").reference

        // Back button functionality
        findViewById<ImageView>(R.id.back_button)?.setOnClickListener {
            finish()
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPets)
        petAdapter = PetAdapter(petList) { pet, action ->
            when (action) {
                "edit" -> showEditPetDialog(pet)
                "delete" -> deletePet(pet)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = petAdapter

        // Add new pet button
        findViewById<Button>(R.id.btnAddPet).setOnClickListener {
            showAddPetDialog()
        }

        // Load pet data
        loadPetData()
        
        // Setup navigation bar
        NavigationHelper.setupNavigation(this, "profile")
    }

    private fun loadPetData() {
        val userId = auth.currentUser?.uid ?: return
        
        database.child("users").child(userId).child("petDetails")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    petList.clear()
                    
                    if (snapshot.exists()) {
                        // Handle single pet data (legacy format)
                        if (snapshot.hasChild("petType")) {
                            val pet = PetData(
                                id = "main",
                                petType = snapshot.child("petType").getValue(String::class.java) ?: "",
                                petBreed = snapshot.child("petBreed").getValue(String::class.java) ?: "",
                                feedingSchedule = snapshot.child("feedingSchedule").getValue(String::class.java) ?: "",
                                lastVaccinationDate = snapshot.child("lastVaccinationDate").getValue(String::class.java) ?: "",
                                location = snapshot.child("location").getValue(String::class.java) ?: ""
                            )
                            petList.add(pet)
                        } else {
                            // Handle multiple pets
                            for (petSnapshot in snapshot.children) {
                                val pet = petSnapshot.getValue(PetData::class.java)
                                pet?.let { 
                                    petList.add(it.copy(id = petSnapshot.key ?: ""))
                                }
                            }
                        }
                    }
                    
                    petAdapter.notifyDataSetChanged()
                    updateEmptyState()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UserProfileActivity, "Failed to load pet data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateEmptyState() {
        val emptyState = findViewById<LinearLayout>(R.id.emptyState)
        if (petList.isEmpty()) {
            emptyState.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
        } else {
            emptyState.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun showAddPetDialog() {
        showPetDialog(null)
    }

    private fun showEditPetDialog(pet: PetData) {
        showPetDialog(pet)
    }

    private fun showPetDialog(existingPet: PetData?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pet_details, null)
        
        val spinnerPetType = dialogView.findViewById<Spinner>(R.id.spinnerPetType)
        val inputPetBreed = dialogView.findViewById<EditText>(R.id.inputPetBreed)
        val spinnerFeedingSchedule = dialogView.findViewById<Spinner>(R.id.spinnerFeedingSchedule)
        val textVaccinationDate = dialogView.findViewById<TextView>(R.id.textVaccinationDate)
        val inputLocation = dialogView.findViewById<EditText>(R.id.inputLocation)

        // Setup spinners
        ArrayAdapter.createFromResource(this, R.array.pet_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPetType.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.feeding_schedules, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFeedingSchedule.adapter = adapter
        }

        // Pre-fill data if editing
        existingPet?.let { pet ->
            val petTypeAdapter = spinnerPetType.adapter as ArrayAdapter<String>
            val petTypePosition = petTypeAdapter.getPosition(pet.petType)
            if (petTypePosition >= 0) spinnerPetType.setSelection(petTypePosition)

            inputPetBreed.setText(pet.petBreed)

            val feedingAdapter = spinnerFeedingSchedule.adapter as ArrayAdapter<String>
            val feedingPosition = feedingAdapter.getPosition(pet.feedingSchedule)
            if (feedingPosition >= 0) spinnerFeedingSchedule.setSelection(feedingPosition)

            textVaccinationDate.text = pet.lastVaccinationDate
            inputLocation.setText(pet.location)
        }

        // Date picker for vaccination date
        textVaccinationDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                textVaccinationDate.text = "${month + 1}/$day/$year"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existingPet == null) "Add New Pet" else "Edit Pet Details")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                savePetData(existingPet, spinnerPetType, inputPetBreed, spinnerFeedingSchedule, textVaccinationDate, inputLocation)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun savePetData(
        existingPet: PetData?,
        spinnerPetType: Spinner,
        inputPetBreed: EditText,
        spinnerFeedingSchedule: Spinner,
        textVaccinationDate: TextView,
        inputLocation: EditText
    ) {
        val petType = spinnerPetType.selectedItem.toString()
        val petBreed = inputPetBreed.text.toString().trim()
        val feedingSchedule = spinnerFeedingSchedule.selectedItem.toString()
        val vaccinationDate = textVaccinationDate.text.toString().trim()
        val location = inputLocation.text.toString().trim()

        if (petType == "Select pet type" || petBreed.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        
        val petData = PetData(
            id = existingPet?.id ?: "",
            petType = petType,
            petBreed = petBreed,
            feedingSchedule = feedingSchedule,
            lastVaccinationDate = vaccinationDate,
            location = location
        )

        val petRef = if (existingPet != null) {
            database.child("users").child(userId).child("petDetails").child(existingPet.id)
        } else {
            database.child("users").child(userId).child("petDetails").push()
        }

        petRef.setValue(petData)
            .addOnSuccessListener {
                Toast.makeText(this, "Pet details saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save pet details: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePet(pet: PetData) {
        AlertDialog.Builder(this)
            .setTitle("Delete Pet")
            .setMessage("Are you sure you want to delete ${pet.petBreed}?")
            .setPositiveButton("Delete") { _, _ ->
                val userId = auth.currentUser?.uid ?: return@setPositiveButton
                
                database.child("users").child(userId).child("petDetails").child(pet.id)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Pet deleted successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete pet: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

class PetAdapter(
    private val petList: List<PetData>,
    private val onItemAction: (PetData, String) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    class PetViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val petName: TextView = itemView.findViewById(R.id.textPetName)
        val petType: TextView = itemView.findViewById(R.id.textPetType)
        val petBreed: TextView = itemView.findViewById(R.id.textPetBreed)
        val feedingSchedule: TextView = itemView.findViewById(R.id.textFeedingSchedule)
        val vaccinationDate: TextView = itemView.findViewById(R.id.textVaccinationDate)
        val location: TextView = itemView.findViewById(R.id.textLocation)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petList[position]
        
        holder.petName.text = pet.petBreed
        holder.petType.text = "Type: ${pet.petType}"
        holder.petBreed.text = "Breed: ${pet.petBreed}"
        holder.feedingSchedule.text = "Feeding: ${pet.feedingSchedule}"
        holder.vaccinationDate.text = "Last Vaccination: ${pet.lastVaccinationDate}"
        holder.location.text = "Location: ${pet.location}"

        holder.btnEdit.setOnClickListener {
            onItemAction(pet, "edit")
        }

        holder.btnDelete.setOnClickListener {
            onItemAction(pet, "delete")
        }
    }

    override fun getItemCount(): Int = petList.size
}
