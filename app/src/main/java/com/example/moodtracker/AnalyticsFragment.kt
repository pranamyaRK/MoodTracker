package com.example.moodtracker

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class AnalyticsFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        databaseHelper = DatabaseHelper(requireContext())

        // Find the LineChart in the layout
        val lineChart: LineChart = view.findViewById(R.id.moodLineChart)
        val pieChart: PieChart = view.findViewById(R.id.ActivityPieChart)

        // Prepare mood data (for demonstration purposes)
        val moodEntries = mutableListOf<Entry>()
        val moodData = databaseHelper.getMoodData() // Custom method to get mood data from the database

        moodData.forEachIndexed { date, mood ->
            moodEntries.add(Entry(date.toFloat(), mood.second.toFloat()))
        }

        // Create dataset and configure chart as explained before
        val lineDataSet = LineDataSet(moodEntries, null)
        Log.d("LineChartDebug", "Mood Entries: $moodEntries")

        lineDataSet.color = Color.BLACK
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.circleRadius = 5f
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.description.isEnabled = false

        // Customize x-axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return moodData.getOrNull(value.toInt())?.first ?: ""
            }
        }

        // Customize y-axis
        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 4f
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    4 -> "Joyful"
                    3 -> "Happy"
                    2 -> "Meh"
                    1 -> "Sad"
                    0 -> "Crying"
                    else -> ""
                }
            }
        }
        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()

        val activityCountMap = databaseHelper.getActivityData()

        // Convert data to PieEntries
        val entries = activityCountMap.map { (activity, count) ->
            PieEntry(count.toFloat(), activity)
        }

        // Create PieDataSet and configure the PieChart
        val pieDataSet = PieDataSet(entries, null)
        pieDataSet.colors = ColorTemplate.LIBERTY_COLORS.toList()
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 16f

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        // Customize PieChart
        pieChart.centerText = "Activity Breakdown"
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)

        // Customize the Legend
        val legend = pieChart.legend
        legend.isEnabled = true // Enable legend
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = 10f // Size of the legend icons
        legend.textSize = 14f // Text size of the legend labels
        legend.xEntrySpace = 10f // Horizontal spacing between entries
        legend.yEntrySpace = 5f // Vertical spacing between entries
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // Position the legend vertically
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // Position the legend horizontally

        // Refresh chart
        pieChart.invalidate()
    }
}