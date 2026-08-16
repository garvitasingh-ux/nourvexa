package com.example.model

object DietEngine {
    fun generatePlan(userId: String, weightKg: Float): DietPlan {
        return DietPlan(
            userId = userId,
            weightSnapshot = weightKg,
            fruitQuantityGrams = (weightKg * 10).toInt(),
            vegetableQuantityGrams = (weightKg * 5).toInt(),
            greensQuantityGrams = weightKg.toInt(),
            snackQuantityGrams = weightKg.toInt()
        )
    }
}
