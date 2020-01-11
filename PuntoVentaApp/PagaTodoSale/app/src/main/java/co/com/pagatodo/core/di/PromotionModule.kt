package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.PromotionInteractor
import co.com.pagatodo.core.data.repositories.IPromotionRepository
import co.com.pagatodo.core.data.repositories.PromotionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PromotionModule {

    @Singleton
    @Provides
    fun providePromotionRepository(): IPromotionRepository {
        return PromotionRepository()
    }

    @Singleton
    @Provides
    fun providePromotionInteractor(): PromotionInteractor {
        return PromotionInteractor()
    }
}