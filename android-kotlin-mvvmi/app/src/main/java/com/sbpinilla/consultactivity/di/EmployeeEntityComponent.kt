package com.sbpinilla.consultactivity.di

import com.sbpinilla.consultactivity.data.repositories.EmployeeEntityRepository
import dagger.Component

@Component(modules = arrayOf(
    EmployeeEntityModule::class
))
interface EmployeeEntityComponent {
    fun inject(employeeEntityRepository: EmployeeEntityRepository)
}