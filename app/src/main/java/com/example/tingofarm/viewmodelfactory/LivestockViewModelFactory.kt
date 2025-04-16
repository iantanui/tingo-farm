package com.example.tingofarm.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tingofarm.data.repository.LivestockRepository
import com.example.tingofarm.viewmodel.LivestockViewModel

class LivestockViewModelFactory(private val repository: LivestockRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LivestockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LivestockViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
