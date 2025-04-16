package com.example.tingofarm.data.repository

import android.util.Log
import com.example.tingofarm.data.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getProduceReport(): List<Report> {
        return try {
            firestore.collection("produceReports")
                .get()
                .await()
                .toObjects(Report::class.java)
        } catch (e: Exception) {
            Log.e("ReportsRepository", "Error fetching produce reports", e)
            emptyList()
        }
    }

    suspend fun getStockUsageReport(): List<Report> {
        return try {
            firestore.collection("stockReports")
                .get()
                .await()
                .toObjects(Report::class.java)
        } catch (e: Exception) {
            Log.e("ReportsRepository", "Error fetching stock usage reports", e)
            emptyList()
        }
    }
}