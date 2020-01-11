package co.com.pagatodo.core.di

import co.com.pagatodo.core.views.lotteryResult.QueryViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(QueryModule::class))
interface QueryComponent {
    fun inject(queryViewModel: QueryViewModel)
}