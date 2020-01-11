package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.data.database.DatabaseViewModel
import co.com.pagatodo.core.data.database.entities.ModalityEntityRoom
import co.com.pagatodo.core.data.dto.GeneralProductDTO
import co.com.pagatodo.core.data.repositories.IModalityRepository
import co.com.pagatodo.core.di.DaggerModalityComponent
import co.com.pagatodo.core.di.ModalityModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModalityInteractor {

    @Inject
    lateinit var modalityRepository: IModalityRepository

    init {
        DaggerModalityComponent.builder().modalityModule(ModalityModule()).build().inject(this)
    }

    fun saveModalitiesInLocal(products: List<GeneralProductDTO>?){
        val modalitiesEntity = arrayListOf<ModalityEntityRoom>()
        products?.forEach {
            val productCode = it.code
            val modalities = it.modalities
            modalities?.forEach { modality ->
                val mModality = ModalityEntityRoom(
                    modality.code,
                    productCode.toString(),
                    modality.description
                )
                modalitiesEntity.add(mModality)
            }
        }

        modalityRepository.saveModalitiesInLocalRoom(modalitiesEntity).subscribe {
            DatabaseViewModel.database.onNext(DatabaseViewModel.DatabaseEvents.MODALITIES_ADDED)
        }
    }
}