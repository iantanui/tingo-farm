package com.example.tingofarm.data.repository

import android.util.Log
import com.example.tingofarm.data.model.Employee
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun getEmployees(): List<Employee> {
        return try {
            firestore.collection("employees")
                .get()
                .await()
                .toObjects(Employee::class.java)
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error fetching employees", e)
            emptyList()
        }
    }

    // add new employee to Firestore
    suspend fun addEmployee(employee: Employee) {
        try {
            val documentRef = firestore.collection("employees").add(employee).await()
            val id = documentRef.id
            documentRef.update("id", id)
            Log.d("EmployeeRepository", "Employee added: $employee")
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error adding employee", e)
        }
    }

    // Update an employee's details
    suspend fun updateEmployee(employeeId: String, updatedEmployee: Employee) {
        try {
            firestore.collection("employees").document(employeeId).set(updatedEmployee).await()
            Log.d("EmployeeRepository", "Employee updated: $updatedEmployee")
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error updating employee", e)
        }
    }


    // Delete an employee
    suspend fun deleteEmployee(employeeId: String) {
        try {
            firestore.collection("employees").document(employeeId).delete().await()
            Log.d("EmployeeRepository", "Employee deleted with ID: $employeeId")
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error deleting employee", e)
        }
    }
}
