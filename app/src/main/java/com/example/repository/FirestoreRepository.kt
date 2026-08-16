package com.example.repository

import com.example.model.DietPlan
import com.example.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    // Catch initialization errors if google-services.json is missing
    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        null
    }

    suspend fun saveUser(user: User): Boolean {
        return try {
            firestore?.collection("users")?.document(user.id)?.set(user)?.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val doc = firestore?.collection("users")?.document(userId)?.get()?.await()
            doc?.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveDietPlan(plan: DietPlan): Boolean {
        return try {
            firestore?.collection("dietPlans")?.document(plan.userId)?.set(plan)?.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getDietPlan(userId: String): DietPlan? {
        return try {
            val doc = firestore?.collection("dietPlans")?.document(userId)?.get()?.await()
            doc?.toObject(DietPlan::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
