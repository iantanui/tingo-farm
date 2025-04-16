package com.example.tingofarm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tingofarm.data.model.Employee
import com.example.tingofarm.data.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    val totalEmployees: StateFlow<Int> = _employees.map { it.size }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0
    )

    init {
        fetchEmployees()
    }

    // fetch employee
    private fun fetchEmployees() {
        viewModelScope.launch {
            _employees.value = repository.getEmployees()
            Log.d("EmployeeViewModel", "Fetched employees: ${_employees.value}")
        }
    }

    // add employee
    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.addEmployee(employee)
            fetchEmployees()
        }
    }

    // Edit an employee's details
    fun updateEmployee(employeeId: String, updatedEmployee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employeeId, updatedEmployee)
            fetchEmployees()
        }
    }

    // Delete an employee
    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            repository.deleteEmployee(employeeId)
            fetchEmployees()
        }
    }
}
