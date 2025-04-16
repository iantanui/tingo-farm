package com.example.tingofarm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tingofarm.data.model.Produce
import com.example.tingofarm.data.repository.ProduceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ProduceViewModel(private val repository: ProduceRepository) : ViewModel() {

    private val _produceList = MutableStateFlow<List<Produce>>(emptyList())
    val produceList: StateFlow<List<Produce>> = _produceList.asStateFlow()

    private val _timeRangeProduceList = MutableStateFlow<List<Produce>>(emptyList())
    val timeRangeProduceList: StateFlow<List<Produce>> = _timeRangeProduceList.asStateFlow()

    private var currentDate: String = getCurrentDate()

    val dailyTotalProduce: StateFlow<Double> = _produceList.map { list ->
        list.sumOf { it.morningQty + it.eveningQty }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0.0
    )

    init {
        fetchProduceByDate(currentDate)
        fetchTimeRangeProduce()

        viewModelScope.launch {
            delay(1000)
            if (_timeRangeProduceList.value.isEmpty()) {
                fetchTimeRangeProduce()
            }
        }
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    fun setDate(date: Long) {
        val dateString = convertMillisToDate(date)
        if (dateString != currentDate) {
            currentDate = dateString
            fetchProduceByDate(currentDate)
        }
    }

    private fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    private fun fetchProduceByDate(date: String) {
        viewModelScope.launch {
            try {
                val produce = repository.getProduceByDate(date)
                _produceList.value = produce
                // Sync with timeRangeProduceList
                val currentTimeRange = _timeRangeProduceList.value.toMutableList()
                val updatedList = currentTimeRange.filter { it.date != date } + produce
                _timeRangeProduceList.value = updatedList
                Log.d("ProduceViewModel", "Fetched produce for date $date: ${produce.joinToString()}")
                Log.d("ProduceViewModel", "Updated timeRangeProduceList: ${updatedList.joinToString()}")
            } catch (e: Exception) {
                Log.e("ProduceViewModel", "Error fetching produce for $date: $e")
            }
        }
    }

    fun fetchTimeRangeProduce() {
        viewModelScope.launch {
            Log.d("ProduceViewModel", "Starting fetchTimeRangeProduce")
            try {
                val today = LocalDate.now()
                val startOfRange = today.minusYears(1)
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val startDateStr = startOfRange.format(formatter)
                Log.d("ProduceViewModel", "Fetching produce since $startDateStr")
                val allProduce = repository.getProduceSinceDate(startDateStr)
                Log.d("ProduceViewModel", "Raw fetch result: ${allProduce.joinToString()}")
                _timeRangeProduceList.value = allProduce
                Log.d(
                    "ProduceViewModel",
                    "Fetched time range produce from $startDateStr: ${allProduce.size} items, data: ${allProduce.joinToString()}"
                )
                if (allProduce.isEmpty()) {
                    Log.w("ProduceViewModel", "No data fetched from Firestore for range starting $startDateStr")
                } else if (allProduce.any { it.date == "26-03-2025" }) {
                    Log.d("ProduceViewModel", "Confirmed 26-03-2025 in fetch: ${allProduce.filter { it.date == "26-03-2025" }.joinToString()}")
                }
            } catch (e: Exception) {
                Log.e("ProduceViewModel", "Error fetching time range produce: $e")
            }
        }
    }

    fun addProduce(produce: Produce) {
        viewModelScope.launch {
            try {
                repository.addProduce(produce)
                Log.d("ProduceViewModel", "Added produce: $produce")
                if (produce.date == currentDate) {
                    _produceList.value = _produceList.value + produce
                }
                val updatedList = _timeRangeProduceList.value + produce
                _timeRangeProduceList.value = updatedList
                Log.d("ProduceViewModel", "Updated timeRangeProduceList: ${updatedList.joinToString()}")
                val fetched = repository.getProduceByDate(produce.date)
                Log.d("ProduceViewModel", "Verified fetch add for ${produce.date}: ${fetched.joinToString()}")
            } catch (e: Exception) {
                Log.e("ProduceViewModel", "Error adding produce: $e")
            }
        }
    }

    fun updateProduce(produce: Produce) {
        viewModelScope.launch {
            try {
                repository.updateProduce(produce)
                Log.d("ProduceViewModel", "Updated produce: $produce")
                fetchProduceByDate(produce.date)
                fetchTimeRangeProduce()
            } catch (e: Exception) {
                Log.e("ProduceViewModel", "Error updating produce: $e")
            }
        }
    }

    fun deleteProduce(produce: Produce) {
        viewModelScope.launch {
            try {
                repository.deleteProduce(produce)
                Log.d("ProduceViewModel", "Deleted produce: $produce")
                fetchProduceByDate(produce.date)
                fetchTimeRangeProduce()
            } catch (e: Exception) {
                Log.e("ProduceViewModel", "Error deleting produce: $e")
            }
        }
    }

    fun getTotalProduceByDate(timeRange: String): StateFlow<List<Pair<String, Double>>> {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDate = when (timeRange) {
            "Weekly" -> today.with(DayOfWeek.MONDAY)
            "Monthly" -> today.withDayOfMonth(1)
            "6 Months" -> today.minusMonths(5).withDayOfMonth(1)
            "Yearly" -> today.minusMonths(11).withDayOfMonth(1)
            else -> today.with(DayOfWeek.MONDAY)
        }
        val endDate = today

        return _timeRangeProduceList.map { list ->
            Log.d("ProduceViewModel", "Raw timeRangeProduceList: ${list.joinToString()}")
            val filteredList = list.filter {
                val produceDate = try {
                    LocalDate.parse(it.date, formatter)
                } catch (e: Exception) {
                    Log.e("ProduceViewModel", "Failed to parse date: ${it.date}, error: $e")
                    null
                }
                produceDate != null && !produceDate.isBefore(startDate) && !produceDate.isAfter(endDate)
            }
            Log.d("ProduceViewModel", "Filtered list for $timeRange ($startDate to $endDate): ${filteredList.joinToString()}")
            val result: List<Pair<String, Double>> = when (timeRange) {
                "Weekly" -> {
                    val dailyTotals = mutableMapOf<DayOfWeek, Double>().apply {
                        DayOfWeek.values().forEach { this[it] = 0.0 }
                    }
                    filteredList.forEach { produce ->
                        val day = LocalDate.parse(produce.date, formatter).dayOfWeek
                        dailyTotals[day] = dailyTotals[day]!! + (produce.morningQty + produce.eveningQty)
                    }
                    DayOfWeek.values().map {
                        it.getDisplayName(
                            java.time.format.TextStyle.FULL,
                            Locale.getDefault()
                        ) to (dailyTotals[it] ?: 0.0)
                    }
                }
                else -> {
                    val monthlyTotals = mutableMapOf<String, Double>()
                    filteredList.groupBy {
                        LocalDate.parse(it.date, formatter)
                            .format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault()))
                    }.forEach { (month, produces) ->
                        monthlyTotals[month] = produces.sumOf { it.morningQty + it.eveningQty }
                    }
                    val monthFormatter = DateTimeFormatter.ofPattern("MMM yy", Locale.getDefault())
                    val months = generateSequence(startDate) { it.plusMonths(1) }
                        .takeWhile { !it.isAfter(endDate) }
                        .map { it.format(monthFormatter) }
                        .distinct()
                        .toList()
                    Log.d("ProduceViewModel", "Months for $timeRange: $months")
                    months.map { month -> month to (monthlyTotals[month.substring(0, 3)] ?: 0.0) }
                }
            }
            result
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getMilkByCow(timeRange: String): StateFlow<List<Pair<String, Double>>> {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDate = when (timeRange) {
            "Weekly" -> today.with(DayOfWeek.MONDAY)
            "Monthly" -> today.withDayOfMonth(1)
            "6 Months" -> today.minusMonths(5).withDayOfMonth(1)
            "Yearly" -> today.minusMonths(11).withDayOfMonth(1)
            else -> today.with(DayOfWeek.MONDAY)
        }
        val endDate = today

        return _timeRangeProduceList.map { list ->
            Log.d("ProduceViewModel", "Raw timeRangeProduceList for milk by cow: ${list.joinToString()}")
            val filteredList = list.filter {
                val produceDate = try {
                    LocalDate.parse(it.date, formatter)
                } catch (e: Exception) {
                    Log.e("ProduceViewModel", "Failed to parse date: ${it.date}, error: $e")
                    null
                }
                produceDate != null && !produceDate.isBefore(startDate) && !produceDate.isAfter(endDate)
            }
            Log.d("ProduceViewModel", "Milk by cow filtered list for $timeRange ($startDate to $endDate): ${filteredList.joinToString()}")
            filteredList.groupBy { it.cow }
                .mapValues { entry -> entry.value.sumOf { it.morningQty + it.eveningQty } }
                .toList()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}