package com.example.tingofarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tingofarm.data.model.Report
import com.example.tingofarm.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository,
) : ViewModel() {

    private val _produceReport = MutableStateFlow<List<Report>>(emptyList())
    val produceReport: StateFlow<List<Report>> get() = _produceReport

    private val _stockUsageReport = MutableStateFlow<List<Report>>(emptyList())
    val stockUsageReport: StateFlow<List<Report>> get() = _stockUsageReport

    init {
        fetchReports()
    }

    private fun fetchReports() {
        viewModelScope.launch {
            _produceReport.value = repository.getProduceReport()
            _stockUsageReport.value = repository.getStockUsageReport()
        }
    }
}