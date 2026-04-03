package com.canteen.tracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class Employee(
    val id: Long = 0,
    val name: String
)

data class CutoffPeriod(
    val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val year: Int,
    val createdAt: Instant = Instant.now()
)

data class DeductionEntry(
    val id: Long = 0,
    val employeeId: Long,
    val cutoffPeriodId: Long,
    val date: Int,
    val description: String,
    val amount: Double,
    val sourceImageUri: String
)

data class ScanSession(
    val id: Long = 0,
    val cutoffPeriodId: Long,
    val scannedAt: Instant = Instant.now(),
    val imageCount: Int
)

data class EmployeeDeductionSummary(
    val employee: Employee,
    val entries: List<DeductionEntry>,
    val total: Double
)

data class CutoffReport(
    val cutoffPeriod: CutoffPeriod,
    val employeeSummaries: List<EmployeeDeductionSummary>,
    val grandTotal: Double
)
