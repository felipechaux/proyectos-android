package com.sbpinilla.consultactivity.di

import com.sbpinilla.consultactivity.data.interactors.EmployeeInteractor
import com.sbpinilla.consultactivity.data.interactors.IEmployeeInteractor
import com.sbpinilla.consultactivity.data.repositories.EmployeeEntityRepository
import com.sbpinilla.consultactivity.data.repositories.EmployeeRepository
import com.sbpinilla.consultactivity.data.repositories.IEmployeeEntityRepository
import com.sbpinilla.consultactivity.data.repositories.IEmployeeRepository
import dagger.Module
import dagger.Provides


@Module
class EmployeeModule {

    @Provides
    fun provideEmployeeInteractor(): IEmployeeInteractor {
        return EmployeeInteractor()
    }

    @Provides
    fun provideEmployeeRepository(): IEmployeeRepository {
        return EmployeeRepository()
    }
}