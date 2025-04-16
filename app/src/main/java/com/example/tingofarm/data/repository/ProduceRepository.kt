package com.example.tingofarm.data.repository

import android.util.Log
import com.example.tingofarm.data.model.Produce
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProduceRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getProduce(): List<Produce> {
        return try {
            val snapshot = firestore.collection("produce")
                .get()
                .await()
            val produceList = snapshot.toObjects(Produce::class.java)
            Log.d("ProduceRepository", "Fetched all produce: ${produceList.joinToString()}")
            produceList
        } catch (e: Exception) {
            Log.e("ProduceRepository", "Error fetching produce", e)
            emptyList()
        }
    }

    suspend fun getProduceByDate(date: String): List<Produce> {
        return try {
            val snapshot = firestore.collection("produce")
                .whereEqualTo("date", date)
                .get()
                .await()
            val produceList = snapshot.map { document ->
                document.toObject(Produce::class.java).copy(id = document.id)
            }
            Log.d("ProduceRepository", "Fetched produce for date $date: ${produceList.joinToString()}")
            produceList
        } catch (e: Exception) {
            Log.e("ProduceRepository", "Error fetching produce for date: $date", e)
            emptyList()
        }
    }

    suspend fun getProduceSinceDate(startDate: String): List<Produce> {
        return try {
            val parsedStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            Log.d("ProduceRepository", "Parsed start date: $parsedStartDate")

            // Fetch all produce and filter locally
            val snapshot = firestore.collection("produce")
                .get()
                .await()
            val allProduce = snapshot.map { document ->
                document.toObject(Produce::class.java).copy(id = document.id)
            }
            Log.d("ProduceRepository", "Fetched all produce for range query: ${allProduce.joinToString()}")

            val filteredProduce = allProduce.filter { produce ->
                val produceDate = LocalDate.parse(produce.date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                !produceDate.isBefore(parsedStartDate)
            }

            Log.d("ProduceRepository", "Filtered produce since $startDate: ${filteredProduce.joinToString()}")
            filteredProduce
        } catch (e: Exception) {
            Log.e("ProduceRepository", "Error fetching produce since $startDate", e)
            emptyList()
        }
    }

    suspend fun addProduce(produce: Produce) {
        try {
            val docRef = firestore.collection("produce").add(produce).await()
            Log.d("ProduceRepository", "Produce added: $produce with ID: ${docRef.id}")
        } catch (e: Exception) {
            Log.e("ProduceRepository", "Error adding produce: $produce", e)
        }
    }

    suspend fun updateProduce(produce: Produce) {
        try {
            if (produce.id.isBlank()) {
                throw IllegalArgumentException("Produce ID is blank, cannot update")
            }
            firestore.collection("produce")
                .document(produce.id)
                .set(produce)
                .await()
            Log.d("ProduceRepository", "Produce updated: $produce")
        } catch (e: Exception) {
            Log.e("ProduceRepository", "Error updating produce: $produce", e)
        }
    }

    suspend fun deleteProduce(produce: Produce) {
        try {
            if (produce.id.isBlank()) {
                throw IllegalArgumentException("Produce ID is blank, cannot delete")
            }
            firestore.collection("produce")
                .document(produce.id)
                .delete()
                .await()
            Log.d("ProduceRepository", "Produce deleted: $produce")
        } catch (e: Exception) {
            Log.e("ProduceRepository", "Error deleting produce: $produce", e)
        }
    }
}