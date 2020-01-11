package co.com.pagatodo.core.data.interactors

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.RaffleEntityRoom
import co.com.pagatodo.core.data.dto.GeneralProductDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.ResponseRaffleAvailableRangeDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IRaffleRepository
import co.com.pagatodo.core.di.DaggerRaffleComponent
import co.com.pagatodo.core.di.RaffleModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RaffleInteractor {

    @Inject
    lateinit var raffleRepository: IRaffleRepository

    init {
        DaggerRaffleComponent
            .builder()
            .raffleModule(RaffleModule())
            .build().inject(this)
    }

    @SuppressLint("CheckResult")
    fun getAllRafflesFromLocal(): Observable<List<RaffleModel>>?{
        return raffleRepository.getRafflesFromLocalRoom()
            ?.flatMap {
                val response = arrayListOf<RaffleModel>()
                it.forEach { raffle ->
                    val raffleModel = RaffleModel().apply {
                        id = raffle.id
                        raffleId = raffle.raffleId
                        raffleName = raffle.raffleName
                        raffleDescription = raffle.descriptionRaffle
                        lotteryName = raffle.lotteryName
                        dateDraw = raffle.dateDraw
                        drawTime = raffle.drawTime
                        price = raffle.price
                        countNumberFigures = raffle.countNumberFigures
                        pricePrize = raffle.pricePrize
                        drawTime = raffle.drawTime
                        logo = raffle.logo
                        messageDescriptionRaffle = raffle.messageRaffle
                    }
                    response.add(raffleModel)
                }
                Observable.just(response)
            }
    }

    fun saveRafflesInLocal(products: List<GeneralProductDTO>?) {
        val productRaffle = products?.filter { it.code.equals(R_string(R.string.code_product_raffle)) }
        if(productRaffle?.isNotEmpty() == true){
            val product = productRaffle.first()
            val raffles = product.raffles
            val rafflesList = arrayListOf<RaffleEntityRoom>()
            raffles?.forEachIndexed { index, raffle ->
                val entity = RaffleEntityRoom(
                    index,
                    raffle.raffleId ?: 0,
                    raffle.raffleName,
                    raffle.descriptionRaffle,
                    raffle.lotteryName,
                    raffle.dateDraw,
                    raffle.price,
                    raffle.countNumberFigures,
                    raffle.prizeValue,
                    raffle.drawTime,
                    raffle.logo,
                    raffle.messageRaffle
                )
                rafflesList.add(entity)
                raffleRepository.saveRaffleInLocal(rafflesList).subscribe()
            }
        }
    }

    fun getRaffleAvailable(raffleId: String, numberRaffle: String): Observable<ResponseRaffleAvailableModel>? {
        val requestRaffleAvailableDTO = convertRequestModelToDTO(RequestRaffleAvailableModel().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            number = numberRaffle
            raffleCode = raffleId
            productCode = R_string(R.string.code_product_raffle)
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
        })
        return raffleRepository
            .getRaffleNumberAvailable(requestRaffleAvailableDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseRaffleAvailableModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    messages = it.messages
                    availability = it.availability
                }
                Observable.just(response)
            }
    }

    fun payRaffle(raffleId: Int, numberSold: String, isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseRaffleRegisterModel>? {
        val requestRaffleSendDTO = convertRequestModelToDTO(RequestRaffleRegisterModel().apply {
            raffleCode = raffleId.toString()
            numberRaffle = numberSold
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.code_product_raffle)
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
        })
        return raffleRepository
            .registerRaffle(requestRaffleSendDTO, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseRaffleRegisterModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    transactionId = it.transactionId
                    message = it.message
                    uniqueSerial = it.uniqueSerial
                    serie1 = it.serie1
                    serie2 = it.serie2
                    currentSerie2 = it.currentSerie2
                    raffleDescription = it.raffleDescription
                }
                Observable.just(response)
            }
    }

    fun getRandomRaffle(raffleId: String): Observable<ResponseRandomRaffleModel>? {
        val requestRaffleRandomNumberDTO = convertRequestModelToDTO(RequestRaffleRandomNumberModel().apply {
            raffleCode = raffleId
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            productCode = R_string(R.string.code_product_raffle)
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
        })
        return raffleRepository
            .getRandomNumberRaffle(requestRaffleRandomNumberDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseRandomRaffleModel().apply {
                    responseCode = it.code
                    isSuccess = it.isSuccess
                    message = it.message
                    transactionDate = it.transactionDate
                    number = it.number
                }
                Observable.just(response)
            }
    }

    fun print(uniqueSerial: String, digitChecked: String, raffleName: String, raffleNumber: String, lotery: String,
              raffleValue: String, rafflePrize: String, dateDraw: String, timeDraw: String, raffleDescription: String, isRetry: Boolean,
              callback: (printerStatus: PrinterStatus) -> Unit?){
        val rafflePrintModel = RafflePrintModel().apply {
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            stub = "${getPreference<String>(R_string(R.string.shared_key_current_serie1))}  ${getPreference<String>(R_string(R.string.shared_key_current_serie2))}"
            this.uniqueSerial = uniqueSerial
            this.digitChecked = digitChecked
            this.raffleName = raffleName
            this.raffleNumber = raffleNumber
            this.lotery = lotery
            this.raffleValue = raffleValue
            this.rafflePrize = rafflePrize
            this.dateDraw = dateDraw
            this.timeDraw = timeDraw
            this.hour = hour
            this.raffleDescription = raffleDescription
        }
        raffleRepository.print(rafflePrintModel, isRetry, callback)
    }

    fun raffleAvailableRange(codRaffle:String, startNum:String, endNum :String): Observable<ResponseRaffleAvailableRangeDTO>?{

        val request = RequestRaffleAvailableRangeDTO().apply {
            this.channelId = getPreference(R_string(R.string.shared_key_canal_id))
            this.raffleCode = codRaffle
            this.productCode = R_string(R.string.code_product_raffle)
            this.sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            this.terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            this.userType = getPreference(R_string(R.string.shared_key_user_type))
            this.startNum = startNum
            this.endNum = endNum


        }

        return raffleRepository
            .raffleAvailableRange(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }




    }

    private fun convertRequestModelToDTO(randomNumberModel: RequestRaffleRandomNumberModel) : RequestRaffleRandomNumberDTO {
        return RequestRaffleRandomNumberDTO().apply {
            this.channelId = randomNumberModel.channelId
            this.terminalCode = randomNumberModel.terminalCode
            this.sellerCode = randomNumberModel.sellerCode
            this.userType = randomNumberModel.userType
            this.productCode = randomNumberModel.productCode
            this.raffleCode = randomNumberModel.raffleCode
        }
    }

    private fun convertRequestModelToDTO(model: RequestRaffleAvailableModel) : RequestRaffleAvailableDTO {
        return RequestRaffleAvailableDTO().apply {
            this.channelId = model.channelId
            this.number = model.number
            this.productCode = model.productCode
            this.raffleCode = model.raffleCode
            this.sellerCode = model.sellerCode
            this.terminalCode = model.terminalCode
            this.userType = model.userType
        }
    }

    private fun convertRequestModelToDTO(model: RequestRaffleRegisterModel) : RequestRaffleSendDTO {
        return RequestRaffleSendDTO().apply {
            this.channelId = model.channelId
            this.numberRaffle = model.numberRaffle
            this.productCode = model.productCode
            this.raffleCode = model.raffleCode
            this.sellerCode = model.sellerCode
            this.serie1 = model.serie1
            this.serie2 = model.serie2
            this.terminalCode = model.terminalCode
            this.userType = model.userType
            this.transactionTime = model.transactionTime
            this.sequenceTransaction = model.sequenceTransaction
        }
    }
}