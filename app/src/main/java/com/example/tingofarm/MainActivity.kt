package com.example.tingofarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.tingofarm.data.network.ApiService
import com.example.tingofarm.data.repository.AuthenticationRepository
import com.example.tingofarm.data.repository.EmployeeRepository
import com.example.tingofarm.data.repository.LivestockRepository
import com.example.tingofarm.data.repository.ProduceRepository
import com.example.tingofarm.data.repository.ReportRepository
import com.example.tingofarm.data.repository.StockRepository
import com.example.tingofarm.ui.navigation.NavigationGraph
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.EmployeeViewModel
import com.example.tingofarm.viewmodel.LivestockViewModel
import com.example.tingofarm.viewmodel.ProduceViewModel
import com.example.tingofarm.viewmodel.ReportViewModel
import com.example.tingofarm.viewmodel.StockViewModel
import com.example.tingofarm.viewmodelfactory.AuthenticationViewModelFactory
import com.example.tingofarm.viewmodelfactory.EmployeeViewModelFactory
import com.example.tingofarm.viewmodelfactory.LivestockViewModelFactory
import com.example.tingofarm.viewmodelfactory.ProduceViewModelFactory
import com.example.tingofarm.viewmodelfactory.ReportViewModelFactory
import com.example.tingofarm.viewmodelfactory.StockViewModelFactory
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var apiService: ApiService
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var employeeRepository: EmployeeRepository
    private lateinit var livestockRepository: LivestockRepository
    private lateinit var produceRepository: ProduceRepository
    private lateinit var stockRepository: StockRepository
    private lateinit var reportRepository: ReportRepository

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var employeeViewModel: EmployeeViewModel
    private lateinit var livestockViewModel: LivestockViewModel
    private lateinit var produceViewModel: ProduceViewModel
    private lateinit var stockViewModel: StockViewModel
    private lateinit var reportViewModel: ReportViewModel

    // Lazy initialization of ApiService
    private fun initializeRepositories() {
        apiService = Retrofit.Builder()
            .baseUrl("https://your.api.url/") // Provide your base URL here
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        authenticationRepository = AuthenticationRepository()
        employeeRepository = EmployeeRepository(FirebaseFirestore.getInstance())
        livestockRepository = LivestockRepository(FirebaseFirestore.getInstance())
        produceRepository = ProduceRepository(FirebaseFirestore.getInstance())
        stockRepository = StockRepository(FirebaseFirestore.getInstance())
        reportRepository = ReportRepository(FirebaseFirestore.getInstance())
    }

    private fun initializeViewModels() {
        // Initialize the ViewModels with their respective factories
        authenticationViewModel = ViewModelProvider(
            this, AuthenticationViewModelFactory(authenticationRepository)
        )[AuthenticationViewModel::class.java]

        employeeViewModel = ViewModelProvider(
            this, EmployeeViewModelFactory(employeeRepository)
        )[EmployeeViewModel::class.java]

        livestockViewModel = ViewModelProvider(
            this, LivestockViewModelFactory(livestockRepository)
        )[LivestockViewModel::class.java]

        produceViewModel = ViewModelProvider(
            this, ProduceViewModelFactory(produceRepository)
        )[ProduceViewModel::class.java]

        stockViewModel = ViewModelProvider(
            this, StockViewModelFactory(stockRepository)
        )[StockViewModel::class.java]

        reportViewModel = ViewModelProvider(
            this, ReportViewModelFactory(reportRepository)
        )[ReportViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize MPAndroidChart Utils
        Utils.init(this)

        // Initialize Repositories and ViewModels
        initializeRepositories()
        initializeViewModels()

        setContent {
            val navController = rememberNavController()
            NavigationGraph(
                navController = navController,
                authenticationViewModel = authenticationViewModel,
                employeeViewModel = employeeViewModel,
                livestockViewModel = livestockViewModel,
                produceViewModel = produceViewModel,
                stockViewModel = stockViewModel,
                reportViewModel = reportViewModel
            )
        }
    }
}