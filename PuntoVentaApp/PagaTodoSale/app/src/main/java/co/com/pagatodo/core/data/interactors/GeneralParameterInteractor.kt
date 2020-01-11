package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.data.dto.GeneralParametersDTO
import co.com.pagatodo.core.data.dto.response.ResponsePaymillionaireParametersDTO
import co.com.pagatodo.core.data.repositories.generalparameters.IGeneralParametersRepository
import co.com.pagatodo.core.di.DaggerGeneralParameterComponent
import co.com.pagatodo.core.di.GeneralParameterModule
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralParameterInteractor {

    @Inject
    lateinit var generalParamRepository: IGeneralParametersRepository

    init {
        DaggerGeneralParameterComponent.builder().generalParameterModule(GeneralParameterModule()).build().inject(this)
    }

    fun getGeneralParameters(): Observable<GeneralParametersDTO>? {
        return generalParamRepository.getGeneralParameters()
    }

    fun getPaymillionaireParameters(): Observable<ResponsePaymillionaireParametersDTO>? {
        return generalParamRepository.getPayMillonaireParameters()
    }
}