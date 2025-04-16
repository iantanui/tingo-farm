package com.example.tingofarm.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tingofarm.data.model.Produce
import com.example.tingofarm.data.repository.ProduceRepository
import com.example.tingofarm.viewmodel.ProduceViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProduceViewModelFactory(private val repository: ProduceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProduceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProduceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}