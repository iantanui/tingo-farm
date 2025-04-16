package com.example.tingofarm.data.repository

import com.example.tingofarm.data.network.ApiService
import com.example.tingofarm.data.network.RetrofitInstance
import com.example.tingofarm.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthenticationRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}