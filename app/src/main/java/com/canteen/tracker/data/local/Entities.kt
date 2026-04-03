package com.canteen.tracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(tableName = "cutoff_periods")
data class CutoffPeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val year: Int,
    val createdAt: Instant = Instant.now()
)

@Entity(
    tableName = "deduction_entries",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CutoffPeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["cutoffPeriodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("employeeId"),
        Index("cutoffPeriodId")
    ]
)
data class DeductionEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val cutoffPeriodId: Long,
    val date: Int,
    val description: String,
    val amount: Double,
    val sourceImageUri: String
)

@Entity(
    tableName = "scan_sessions",
    foreignKeys = [
        ForeignKey(
            entity = CutoffPeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["cutoffPeriodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cutoffPeriodId")]
)
data class ScanSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cutoffPeriodId: Long,
    val scannedAt: Instant = Instant.now(),
    val imageCount: Int
)
