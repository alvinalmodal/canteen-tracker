package com.canteen.tracker.data.repository

import com.canteen.tracker.data.local.CutoffPeriodDao
import com.canteen.tracker.domain.model.CutoffPeriod
import com.canteen.tracker.domain.repository.CutoffPeriodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CutoffPeriodRepositoryImpl @Inject constructor(
    private val cutoffPeriodDao: CutoffPeriodDao
) : CutoffPeriodRepository {

    override fun getAll(): Flow<List<CutoffPeriod>> =
        cutoffPeriodDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: Long): CutoffPeriod? =
        cutoffPeriodDao.getById(id)?.toDomain()

    override suspend fun create(cutoffPeriod: CutoffPeriod): Long =
        cutoffPeriodDao.insert(cutoffPeriod.toEntity())
}
