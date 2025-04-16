package com.example.tingofarm.data.repository

import android.util.Log
import com.example.tingofarm.data.model.Livestock
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LivestockRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getLivestock(): List<Livestock> {
        return try {
            firestore.collection("livestock")
                .get()
                .await()
                .toObjects(Livestock::class.java)
        } catch (e: Exception) {
            Log.e("LivestockRepository", "Error fetching livestock", e)
            emptyList()
        }
    }

    // add new livestock to Firestore
    suspend fun addLivestock(livestock: Livestock) {
        try {
            val documentRef = firestore.collection("livestock").add(livestock).await()
            documentRef.update("id", documentRef.id).await()
            Log.d("LivestockRepository", "Livestock added: $livestock")
        } catch (e: Exception) {
            Log.e("LivestockRepository", "Error adding livestock")
        }
    }

    // update livestock
    suspend fun updateLivestock(id: String, updatedLivestock: Livestock) {
        try {
            firestore.collection("livestock").document(id).set(updatedLivestock).await()
            Log.d("LivestockRepository", "Livestock updated: $updatedLivestock")
        } catch (e: Exception) {
            Log.e("LivestockRepository", "Error updating livestock", e)
        }
    }

    // delete livestock
    suspend fun deleteLivestock(id: String) {
        try {
            firestore.collection("livestock").document(id).delete().await()
            Log.d("LivestockRepository", "Livestock deleted with ID: $id")
        } catch (e: Exception) {
            Log.e("LivestockRepository", "Error deleting livestock", e)
        }
    }
}