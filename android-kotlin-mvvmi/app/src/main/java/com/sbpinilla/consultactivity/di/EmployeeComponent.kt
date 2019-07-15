package com.sbpinilla.consultactivity.di

import com.sbpinilla.consultactivity.data.interactors.EmployeeInteractor
import com.sbpinilla.consultactivity.data.repositories.EmployeeEntityRepository
import com.sbpinilla.consultactivity.data.repositories.EmployeeRepository
import com.sbpinilla.consultactivity.view.main.MainViewModel
import dagger.Component

@Component(modules = arrayOf(
    EmployeeModule::class,
    EmployeeEntityModule::class
))
interface EmployeeComponent {
    fun inject(employeeInteractor: EmployeeInteractor)
    fun inject(employeeauthRepository: EmployeeRepository)
    fun inject(employeeauthViewModel: MainViewModel)
}