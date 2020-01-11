package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.data.database.DatabaseViewModel
import co.com.pagatodo.core.data.database.entities.ModalitiesValuesEntityRoom
import co.com.pagatodo.core.data.database.entities.PromotionalEntityRoom
import co.com.pagatodo.core.data.dto.GeneralProductDTO
import co.com.pagatodo.core.data.repositories.IPromotionRepository
import co.com.pagatodo.core.di.DaggerPromotionComponent
import co.com.pagatodo.core.di.PromotionModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionInteractor {

    @Inject
    lateinit var promotionRepository: IPromotionRepository

    init {
        DaggerPromotionComponent.builder().promotionModule(PromotionModule()).build().inject(this)
    }

    fun savePromotionalInLocal(products: List<GeneralProductDTO>?){
        products?.forEach {
            val promotional = it.promotional
            val promotionalEntity = arrayListOf<PromotionalEntityRoom>()
            promotional?.forEach {promotion ->
                if(promotion.name == "MAXICHANCE") {

                    val promotionalId = promotion.id ?: 0

                    val mPromotion = PromotionalEntityRoom().apply {
                        this.id = promotionalId
                        this.bettingAmount = promotion.bettingAmount
                        this.quantityFigures = promotion.quantityFigures
                        this.lotteriesQuantity = promotion.lotteriesQuantity
                        this.isGenerateRandom = promotion.generateRandom
                        this.name = promotion.name
                        this.openValue = promotion.openValue
                        this.modalitiesValues = convertListStringToEntityRoom(promotionalId.toLong(), promotion.modalitiesValues)
                    }
                    promotionalEntity.add(mPromotion)
                }
            }

            promotionRepository.savePromotionalInLocalRoom(promotionalEntity).subscribe {
                DatabaseViewModel.database.onNext(DatabaseViewModel.DatabaseEvents.PROMOTIONAL_ADDED)
            }
        }
    }

    private fun convertListStringToEntityRoom(promotionalId: Long, modalitiesValues: List<String>?): List<ModalitiesValuesEntityRoom> {
        val response = arrayListOf<ModalitiesValuesEntityRoom>()
        var index: Long = 1
        modalitiesValues?.forEach {
            response.add(
                ModalitiesValuesEntityRoom(index, promotionalId, it)
            )
            index++
        }
        return response
    }
}