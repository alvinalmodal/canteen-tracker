package com.canteen.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        EmployeeEntity::class,
        CutoffPeriodEntity::class,
        DeductionEntryEntity::class,
        ScanSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CanteenDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun cutoffPeriodDao(): CutoffPeriodDao
    abstract fun deductionEntryDao(): DeductionEntryDao
    abstract fun scanSessionDao(): ScanSessionDao
}
