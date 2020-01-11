package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.PromotionInteractor
import co.com.pagatodo.core.data.repositories.PromotionRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PromotionModule::class))
interface PromotionComponent {
    fun inject(promotionInteractor: PromotionInteractor)
    fun inject(promotionRepository: PromotionRepository)
}