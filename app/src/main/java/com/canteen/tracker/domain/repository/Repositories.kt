package com.canteen.tracker.domain.repository

import com.canteen.tracker.domain.model.CutoffPeriod
import com.canteen.tracker.domain.model.DeductionEntry
import com.canteen.tracker.domain.model.Employee
import com.canteen.tracker.domain.model.ScanSession
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getAll(): Flow<List<Employee>>
    suspend fun findByName(name: String): Employee?
    suspend fun getOrCreate(name: String): Employee
}

interface CutoffPeriodRepository {
    fun getAll(): Flow<List<CutoffPeriod>>
    suspend fun getById(id: Long): CutoffPeriod?
    suspend fun create(cutoffPeriod: CutoffPeriod): Long
}

interface DeductionRepository {
    fun getByCutoffPeriod(cutoffPeriodId: Long): Flow<List<DeductionEntry>>
    fun getByEmployeeAndCutoff(employeeId: Long, cutoffPeriodId: Long): Flow<List<DeductionEntry>>
    suspend fun insert(entry: DeductionEntry): Long
    suspend fun insertAll(entries: List<DeductionEntry>)
    suspend fun update(entry: DeductionEntry)
    suspend fun delete(id: Long)
    suspend fun getTotalByCutoff(cutoffPeriodId: Long): Double
    suspend fun getTotalByEmployeeAndCutoff(employeeId: Long, cutoffPeriodId: Long): Double
}

interface ScanSessionRepository {
    fun getAll(): Flow<List<ScanSession>>
    fun getByCutoffPeriod(cutoffPeriodId: Long): Flow<List<ScanSession>>
    suspend fun create(scanSession: ScanSession): Long
}
