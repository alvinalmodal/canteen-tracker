package com.canteen.tracker.ocr.model

import java.time.LocalDate

data class ParsedCanteenForm(
    val cutoffPeriodText: String?,
    val cutoffStartDate: LocalDate?,
    val cutoffEndDate: LocalDate?,
    val employees: List<ParsedEmployeeSection>
)

data class ParsedEmployeeSection(
    val name: String,
    val entries: List<ParsedEntry>,
    val total: Double?
)

data class ParsedEntry(
    val date: Int?,
    val description: String,
    val amount: Double?
)
