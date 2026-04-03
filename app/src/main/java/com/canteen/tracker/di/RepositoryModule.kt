package com.canteen.tracker.di

import com.canteen.tracker.data.repository.CutoffPeriodRepositoryImpl
import com.canteen.tracker.data.repository.DeductionRepositoryImpl
import com.canteen.tracker.data.repository.EmployeeRepositoryImpl
import com.canteen.tracker.data.repository.ScanSessionRepositoryImpl
import com.canteen.tracker.domain.repository.CutoffPeriodRepository
import com.canteen.tracker.domain.repository.DeductionRepository
import com.canteen.tracker.domain.repository.EmployeeRepository
import com.canteen.tracker.domain.repository.ScanSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindEmployeeRepository(impl: EmployeeRepositoryImpl): EmployeeRepository

    @Binds
    abstract fun bindCutoffPeriodRepository(impl: CutoffPeriodRepositoryImpl): CutoffPeriodRepository

    @Binds
    abstract fun bindDeductionRepository(impl: DeductionRepositoryImpl): DeductionRepository

    @Binds
    abstract fun bindScanSessionRepository(impl: ScanSessionRepositoryImpl): ScanSessionRepository
}
