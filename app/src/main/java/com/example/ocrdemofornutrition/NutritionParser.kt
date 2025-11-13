package com.example.ocrdemofornutrition
import android.util.Log
import com.google.mlkit.vision.text.Text

data class NutritionRow(
    val leftCell: String,
    val rightCell: String?
)
object NutritionParser {

    fun extractNutritionTable(visionText: Text): List<NutritionRow> {
        val rows = mutableListOf<NutritionRow>()
        val percentRegex = Regex("\\d+%")

        // Collect all lines with their positions
        val lineData = mutableListOf<Pair<String, Int>>() // text + Y position
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val y = line.boundingBox?.centerY() ?: 0
                lineData.add(line.text.trim() to y)
            }
        }

        // Sort lines by vertical position
        val sortedLines = lineData.sortedBy { it.second }

        var skipNext = false
        for (i in sortedLines.indices) {
            if (skipNext) {
                skipNext = false
                continue
            }

            val (text, y) = sortedLines[i]
            val percentInSameLine = percentRegex.find(text)?.value

            if (percentInSameLine != null) {
                // Case 1: %DV is in same line
                val leftText = text.replace(percentInSameLine, "").trim()
                rows.add(NutritionRow(leftText, percentInSameLine))
            } else {
                // Case 2: check if next line contains % (like "4%" in next line)
                val next = sortedLines.getOrNull(i + 1)
                val nextPercent = next?.first?.let { percentRegex.find(it)?.value }

                if (next != null && nextPercent != null && (next.second - y) < 40) {
                    // close vertically → treat as same row
                    rows.add(NutritionRow(text, nextPercent))
                    skipNext = true
                } else {
                    // no % value near it
                    rows.add(NutritionRow(text, null))
                }
            }
        }

        return rows
    }

    fun printTable(rows: List<NutritionRow>) {
        for (row in rows) {
            Log.d("NutritionRow", "${row.leftCell} | ${row.rightCell ?: ""}")
        }
    }
}
