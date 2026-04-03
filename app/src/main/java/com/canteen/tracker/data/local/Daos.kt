package com.canteen.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: EmployeeEntity): Long

    @Query("SELECT * FROM employees WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): EmployeeEntity?

    @Query("SELECT * FROM employees ORDER BY name")
    fun getAll(): Flow<List<EmployeeEntity>>
}

@Dao
interface CutoffPeriodDao {
    @Insert
    suspend fun insert(cutoffPeriod: CutoffPeriodEntity): Long

    @Query("SELECT * FROM cutoff_periods ORDER BY createdAt DESC")
    fun getAll(): Flow<List<CutoffPeriodEntity>>

    @Query("SELECT * FROM cutoff_periods WHERE id = :id")
    suspend fun getById(id: Long): CutoffPeriodEntity?
}

@Dao
interface DeductionEntryDao {
    @Insert
    suspend fun insert(entry: DeductionEntryEntity): Long

    @Insert
    suspend fun insertAll(entries: List<DeductionEntryEntity>)

    @Update
    suspend fun update(entry: DeductionEntryEntity)

    @Query("DELETE FROM deduction_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM deduction_entries WHERE cutoffPeriodId = :cutoffPeriodId ORDER BY employeeId, date")
    fun getByCutoffPeriod(cutoffPeriodId: Long): Flow<List<DeductionEntryEntity>>

    @Query("SELECT * FROM deduction_entries WHERE employeeId = :employeeId AND cutoffPeriodId = :cutoffPeriodId ORDER BY date")
    fun getByEmployeeAndCutoff(employeeId: Long, cutoffPeriodId: Long): Flow<List<DeductionEntryEntity>>

    @Query("SELECT SUM(amount) FROM deduction_entries WHERE cutoffPeriodId = :cutoffPeriodId")
    suspend fun getTotalByCutoff(cutoffPeriodId: Long): Double?

    @Query("SELECT SUM(amount) FROM deduction_entries WHERE employeeId = :employeeId AND cutoffPeriodId = :cutoffPeriodId")
    suspend fun getTotalByEmployeeAndCutoff(employeeId: Long, cutoffPeriodId: Long): Double?
}

@Dao
interface ScanSessionDao {
    @Insert
    suspend fun insert(scanSession: ScanSessionEntity): Long

    @Query("SELECT * FROM scan_sessions ORDER BY scannedAt DESC")
    fun getAll(): Flow<List<ScanSessionEntity>>

    @Query("SELECT * FROM scan_sessions WHERE cutoffPeriodId = :cutoffPeriodId")
    fun getByCutoffPeriod(cutoffPeriodId: Long): Flow<List<ScanSessionEntity>>
}
