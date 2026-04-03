package com.canteen.tracker.domain.usecase

import android.net.Uri
import com.canteen.tracker.ocr.CanteenFormParser
import com.canteen.tracker.ocr.MlKitTextRecognizer
import com.canteen.tracker.ocr.model.ParsedCanteenForm
import com.canteen.tracker.ocr.model.ParsedEmployeeSection
import com.canteen.tracker.ocr.model.ParsedEntry
import javax.inject.Inject

data class BatchProcessResult(
    val forms: List<ImageProcessResult>,
    val mergedEmployees: List<ParsedEmployeeSection>,
    val cutoffPeriodText: String?
)

data class ImageProcessResult(
    val imageUri: Uri,
    val form: ParsedCanteenForm?,
    val error: String?
)

class ProcessImagesUseCase @Inject constructor(
    private val textRecognizer: MlKitTextRecognizer,
    private val formParser: CanteenFormParser
) {
    suspend fun processImages(imageUris: List<Uri>): BatchProcessResult {
        val results = imageUris.map { uri ->
            try {
                val text = textRecognizer.recognizeText(uri)
                val form = formParser.parse(text)
                ImageProcessResult(imageUri = uri, form = form, error = null)
            } catch (e: Exception) {
                ImageProcessResult(imageUri = uri, form = null, error = e.message)
            }
        }

        val allEmployees = mergeEmployeeSections(results)
        val cutoffPeriodText = results.firstNotNullOfOrNull { it.form?.cutoffPeriodText }

        return BatchProcessResult(
            forms = results,
            mergedEmployees = allEmployees,
            cutoffPeriodText = cutoffPeriodText
        )
    }

    private fun mergeEmployeeSections(results: List<ImageProcessResult>): List<ParsedEmployeeSection> {
        val employeeMap = mutableMapOf<String, MutableList<ParsedEntry>>()
        val employeeTotals = mutableMapOf<String, Double>()

        for (result in results) {
            val form = result.form ?: continue
            for (section in form.employees) {
                val normalizedName = section.name.trim().lowercase()
                employeeMap.getOrPut(normalizedName) { mutableListOf() }
                    .addAll(section.entries)
                employeeTotals[normalizedName] =
                    (employeeTotals[normalizedName] ?: 0.0) + (section.total ?: 0.0)
            }
        }

        return employeeMap.map { (name, entries) ->
            ParsedEmployeeSection(
                name = name.replaceFirstChar { it.uppercase() },
                entries = entries,
                total = employeeTotals[name]
            )
        }.sortedBy { it.name }
    }
}
