package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.SportingBetDTO
import co.com.pagatodo.core.data.dto.SportingEventDTO
import co.com.pagatodo.core.data.dto.SportingProductDTO
import co.com.pagatodo.core.data.dto.request.RequestSellSportingBetDTO
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.data.model.SportingEventModel
import co.com.pagatodo.core.data.model.SportingProductModel
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.data.model.request.RequestSellSportingBetModel
import co.com.pagatodo.core.data.model.response.ResponseSellSportingBetModel
import co.com.pagatodo.core.data.model.response.ResponseSportingModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.ISportingRepository
import co.com.pagatodo.core.di.DaggerSportingComponent
import co.com.pagatodo.core.di.SportingModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportingInteractor {

    @Inject
    lateinit var sportingRepository: ISportingRepository

    init {
        DaggerSportingComponent
            .builder()
            .sportingModule(SportingModule())
            .build().inject(this)
    }

    fun getProductParameters(): Observable<List<KeyValueModel>>?{
        return sportingRepository
            .getProductParametersForSportingsRoom()
            ?.flatMap {
                val response = mutableListOf<KeyValueModel>()
                it.forEach { ent ->
                    val model = KeyValueModel().apply {
                        key = ent.key
                        value = ent.value
                    }
                    response.add(model)
                }
                Observable.just(response)
            }
    }

    fun getParameters(): Observable<ResponseSportingModel>? {
        return sportingRepository
            .getParameters()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseSportingModel().apply {
                    serie1 = it.serie1
                    serie2 = it.serie2
                    idExternalProduct = it.idExternalProduct
                    resolution = it.resolution
                    productName = it.productName
                    version = it.version
                    products = convertProductsDTOToModel(it.products)
                    prizePlan = it.prizePlan
                    megaGoalMaxBet = it.megaGoalMaxBet
                }
                Observable.just(response)
            }
    }

    fun sellSportingBet(requestBetModel: RequestSellSportingBetModel, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseSellSportingBetModel>? {
        val request = RequestSellSportingBetDTO().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            productCode = R_string(R.string.code_sporting)
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType =  getPreference(R_string(R.string.shared_key_user_type))
            serie = getPreference(R_string(R.string.shared_key_current_serie1))
            consecutive = getPreference(R_string(R.string.shared_key_current_serie2))
            value = requestBetModel.value
            transactionTime = requestBetModel.transactionTime
            sequenceTransaction = requestBetModel.sequenceTransaction
            event = SportingEventDTO().apply {
                eventId = requestBetModel.event?.id
                bets = convertBetsModelToDTO(requestBetModel.event?.bets)
            }
            product = SportingProductDTO().apply {
                productId = requestBetModel.product?.id
            }
        }

        return sportingRepository
            .sellSportingBet(request, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseSellSportingBetModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    transactionId = it.transactionId
                    messages = it.messages
                    serie1 = it.serie1
                    consecutive = it.consecutive
                    date = it.date
                    hour = it.hour
                    verificationCode = it.verificationCode
                    totalValue = it.totalValue
                    currentSerie2 = it.currentSerie2
                    valueIva = it.ValueIva
                    valueNeto = it.ValueNeto
                }
                Observable.just(response)
            }
    }


    private fun convertProductsDTOToModel(productDTO: List<SportingProductDTO>?): List<SportingProductModel> {
        val listProductModel = arrayListOf<SportingProductModel>()
        productDTO?.forEach{
            listProductModel.add(SportingProductModel().apply {
                productId = it.productId
                productCode = it.productCode
                productName = it.productName
                iva = it.iva
                events = convertEventsDTOTOModel(it.events)
            })
        }

        return listProductModel.toList()
    }

    private fun convertEventsDTOTOModel(eventsDTO: List<SportingEventDTO>?): List<SportingEventModel> {
        val listEventsModel = arrayListOf<SportingEventModel>()
        eventsDTO?.forEach {
            listEventsModel.add(SportingEventModel().apply {
                eventId = it.eventId
                code = it.code
                name = it.name
                betValue = it.betValue
                multipleVale = it.multipleVale
                closeDate = it.closeDate
                closeTime = it.closeTime
                bets = convertBetDTOToModel(it.bets)
                accumulated = it.accumulated
            })
        }
        return listEventsModel
    }

    private fun convertBetDTOToModel(bestsDTO: List<SportingBetDTO>?): List<SportingBetModel> {
        val listBetsModel = arrayListOf<SportingBetModel>()
        bestsDTO?.forEach {
            listBetsModel.add(SportingBetModel().apply {
                betId = it.betId
                code = it.code
                local = it.local
                localName = it.localName
                visitor = it.visitor
                visitorName = it.visitorName
                date = it.date.toString()
                time = it.time
                isLocalResult = it.isLocalResult
                isVisitorResult = it.isVisitorResult
                isTieResult = it.isTieResult
                localMarker = it.localMarker
                visitorMarker = it.visitorMarker
            })
        }
        return listBetsModel
    }

    private fun convertBetsModelToDTO(betsModel: List<SportingBetModel>?): List<SportingBetDTO>{
        val betsDTOArray = arrayListOf<SportingBetDTO>()
        betsModel?.forEach {
            betsDTOArray.add(SportingBetDTO().apply {
                betId = it.betId
                code = it.code
                local = it.local
                localName = it.localName
                visitor = it.visitor
                visitorName = it.visitorName
                date = it.date?.toLong()
                time = it.time
                isLocalResult = it.isLocalResult
                isVisitorResult = it.isVisitorResult
                isTieResult = it.isTieResult
                localMarker = it.localMarker
                visitorMarker = it.visitorMarker
            })
        }
        return betsDTOArray
    }

    fun print(sportinModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        sportingRepository.print(sportinModel, isRetry, callback)
    }
}