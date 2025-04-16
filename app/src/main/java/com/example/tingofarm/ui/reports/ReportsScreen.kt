package com.example.tingofarm.ui.reports

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tingofarm.data.repository.ReportRepository
import com.example.tingofarm.ui.components.BaseScreen
import com.example.tingofarm.viewmodel.AuthenticationViewModel
import com.example.tingofarm.viewmodel.ProduceViewModel
import com.example.tingofarm.viewmodel.ReportViewModel
import com.example.tingofarm.viewmodel.StockViewModel
import com.example.tingofarm.viewmodelfactory.ReportViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReportsScreen(
    navController: NavHostController,
    authenticationViewModel: AuthenticationViewModel,
    produceViewModel: ProduceViewModel,
    stockViewModel: StockViewModel,
    reportViewModel: ReportViewModel = viewModel(
        factory = ReportViewModelFactory(ReportRepository(FirebaseFirestore.getInstance()))
    ),
) {
    var selectedRange by remember { mutableStateOf("Weekly") }
    var isLoading by remember { mutableStateOf(true) }

    val totalProduceByDate by produceViewModel.getTotalProduceByDate(selectedRange).collectAsState()
    val milkByCow by produceViewModel.getMilkByCow(selectedRange).collectAsState(initial = emptyList())

    LaunchedEffect(selectedRange) {
        produceViewModel.fetchTimeRangeProduce()
        delay(1000)
        isLoading = true
        withTimeoutOrNull(3000) {
            while (produceViewModel.timeRangeProduceList.value.isEmpty()) {
                delay(100)
            }
        }
        isLoading = false
    }

    LaunchedEffect(totalProduceByDate) {
        println("DEBUG: produce -> $totalProduceByDate")
    }

    LaunchedEffect(milkByCow) {
        println("DEBUG: milk by cow -> $milkByCow")
    }

    LaunchedEffect(selectedRange) {
        println("DEBUG: selected range -> $selectedRange")
    }

    BaseScreen(
        navController = navController,
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(Color.White)
            ) {

                item {
                    Text(
                        text = "Farm reports",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    TimeRangeToggle(selectedRange) { selectedRange = it }
                }

                // Milk Production Report
                item {
                    ReportChart("Milk production", totalProduceByDate, selectedRange)
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    BarChart("Milk Production by cow", milkByCow, selectedRange)
                }

                /*item {
                    ReportChart("Stock usage", stockReports)
                } */
            }
        },
        onLogout = {
            authenticationViewModel.logout()
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true}
            }
        }
    )
}

@Composable
fun TimeRangeToggle(selectedRange: String, onRangeSelected: (String) -> Unit) {
    val ranges = listOf("Weekly", "Monthly", "6 Months", "Yearly")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ranges.forEach { range ->
            Button(
                onClick = { onRangeSelected(range) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRange == range) Color.DarkGray else Color.LightGray
                )
            ) {
                Text(text = range, color = Color.White)
            }
        }
    }
}


@Composable
fun ReportChart(title: String, data: List<Pair<String, Double>>, selectedRange: String) {

    val entries = data.mapIndexed { index, (_, value) ->
        Entry(index.toFloat(), value.toFloat())
    }
    val labels = data.map { it.first }

    val lineDataSet = LineDataSet(entries, title).apply {
        color = ColorTemplate.MATERIAL_COLORS[0]
        valueTextColor = android.graphics.Color.BLACK
        lineWidth = 2f
        setCircleColor(ColorTemplate.MATERIAL_COLORS[1])
    }
    val lineData = LineData(lineDataSet)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { context ->
                LineChart(context).apply {
                    this.data = lineData
                    description.isEnabled = false
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        valueFormatter = IndexAxisValueFormatter(labels)
                        textSize = 10f
                        labelRotationAngle = -45f
                    }
                    axisLeft.apply {
                        axisMinimum = 0f
                        granularity = 1f
                        textSize = 12f
                        setDrawGridLines(true)
                    }

                    axisRight.isEnabled = false
                    invalidate()
                    setPinchZoom(false)
                    animateY(1000)
                }
            },
            update = { lineChart ->
                if (data.isNotEmpty()) {
                    lineChart.data = lineData
                    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                }
            }
        )
    }
}

@Composable
fun BarChart(title: String, data: List<Pair<String, Double>>, selectedRange: String) {

    val entries = data.mapIndexed { index, (_, value) ->
        BarEntry(index.toFloat(), value.toFloat())
    }
    val labels = data.map { it.first }

    LaunchedEffect(data) {
        println("DEBUG: BarChart data -> $data")
        println("DEBUG: BarChart labels -> $labels")
        println("DEBUG: BarChart entries -> $entries")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { context ->
                BarChart(context).apply {
                    this.data = barData
                    description.isEnabled = false
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        valueFormatter = IndexAxisValueFormatter(labels)
                        setLabelCount(labels.size, true)
                        textSize = 10f
                        labelRotationAngle = -30f
                        setDrawGridLines(false)
                    }

                    axisLeft.apply {
                        axisMinimum = 0f
                        granularity = 1f
                        textSize = 12f
                        setDrawGridLines(true)
                    }

                    axisRight.isEnabled = false
                    setFitBars(true)
                    setPinchZoom(false)
                    animateY(1000)

                }
            },
            update = { barChart ->
                if (data.isNotEmpty()) {

                    val barDataSet = BarDataSet(entries, title).apply {
                        color = ColorTemplate.MATERIAL_COLORS[2]
                        valueTextColor = android.graphics.Color.BLACK
                        valueTextSize = 12f
                    }

                    val barData = BarData(barDataSet).apply {
                        barWidth = 0.5f
                        setValueTextSize(10f)
                    }

                    barChart.data = barData
                    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    barChart.xAxis.setLabelCount(labels.size, false)
                    barChart.notifyDataSetChanged()
                    barChart.invalidate()
                }
            }
        )
    }
}
