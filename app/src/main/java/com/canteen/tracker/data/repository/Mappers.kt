package com.canteen.tracker.data.repository

import com.canteen.tracker.data.local.CutoffPeriodEntity
import com.canteen.tracker.data.local.DeductionEntryEntity
import com.canteen.tracker.data.local.EmployeeEntity
import com.canteen.tracker.data.local.ScanSessionEntity
import com.canteen.tracker.domain.model.CutoffPeriod
import com.canteen.tracker.domain.model.DeductionEntry
import com.canteen.tracker.domain.model.Employee
import com.canteen.tracker.domain.model.ScanSession

fun EmployeeEntity.toDomain() = Employee(id = id, name = name)
fun Employee.toEntity() = EmployeeEntity(id = id, name = name)

fun CutoffPeriodEntity.toDomain() = CutoffPeriod(
    id = id, startDate = startDate, endDate = endDate, year = year, createdAt = createdAt
)
fun CutoffPeriod.toEntity() = CutoffPeriodEntity(
    id = id, startDate = startDate, endDate = endDate, year = year, createdAt = createdAt
)

fun DeductionEntryEntity.toDomain() = DeductionEntry(
    id = id, employeeId = employeeId, cutoffPeriodId = cutoffPeriodId,
    date = date, description = description, amount = amount, sourceImageUri = sourceImageUri
)
fun DeductionEntry.toEntity() = DeductionEntryEntity(
    id = id, employeeId = employeeId, cutoffPeriodId = cutoffPeriodId,
    date = date, description = description, amount = amount, sourceImageUri = sourceImageUri
)

fun ScanSessionEntity.toDomain() = ScanSession(
    id = id, cutoffPeriodId = cutoffPeriodId, scannedAt = scannedAt, imageCount = imageCount
)
fun ScanSession.toEntity() = ScanSessionEntity(
    id = id, cutoffPeriodId = cutoffPeriodId, scannedAt = scannedAt, imageCount = imageCount
)
