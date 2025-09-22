package com.example.finalapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class InterestSelectionActivity : ComponentActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sharedPreferences = getSharedPreferences("PetPalPrefs", Context.MODE_PRIVATE)
        
        setContent {
            InterestSelectionScreen(
                onContinue = { selectedItems ->
                    saveSelectedInterests(selectedItems)
                    // Navigate to next screen
                    // startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onBackPressed = { finish() },
                savedSelections = getSavedInterests()
            )
        }
    }
    
    private fun saveSelectedInterests(selectedItems: Set<String>) {
        sharedPreferences.edit()
            .putStringSet("selected_interests", selectedItems)
            .apply()
    }
    
    private fun getSavedInterests(): Set<String> {
        return sharedPreferences.getStringSet("selected_interests", emptySet()) ?: emptySet()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestSelectionScreen(
    onContinue: (Set<String>) -> Unit,
    onBackPressed: () -> Unit,
    savedSelections: Set<String>
) {
    val selectedItems = remember { mutableStateOf(savedSelections.toMutableSet()) }
    
    val interestItems = listOf(
        InterestItem("dog_food", "Dog Food", R.drawable.ic_paw_print),
        InterestItem("cat_food", "Cat Food", R.drawable.ic_paw_print),
        InterestItem("pet_toys", "Pet Toys", R.drawable.ic_paw_print),
        InterestItem("accessories", "Accessories", R.drawable.ic_paw_print),
        InterestItem("grooming", "Grooming", R.drawable.ic_grooming),
        InterestItem("pet_clothing", "Pet Clothing", R.drawable.ic_paw_print),
        InterestItem("training", "Training", R.drawable.ic_training),
        InterestItem("health", "Health", R.drawable.ic_medical)
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFCF8))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Select Your Interests",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Choose what you're interested in",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(interestItems) { item ->
                val isSelected = selectedItems.value.contains(item.id)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFFFEA500) else Color.White)
                        .clickable {
                            if (isSelected) selectedItems.value.remove(item.id)
                            else selectedItems.value.add(item.id)
                        }
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.name,
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = if (isSelected) Color.White else Color.Black
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onContinue(selectedItems.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            enabled = selectedItems.value.isNotEmpty()
        ) {
            Text("Continue", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InterestCard(
    item: InterestItem,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF4ECDC4) else Color.White
            )
            .clickable { onSelectionChanged(!isSelected) }
    ) {
        // Background gradient effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSelected) 
                        Color(0xFF4ECDC4).copy(alpha = 0.9f)
                    else 
                        Color(0xFFF8F8F8)
                )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                tint = if (isSelected) Color.White else Color(0xFF4ECDC4),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFF333333),
                textAlign = TextAlign.Center
            )
        }
        
        // Checkmark overlay
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4ECDC4).copy(alpha = 0.2f))
            )
            
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .background(
                        Color(0xFF4ECDC4),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp)
            )
        }
    }
}
