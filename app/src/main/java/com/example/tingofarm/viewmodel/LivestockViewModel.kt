package com.example.tingofarm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tingofarm.data.model.Livestock
import com.example.tingofarm.data.repository.LivestockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LivestockViewModel(private val repository: LivestockRepository) : ViewModel() {

    private val _livestock = MutableStateFlow<List<Livestock>>(emptyList())
    val livestock: StateFlow<List<Livestock>> = _livestock.asStateFlow()

    val totalLivestock: StateFlow<Int> = _livestock.map { it.size }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0
    )

    val healthyLivestock: StateFlow<Int> = _livestock.map { list ->
        list.count { it.healthStatus == "Healthy" }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0
    )

    val unhealthyLivestock: StateFlow<Int> = _livestock.map { list ->
        list.count { it.healthStatus == "Under Treatment" }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0
    )

    init {
        fetchLivestock()
    }

    // fetch livestock
    private fun fetchLivestock() {
        viewModelScope.launch {
            _livestock.value = repository.getLivestock()
            Log.d("LivestockViewModel", "Fetched livestock: ${_livestock.value}")
        }
    }

    // Method to add livestock
    fun addLivestock(livestock: Livestock) {
        viewModelScope.launch {
            repository.addLivestock(livestock)
            fetchLivestock()
        }
    }

    // Update livestock
    fun updateLivestock(id: String, updatedLivestock: Livestock) {
        viewModelScope.launch {
            repository.updateLivestock(id, updatedLivestock)
            fetchLivestock()
        }
    }

    // Delete livestock
    fun deleteLivestock(id: String) {
        viewModelScope.launch {
            repository.deleteLivestock(id)
            fetchLivestock()
        }
    }
}
