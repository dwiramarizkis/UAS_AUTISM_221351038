package com.example.autism_221351038.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autism_221351038.R
import com.example.autism_221351038.databinding.FragmentDatasetBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class DatasetFragment : Fragment() {
    private var _binding: FragmentDatasetBinding? = null
    private val binding get() = _binding
    
    private lateinit var datasetAdapter: DatasetAdapter
    
    private val mainScope = MainScope()
    
    private var allData: List<List<String>> = emptyList()
    private var currentPage = 0
    private val pageSize = 50
    
    companion object {
        private const val TAG = "DatasetFragment"
        private const val PAGE_SIZE = 50
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDatasetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "DatasetFragment onViewCreated")
        
        binding?.let { safeBinding ->
            val btnLoadMore = safeBinding.root.findViewById<View>(R.id.btnLoadMore)
            btnLoadMore.visibility = View.GONE
            showLoading(true)
            mainScope.launch {
                try {
                    val csvData = withContext(Dispatchers.IO) {
                        readCsvFile("autism_screening.csv")
                    }
                    showLoading(false)
                    if (csvData.isEmpty()) {
                        showInfoRow("Tidak ada data yang ditemukan")
                        return@launch
                    }
                    val normalizedData = normalizeColumnCount(csvData)
                    updateStatistics(normalizedData)
                    allData = normalizedData
                    currentPage = 0
                    showTablePage()
                    btnLoadMore.setOnClickListener {
                        currentPage++
                        showTablePage()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in onViewCreated", e)
                    showLoading(false)
                    showInfoRow("Error: ${e.message}")
                }
            }
        }
    }

    private fun normalizeColumnCount(csvData: List<List<String>>): List<List<String>> {
        if (csvData.isEmpty()) return csvData
        
        val maxColumns = csvData.maxOfOrNull { it.size } ?: 0
        
        return csvData.map { row ->
            val normalizedRow = row.toMutableList()
            while (normalizedRow.size < maxColumns) {
                normalizedRow.add("")
            }
            normalizedRow
        }
    }

    private fun loadRemainingData(dataRows: List<List<String>>, startIndex: Int) {
    }

    private fun readCsvFile(fileName: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        
        try {
            val inputStream = requireContext().assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8), 8192)
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val row = parseCsvLine(line!!)
                result.add(row)
            }
            
            reader.close()
            inputStream.close()
            
            Log.d(TAG, "Successfully read ${result.size} rows from CSV")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CSV file", e)
            throw e
        }
        
        return result
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        var currentField = StringBuilder()
        
        for (char in line) {
            when (char) {
                '"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        currentField.append(char)
                    } else {
                        result.add(currentField.toString().trim())
                        currentField = StringBuilder()
                    }
                }
                else -> currentField.append(char)
            }
        }
        
        result.add(currentField.toString().trim())
        
        return result
    }

    private fun updateStatistics(csvData: List<List<String>>) {
        try {
            if (csvData.size < 2) return
            
            binding?.let { safeBinding ->
                val totalRecords = csvData.size - 1
                safeBinding.totalRecords.text = totalRecords.toString()
                
                val resultColumnIndex = csvData[0].size - 1
                var asdCount = 0
                var nonAsdCount = 0
                
                for (i in 1 until csvData.size) {
                    if (i < csvData.size && resultColumnIndex < csvData[i].size) {
                        val result = csvData[i][resultColumnIndex].trim().uppercase()
                        if (result == "YES" || result == "1") {
                            asdCount++
                        } else {
                            nonAsdCount++
                        }
                    }
                }
                
                safeBinding.asdCount.text = asdCount.toString()
                safeBinding.nonAsdCount.text = nonAsdCount.toString()
                
                Log.d(TAG, "Statistics updated: Total=$totalRecords, ASD=$asdCount, Non-ASD=$nonAsdCount")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating statistics", e)
        }
    }

    private fun showTable(tableData: List<List<String>>) {
        val tableLayout = _binding?.datasetTableLayout ?: return
        tableLayout.removeAllViews()
        if (tableData.isEmpty()) return
        val context = requireContext()
        val headerRow = TableRow(context)
        tableData[0].forEach { colName ->
            val tv = TextView(context).apply {
                text = colName
                setTypeface(null, Typeface.BOLD)
                setPadding(16, 12, 16, 12)
                setBackgroundResource(R.drawable.header_background)
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            }
            headerRow.addView(tv)
        }
        tableLayout.addView(headerRow)
        for (i in 1 until tableData.size) {
            val row = tableData[i]
            val tableRow = TableRow(context)
            row.forEach { cell ->
                val tv = TextView(context).apply {
                    text = cell
                    setPadding(16, 12, 16, 12)
                    setBackgroundResource(R.drawable.cell_border)
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                }
                if (i % 2 == 1) {
                    tv.setBackgroundColor(ContextCompat.getColor(context, R.color.table_row_odd))
                }
                tableRow.addView(tv)
            }
            tableLayout.addView(tableRow)
        }
    }

    private fun showInfoRow(message: String) {
        val tableLayout = _binding?.datasetTableLayout ?: return
        tableLayout.removeAllViews()
        val context = requireContext()
        val row = TableRow(context)
        val tv = TextView(context).apply {
            text = message
            setPadding(24, 24, 24, 24)
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }
        row.addView(tv)
        tableLayout.addView(row)
    }

    private fun showLoading(show: Boolean) {
        _binding?.datasetProgressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showTablePage() {
        binding?.let { safeBinding ->
            val btnLoadMore = safeBinding.root.findViewById<View>(R.id.btnLoadMore)
            val start = 1
            val end = minOf(1 + (currentPage + 1) * pageSize, allData.size)
            val pageData = if (allData.isNotEmpty()) listOf(allData[0]) + allData.subList(start, end) else emptyList()
            showTable(pageData)
            btnLoadMore.visibility = if (end < allData.size) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainScope.cancel()
        _binding = null
    }
} 