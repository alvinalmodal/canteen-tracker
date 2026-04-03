package com.canteen.tracker.data.repository

import com.canteen.tracker.data.local.EmployeeDao
import com.canteen.tracker.domain.model.Employee
import com.canteen.tracker.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepositoryImpl @Inject constructor(
    private val employeeDao: EmployeeDao
) : EmployeeRepository {

    override fun getAll(): Flow<List<Employee>> =
        employeeDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun findByName(name: String): Employee? =
        employeeDao.findByName(name)?.toDomain()

    override suspend fun getOrCreate(name: String): Employee {
        val existing = employeeDao.findByName(name)
        if (existing != null) return existing.toDomain()
        val id = employeeDao.insert(Employee(name = name).toEntity())
        return Employee(id = id, name = name)
    }
}
