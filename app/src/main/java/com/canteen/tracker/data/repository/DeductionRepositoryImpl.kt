package com.canteen.tracker.data.repository

import com.canteen.tracker.data.local.DeductionEntryDao
import com.canteen.tracker.domain.model.DeductionEntry
import com.canteen.tracker.domain.repository.DeductionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeductionRepositoryImpl @Inject constructor(
    private val deductionEntryDao: DeductionEntryDao
) : DeductionRepository {

    override fun getByCutoffPeriod(cutoffPeriodId: Long): Flow<List<DeductionEntry>> =
        deductionEntryDao.getByCutoffPeriod(cutoffPeriodId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getByEmployeeAndCutoff(employeeId: Long, cutoffPeriodId: Long): Flow<List<DeductionEntry>> =
        deductionEntryDao.getByEmployeeAndCutoff(employeeId, cutoffPeriodId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insert(entry: DeductionEntry): Long =
        deductionEntryDao.insert(entry.toEntity())

    override suspend fun insertAll(entries: List<DeductionEntry>) =
        deductionEntryDao.insertAll(entries.map { it.toEntity() })

    override suspend fun update(entry: DeductionEntry) =
        deductionEntryDao.update(entry.toEntity())

    override suspend fun delete(id: Long) =
        deductionEntryDao.deleteById(id)

    override suspend fun getTotalByCutoff(cutoffPeriodId: Long): Double =
        deductionEntryDao.getTotalByCutoff(cutoffPeriodId) ?: 0.0

    override suspend fun getTotalByEmployeeAndCutoff(employeeId: Long, cutoffPeriodId: Long): Double =
        deductionEntryDao.getTotalByEmployeeAndCutoff(employeeId, cutoffPeriodId) ?: 0.0
}
