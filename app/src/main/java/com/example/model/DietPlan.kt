package com.example.model

data class DietPlan(
    val userId: String = "",
    val generatedAt: Long = System.currentTimeMillis(),
    val weightSnapshot: Float = 0f,
    val fruitQuantityGrams: Int = 0,
    val vegetableQuantityGrams: Int = 0,
    val greensQuantityGrams: Int = 0,
    val snackQuantityGrams: Int = 0
)
