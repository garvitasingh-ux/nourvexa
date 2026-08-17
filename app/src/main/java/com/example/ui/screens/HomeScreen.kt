package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.DietPlan
import com.example.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    user: User,
    plan: DietPlan,
    onRecalculate: () -> Unit,
    paddingValues: PaddingValues
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val today = dateFormat.format(Date())

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                Text(
                    text = "Hello, ${user.name} 🌿",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Today's Plan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                QuantitiesCard(plan = plan)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Daily Timeline",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                TimelineItem("10:00 PM - 6:00 AM", "Sleep Window")
                TimelineItem("Morning - 12:00 PM", "Seasonal fruits (Total ${plan.fruitQuantityGrams}g)")
                TimelineItem("After 12:00 PM", "Lunch Salad (${plan.vegetableQuantityGrams}g veg + ${plan.greensQuantityGrams}g greens) + Home-cooked meal")
                TimelineItem("As needed", "Snack: Sprouts + Nuts (Total ${plan.snackQuantityGrams}g)")
                TimelineItem("By 6:00 PM", "Dinner completed (Same structure as lunch)")
                TimelineItem("After 8:00 PM", "Strict Cutoff - No Food")
                TimelineItem("Daily", "30+ mins Grass Walk + Sunlight exposure")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        com.example.model.ImageExportUtil.exportDietPlanAsImage(context, user, plan)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Download Plan as Image", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuantitiesCard(plan: DietPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            QuantityRow("Fruits", "${plan.fruitQuantityGrams} g")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            QuantityRow("Vegetables", "${plan.vegetableQuantityGrams} g")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            QuantityRow("Greens", "${plan.greensQuantityGrams} g")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            QuantityRow("Snacks", "${plan.snackQuantityGrams} g")
        }
    }
}

@Composable
fun QuantityRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimelineItem(time: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = time, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
