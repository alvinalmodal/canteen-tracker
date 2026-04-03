package com.canteen.tracker.data.repository

import com.canteen.tracker.data.local.ScanSessionDao
import com.canteen.tracker.domain.model.ScanSession
import com.canteen.tracker.domain.repository.ScanSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanSessionRepositoryImpl @Inject constructor(
    private val scanSessionDao: ScanSessionDao
) : ScanSessionRepository {

    override fun getAll(): Flow<List<ScanSession>> =
        scanSessionDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getByCutoffPeriod(cutoffPeriodId: Long): Flow<List<ScanSession>> =
        scanSessionDao.getByCutoffPeriod(cutoffPeriodId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun create(scanSession: ScanSession): Long =
        scanSessionDao.insert(scanSession.toEntity())
}
