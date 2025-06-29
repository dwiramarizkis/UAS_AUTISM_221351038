package com.example.autism_221351038.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autism_221351038.R

class DatasetAdapter : RecyclerView.Adapter<DatasetAdapter.DatasetViewHolder>() {
    private val allRows = mutableListOf<List<String>>()
    private val columnWidths = mutableListOf<Int>()

    fun setAllRows(rows: List<List<String>>) {
        allRows.clear()
        allRows.addAll(rows)
        
        if (rows.isNotEmpty()) {
            calculateColumnWidths(rows[0])
        }
        
        notifyDataSetChanged()
    }

    fun clearData() {
        allRows.clear()
        columnWidths.clear()
        notifyDataSetChanged()
    }

    private fun calculateColumnWidths(headerRow: List<String>) {
        columnWidths.clear()
        val minWidth = 200
        for (i in headerRow.indices) {
            val headerWidth = headerRow[i].length * 18
            columnWidths.add(maxOf(headerWidth, minWidth))
        }
    }

    private fun isSpecialMessageRow(data: List<String>): Boolean {
        if (data.size == 1) {
            val message = data[0]
            return message.startsWith("Memuat") || 
                   message.startsWith("Error") || 
                   message.startsWith("Tidak ada data") ||
                   message.startsWith("Info")
        }
        return false
    }

    override fun getItemCount(): Int = allRows.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatasetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dataset_row, parent, false)
        return DatasetViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatasetViewHolder, position: Int) {
        holder.bind(allRows[position], columnWidths, position == 0, position)
    }

    class DatasetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container = itemView as LinearLayout

        fun bind(data: List<String>, columnWidths: List<Int>, isHeader: Boolean, position: Int) {
            container.removeAllViews()
            val isSpecialMessage = data.size == 1 && (
                data[0].startsWith("Memuat") || 
                data[0].startsWith("Error") || 
                data[0].startsWith("Tidak ada data") ||
                data[0].startsWith("Info")
            )
            if (isSpecialMessage) {
                val textView = TextView(container.context).apply {
                    this.text = data[0]
                    setPadding(16, 16, 16, 16)
                    textSize = 14f
                    setSingleLine(false)
                    setTextIsSelectable(false)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    when {
                        data[0].startsWith("Memuat") -> setTextColor(0xFF2196F3.toInt())
                        data[0].startsWith("Error") -> setTextColor(0xFFF44336.toInt())
                        data[0].startsWith("Tidak ada data") -> setTextColor(0xFFFF9800.toInt())
                        else -> setTextColor(0xFF4CAF50.toInt())
                    }
                    setBackgroundResource(R.drawable.circle_background_light)
                }
                container.addView(textView)
            } else {
                val colWidth = 15
                val rowText = data.joinToString(" | ") { it.padEnd(colWidth) }
                val textView = TextView(container.context).apply {
                    text = rowText
                    setPadding(12, 8, 12, 8)
                    textSize = if (isHeader) 15f else 14f
                    setTypeface(android.graphics.Typeface.MONOSPACE)
                    setHorizontallyScrolling(true)
                    setSingleLine(true)
                    setTextIsSelectable(false)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    if (isHeader) {
                        setTextColor(0xFFFFFFFF.toInt())
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium)
                        setBackgroundResource(R.drawable.header_background)
                    } else {
                        setTextColor(0xFF222222.toInt())
                        setBackgroundResource(R.drawable.cell_border)
                        if (position % 2 == 0) {
                            setBackgroundColor(0xFFFFFFFF.toInt())
                        } else {
                            setBackgroundColor(0xFFF8F9FA.toInt())
                        }
                    }
                }
                container.addView(textView)
            }
        }
    }
} 