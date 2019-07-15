package com.sbpinilla.consultactivity.di

import com.sbpinilla.consultactivity.data.repositories.EmployeeEntityRepository
import com.sbpinilla.consultactivity.data.repositories.IEmployeeEntityRepository
import dagger.Module
import dagger.Provides

@Module
class EmployeeEntityModule {


    @Provides
    fun provideEmployeeEntityRepository(): IEmployeeEntityRepository {
        return EmployeeEntityRepository()
    }


}