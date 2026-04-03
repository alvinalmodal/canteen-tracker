package com.canteen.tracker.ocr

import com.canteen.tracker.ocr.model.ParsedCanteenForm
import com.canteen.tracker.ocr.model.ParsedEmployeeSection
import com.canteen.tracker.ocr.model.ParsedEntry
import com.google.mlkit.vision.text.Text
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanteenFormParser @Inject constructor() {

    fun parse(text: Text): ParsedCanteenForm {
        val lines = extractLines(text)
        if (lines.isEmpty()) return emptyForm()

        val imageWidth = estimateImageWidth(lines)
        val cutoffPeriodText = extractCutoffPeriod(lines)
        val (cutoffStart, cutoffEnd) = parseCutoffDates(cutoffPeriodText)

        val headerEndY = findHeaderEnd(lines)
        val dataLines = lines.filter { it.centerY > headerEndY }

        val sections = splitIntoEmployeeSections(dataLines, imageWidth)
        val firstName = extractFirstEmployeeName(lines, headerEndY)

        val employeeSections = buildEmployeeSections(sections, firstName)

        return ParsedCanteenForm(
            cutoffPeriodText = cutoffPeriodText,
            cutoffStartDate = cutoffStart,
            cutoffEndDate = cutoffEnd,
            employees = employeeSections
        )
    }

    private data class TextLine(
        val text: String,
        val centerY: Float,
        val centerX: Float,
        val left: Float,
        val right: Float,
        val top: Float,
        val bottom: Float,
        val height: Float
    )

    private fun extractLines(text: Text): List<TextLine> {
        return text.textBlocks.flatMap { block ->
            block.lines.mapNotNull { line ->
                val box = line.boundingBox ?: return@mapNotNull null
                TextLine(
                    text = line.text.trim(),
                    centerY = (box.top + box.bottom) / 2f,
                    centerX = (box.left + box.right) / 2f,
                    left = box.left.toFloat(),
                    right = box.right.toFloat(),
                    top = box.top.toFloat(),
                    bottom = box.bottom.toFloat(),
                    height = (box.bottom - box.top).toFloat()
                )
            }
        }.sortedBy { it.centerY }
    }

    private fun estimateImageWidth(lines: List<TextLine>): Float {
        return lines.maxOfOrNull { it.right } ?: 1000f
    }

    private fun extractCutoffPeriod(lines: List<TextLine>): String? {
        for (line in lines) {
            val text = line.text.lowercase()
            if (text.contains("cut") && text.contains("period")) {
                // The cutoff period value might be on the same line after ":"
                val colonIndex = line.text.indexOf(':')
                if (colonIndex >= 0 && colonIndex < line.text.length - 1) {
                    return line.text.substring(colonIndex + 1).trim()
                }
            }
            // Check for date-like patterns that are near the top and right side
            if (line.centerY < lines.first().centerY + 100 && CUTOFF_DATE_PATTERN.containsMatchIn(line.text)) {
                return line.text.trim()
            }
        }
        return null
    }

    private fun parseCutoffDates(cutoffText: String?): Pair<LocalDate?, LocalDate?> {
        if (cutoffText == null) return Pair(null, null)
        // Try patterns like "March 1-15, 2025" or "March 1-15,2025"
        val match = CUTOFF_FULL_PATTERN.find(cutoffText)
        if (match != null) {
            val month = parseMonth(match.groupValues[1])
            val startDay = match.groupValues[2].toIntOrNull()
            val endDay = match.groupValues[3].toIntOrNull()
            val year = match.groupValues[4].toIntOrNull()
            if (month != null && startDay != null && endDay != null && year != null) {
                return Pair(
                    LocalDate.of(year, month, startDay),
                    LocalDate.of(year, month, endDay)
                )
            }
        }
        return Pair(null, null)
    }

    private fun parseMonth(text: String): Int? {
        return when (text.lowercase().take(3)) {
            "jan" -> 1; "feb" -> 2; "mar" -> 3; "apr" -> 4
            "may" -> 5; "jun" -> 6; "jul" -> 7; "aug" -> 8
            "sep" -> 9; "oct" -> 10; "nov" -> 11; "dec" -> 12
            else -> null
        }
    }

    private fun findHeaderEnd(lines: List<TextLine>): Float {
        // Look for the "Date" / "Description" / "Amount" header row
        for (line in lines) {
            val text = line.text.lowercase()
            if (text.contains("date") && (text.contains("description") || text.contains("amount"))) {
                return line.bottom
            }
        }
        // Look for "CANTEEN DEDUCTION" and assume header is ~150px below it
        for (line in lines) {
            if (line.text.uppercase().contains("CANTEEN") && line.text.uppercase().contains("DEDUCTION")) {
                return line.bottom + 80
            }
        }
        // Fallback: header is top 15% of content
        val minY = lines.minOf { it.top }
        val maxY = lines.maxOf { it.bottom }
        return minY + (maxY - minY) * 0.15f
    }

    private fun extractFirstEmployeeName(lines: List<TextLine>, headerEndY: Float): String? {
        // Look for "Name:" field in header area
        for (line in lines) {
            if (line.centerY > headerEndY) break
            val text = line.text
            val nameMatch = NAME_FIELD_PATTERN.find(text)
            if (nameMatch != null) {
                val name = nameMatch.groupValues[1].trim()
                if (name.isNotEmpty()) return name
            }
        }
        return null
    }

    private fun splitIntoEmployeeSections(
        dataLines: List<TextLine>,
        imageWidth: Float
    ): List<Pair<String?, List<TextLine>>> {
        val sections = mutableListOf<Pair<String?, List<TextLine>>>()
        var currentName: String? = null
        var currentLines = mutableListOf<TextLine>()

        for (line in dataLines) {
            if (isEmployeeNameLine(line, imageWidth)) {
                if (currentLines.isNotEmpty() || currentName != null) {
                    sections.add(Pair(currentName, currentLines.toList()))
                    currentLines = mutableListOf()
                }
                currentName = cleanEmployeeName(line.text)
            } else {
                currentLines.add(line)
            }
        }
        if (currentLines.isNotEmpty() || currentName != null) {
            sections.add(Pair(currentName, currentLines.toList()))
        }

        return sections
    }

    private fun isEmployeeNameLine(line: TextLine, imageWidth: Float): Boolean {
        val text = line.text.trim()
        // Skip if it looks like a data row (starts with a number = date, or ends with a number = amount)
        if (DATE_PREFIX_PATTERN.matches(text)) return false
        if (text.isEmpty()) return false

        // Employee names are typically:
        // - Short (1-2 words, < 20 chars)
        // - Don't contain numbers (or very few)
        // - Not common food/header words
        val wordCount = text.split("\\s+".toRegex()).size
        val digitRatio = text.count { it.isDigit() }.toFloat() / text.length

        if (wordCount > 3) return false
        if (digitRatio > 0.2) return false
        if (isHeaderOrFoodWord(text)) return false

        // Names tend to be relatively centered or left-aligned in the name column
        return text.length < 20 && !AMOUNT_ONLY_PATTERN.matches(text)
    }

    private fun isHeaderOrFoodWord(text: String): Boolean {
        val lower = text.lowercase()
        return lower in setOf(
            "date", "description", "amount", "name", "received by",
            "canteen deduction", "cut off period", "total"
        )
    }

    private fun cleanEmployeeName(text: String): String {
        return text.trim()
            .replace(LEADING_NUMBERS_PATTERN, "")
            .trim()
    }

    private fun buildEmployeeSections(
        sections: List<Pair<String?, List<TextLine>>>,
        firstName: String?
    ): List<ParsedEmployeeSection> {
        val result = mutableListOf<ParsedEmployeeSection>()

        for ((index, section) in sections.withIndex()) {
            val (sectionName, lines) = section
            val name = sectionName
                ?: if (index == 0) firstName
                else "Unknown ${index + 1}"

            if (name == null && lines.isEmpty()) continue

            val entries = parseEntries(lines)
            val total = extractTotal(lines) ?: entries.mapNotNull { it.amount }.sum()

            result.add(
                ParsedEmployeeSection(
                    name = name ?: "Unknown",
                    entries = entries,
                    total = total
                )
            )
        }

        return result
    }

    private fun parseEntries(lines: List<TextLine>): List<ParsedEntry> {
        val entries = mutableListOf<ParsedEntry>()

        for (line in lines) {
            val text = line.text.trim()
            if (text.isEmpty()) continue
            // Skip lines that look like totals
            if (TOTAL_PATTERN.containsMatchIn(text.lowercase())) continue
            // Skip lines that are just a number (likely a standalone total)
            if (AMOUNT_ONLY_PATTERN.matches(text)) continue

            val entry = parseEntryLine(text)
            if (entry != null) entries.add(entry)
        }

        return entries
    }

    private fun parseEntryLine(text: String): ParsedEntry? {
        // Pattern: [date] [description] [amount]
        // Date is 1-2 digits at the start
        // Amount is a number at the end (possibly with comma separators)
        val dateMatch = ENTRY_DATE_PATTERN.find(text)
        val amountMatch = ENTRY_AMOUNT_PATTERN.find(text)

        val date = dateMatch?.groupValues?.get(1)?.toIntOrNull()
        val amount = amountMatch?.groupValues?.get(1)
            ?.replace(",", "")
            ?.toDoubleOrNull()

        // Description is everything between date and amount
        var description = text
        if (dateMatch != null) {
            description = description.substring(dateMatch.range.last + 1)
        }
        if (amountMatch != null) {
            val amountStart = description.lastIndexOf(amountMatch.groupValues[1])
            if (amountStart > 0) {
                description = description.substring(0, amountStart)
            }
        }
        description = description.trim().trimEnd(',', ' ')

        if (description.isEmpty() && date == null && amount == null) return null

        return ParsedEntry(
            date = date,
            description = description,
            amount = amount
        )
    }

    private fun extractTotal(lines: List<TextLine>): Double? {
        // Look for standalone number at the bottom, or a line with "total"
        for (line in lines.reversed()) {
            val text = line.text.trim()
            if (AMOUNT_ONLY_PATTERN.matches(text)) {
                return text.replace(",", "").toDoubleOrNull()
            }
        }
        return null
    }

    private fun emptyForm() = ParsedCanteenForm(
        cutoffPeriodText = null,
        cutoffStartDate = null,
        cutoffEndDate = null,
        employees = emptyList()
    )

    companion object {
        private val CUTOFF_DATE_PATTERN = Regex("""[A-Za-z]+\s*\d+\s*[-–]\s*\d+""")
        private val CUTOFF_FULL_PATTERN = Regex("""([A-Za-z]+)\s*(\d+)\s*[-–]\s*(\d+)\s*[,.]?\s*(\d{4})""")
        private val NAME_FIELD_PATTERN = Regex("""[Nn]ame\s*:?\s*(.+)""")
        private val DATE_PREFIX_PATTERN = Regex("""^\d{1,2}\s+.+\d+\s*$""")
        private val ENTRY_DATE_PATTERN = Regex("""^(\d{1,2})\s+""")
        private val ENTRY_AMOUNT_PATTERN = Regex("""(\d[\d,]*\.?\d*)\s*$""")
        private val AMOUNT_ONLY_PATTERN = Regex("""^\d[\d,]*\.?\d*$""")
        private val TOTAL_PATTERN = Regex("""total""")
        private val LEADING_NUMBERS_PATTERN = Regex("""^\d+\s*""")
    }
}
