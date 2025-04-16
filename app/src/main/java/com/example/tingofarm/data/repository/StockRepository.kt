package com.example.tingofarm.data.repository

import android.util.Log
import com.example.tingofarm.data.model.StockItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getStockItems(): List<StockItem> {
        return try {
            firestore.collection("stock")
                .get()
                .await()
                .toObjects(StockItem::class.java)
        } catch (e: Exception) {
            Log.e("StockRepository", "Error fetching stock items", e)
            emptyList()
        }
    }

    suspend fun addStockItem(stock: StockItem) {
        try {
            val documentRef = firestore.collection("stock").add(stock).await()
            documentRef.update("id", documentRef.id).await()
            Log.d("StockRepository", "Stock added: $stock")
        } catch (e: Exception) {
            Log.e("StockRepository", "Error adding stock", e)
        }
    }

    suspend fun updateStockItem(id: String, updatedStock: StockItem) {
        try {
            firestore.collection("stock").document(id).set(updatedStock).await()
            Log.d("StockRepository", "Stock updated: $updatedStock")
        } catch (e: Exception) {
            Log.e("StockRepository", "Error updating stock", e)
        }
    }

    suspend fun deleteStockItem(id: String) {
        try {
            firestore.collection("stock").document(id).delete().await()
            Log.d("StockRepository", "Stock deleted: $id")
        } catch (e: Exception) {
            Log.e("StockRepository", "Error deleting stock", e)
        }
    }
}