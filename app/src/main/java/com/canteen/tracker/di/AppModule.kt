package com.canteen.tracker.di

import android.content.Context
import androidx.room.Room
import com.canteen.tracker.data.local.CanteenDatabase
import com.canteen.tracker.data.local.DeductionEntryDao
import com.canteen.tracker.data.local.EmployeeDao
import com.canteen.tracker.data.local.CutoffPeriodDao
import com.canteen.tracker.data.local.ScanSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CanteenDatabase {
        return Room.databaseBuilder(
            context,
            CanteenDatabase::class.java,
            "canteen_tracker.db"
        ).build()
    }

    @Provides
    fun provideEmployeeDao(db: CanteenDatabase): EmployeeDao = db.employeeDao()

    @Provides
    fun provideCutoffPeriodDao(db: CanteenDatabase): CutoffPeriodDao = db.cutoffPeriodDao()

    @Provides
    fun provideDeductionEntryDao(db: CanteenDatabase): DeductionEntryDao = db.deductionEntryDao()

    @Provides
    fun provideScanSessionDao(db: CanteenDatabase): ScanSessionDao = db.scanSessionDao()
}
