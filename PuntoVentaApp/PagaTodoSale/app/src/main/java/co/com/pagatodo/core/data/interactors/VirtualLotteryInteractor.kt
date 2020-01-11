package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductLotteriesEntityRoom
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.NumberDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.ResponseLotteryNumberDTO
import co.com.pagatodo.core.data.model.LotteryDetailModel
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.data.model.response.ResponsePayPhysicalLotteryModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IVirtualLotteryRepository
import co.com.pagatodo.core.di.DaggerVirtualLotteryComponent
import co.com.pagatodo.core.di.VirtualLotteryModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualLotteryInteractor {

    @Inject
    lateinit var virtualLotteryRepository: IVirtualLotteryRepository

    init {
        DaggerVirtualLotteryComponent.builder().virtualLotteryModule(VirtualLotteryModule()).build().inject(this)
    }

    fun checkPhysicalLotteryNumber(number: String, lotteryCode: String): Observable<ResponseCheckNumberLotteryModel>? {
        val productCode = R_string(R.string.name_code_product_physical_lottery)
        val transactionSequence = R_string(R.string.transaction_sequences_24).toInt()
        return virtualLotteryRepository
            .checkPhysicalLotteryNumber(createRequestCheckNumberLotteryDTO(number, lotteryCode, transactionSequence, productCode))
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val model = ResponseCheckNumberLotteryModel().apply {
                    this.responseCode = it.responseCode
                    this.isSuccess = it.isSuccess?:false
                    this.transactionDate = it.transactionDate
                    this.transactionTime = it.transactionHour
                    this.message = it.message
                    this.tickets = convertListLotteryNumberDtoToModel(it.tickets)
                }
                Observable.just(model)
            }
    }

    fun checkVirtualLotteryNumber(number: String, lotteryCode: String): Observable<ResponseCheckNumberLotteryModel>? {
        val productCode = R_string(R.string.name_code_product_virtual_lottery)
        val transactionSequence = R_string(R.string.transaction_sequences_7).toInt()
        return virtualLotteryRepository
            .checkVirtualLotteryNumber(createRequestCheckNumberLotteryDTO(number, lotteryCode, transactionSequence, productCode))
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val model = ResponseCheckNumberLotteryModel().apply {
                    this.responseCode = it.responseCode
                    this.isSuccess = it.isSuccess?:false
                    this.transactionDate = it.transactionDate
                    this.transactionTime = it.transactionHour
                    this.message = it.message
                    this.numbers = convertListLotteryNumberDtoToModel(it.numbers)
                }
                Observable.just(model)
            }
    }

    private fun convertListLotteryNumberDtoToModel(tickets: List<ResponseLotteryNumberDTO>?): List<LotteryNumberModel> {
        val listTickets = mutableListOf<LotteryNumberModel>()
        tickets?.forEach {
            val model = LotteryNumberModel().apply {
                this.number = it.number
                this.fraction = it.fraction
                this.serie = it.serie
                this.barcode = it.barcode
                this.lottery = convertLotteryDtoToModel(it.lottery)
            }
            listTickets.add(model)
        }
        return listTickets
    }

    private fun convertLotteryDtoToModel(dto: LotteryDTO?): LotteryDetailModel {
        return LotteryDetailModel().apply {
            code = dto?.code
            name = dto?.name
            date = dto?.date
            hour = dto?.hour
            fractions = dto?.fractions
            fractionValue = dto?.fractionValue
            draw = dto?.draw
            fullName = dto?.fullName
        }
    }

    private fun createRequestCheckNumberLotteryDTO(number: String, lotteryCode: String, transactionSequence: Int, productCode: String): RequestCheckNumberLotteryDTO {
        return RequestCheckNumberLotteryDTO().apply {
            userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = productCode
            canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            transactionTime = System.currentTimeMillis()
            this.transactionSequence = transactionSequence
            this.number = createLotteryNumberDTO(number, lotteryCode)
        }
    }

    private fun createLotteryNumberDTO(number: String, lotteryCode: String): LotteryNumberDTO {
        val objectNumber = LotteryNumberDTO().apply {
            this.lottery = LotteryDTO().apply {
                code = lotteryCode
            }
        }
        if(number != "0"){
            objectNumber.apply {
                this.number = number
            }
        }
        return objectNumber
    }

    fun getVirtualLotteries(productCode: String): Observable<List<VirtualLotteryModel>>? {
        return virtualLotteryRepository
            .getLotteriesRoom(productCode)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = convertVirtualLotteriesModelFromEntityRom(it)
                Observable.just(response)
            }
    }

    private fun convertVirtualLotteriesModelFromEntityRom(list: List<ProductLotteriesEntityRoom>): List<VirtualLotteryModel> {
        val response = mutableListOf<VirtualLotteryModel>()
        list.forEach {
            val lotteryModel = VirtualLotteryModel().apply {
                this.lotteryCode = it.lotteryCode
                this.fullName = it.fullName
                this.shortName = it.shortName
                this.date = it.date
                this.hour = it.hour
                this.fractions = it.fractions
                this.fractionValue = it.fractionValue?.toDouble()
                this.draw = it.draw
                this.award = it.award?.toDouble()
            }
            response.add(lotteryModel)
        }
        return response
    }

    fun payPhysicalLottery(value: String, number: String, fractions: String, serie: String, lotteryCode: String, draw: String,
                           isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponsePayPhysicalLotteryModel>? {
        val productCode = R_string(R.string.name_code_product_physical_lottery)
        val request = createRequestLotteryResultDTO(value, number, fractions, serie, lotteryCode, draw, productCode, false, transactionTime, sequenceTransaction)

        return virtualLotteryRepository
            .payPhysicalLottery(request, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val model = ResponsePayPhysicalLotteryModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess?:false
                    transactionDate = it.transactionDate
                    this.transactionTime = it.transactionHour
                    transactionDateDrawDate = it.numbers?.first()?.lottery?.date
                    transactionTimeDrawHour = it.numbers?.first()?.lottery?.hour
                    responseTransactionId = it.transactionId
                    message = it.message
                    checkDigit = it.checkDigit
                    serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
                    currentSerie2 = it.currentSerie2
                }
                Observable.just(model)
            }
    }

    fun payVirtualLottery(value: String, number: String, fractions: String, serie: String, lotteryCode: String, draw: String,
                          isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponsePayPhysicalLotteryModel>? {
        val productCode = R_string(R.string.name_code_product_virtual_lottery)
        val request = createRequestLotteryResultDTO(value, number, fractions, serie, lotteryCode, draw,
             productCode, true, transactionTime, sequenceTransaction)

        return virtualLotteryRepository
            .payVirtualLottery(request, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val model = ResponsePayPhysicalLotteryModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess?:false
                    this.transactionDate = it.transactionDate
                    this.transactionTime = it.transactionHour
                    transactionDateDrawDate = it.numbers?.first()?.lottery?.date
                    transactionTimeDrawHour = it.numbers?.first()?.lottery?.hour
                    responseTransactionId = it.transactionId
                    message = it.message
                    checkDigit = it.checkDigit
                    serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
                    currentSerie2 = it.currentSerie2
                    numbers = convertNumbersDtoToModel(it.numbers)
                }
                Observable.just(model)
            }
    }

    fun loadRandomVirtualLottery(codLottery: String): Observable<ResponseCheckNumberLotteryModel>? {
        return virtualLotteryRepository
            .generateRandomVirtualLottery(createResponseRandomVirtualLotteryDTO(codLottery))
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val model = ResponseCheckNumberLotteryModel().apply {
                    this.responseCode = it.responseCode
                    this.isSuccess = it.isSuccess?:false
                    this.transactionDate = it.transactionDate
                    this.transactionTime = it.transactionHour
                    this.message = it.message
                    this.number = convertNumberDtoToModel(it.number)
                }
                Observable.just(model)
            }
    }

    private fun convertNumbersDtoToModel(numbersDTO: List<NumberDTO>?): List<LotteryNumberModel> {
        val numbers = mutableListOf<LotteryNumberModel>()
        numbersDTO?.forEach {
            numbers.add(LotteryNumberModel().apply {
                number = it.number
                fraction = it.fraction
                serie = it.serie
                serie1 = it.serie1
                serie2 = it.serie2
                lottery = LotteryDetailModel().apply {
                    code = it.lottery?.code
                    name = it.lottery?.name
                    date = it.lottery?.date
                    hour = it.lottery?.hour
                    draw = it.lottery?.draw
                    fullName = it.lottery?.fullName
                }
            })
        }
        return numbers
    }

    private fun convertNumberDtoToModel(numberDTO: NumberDTO?): LotteryNumberModel {
        return LotteryNumberModel().apply {
            number = numberDTO?.number
            fraction = numberDTO?.fraction
            fractions = numberDTO?.lottery?.fractions
            serie = numberDTO?.serie
            lottery = LotteryDetailModel().apply {
                code = numberDTO?.lottery?.code
                fractionValue = numberDTO?.lottery?.fractionValue
                draw = numberDTO?.lottery?.draw
            }
        }
    }

    private fun createResponseRandomVirtualLotteryDTO(codLottery: String): RequestRandomVirtualLotteryDTO {
        return RequestRandomVirtualLotteryDTO().apply {
            number = createNumberDTO(codLottery)
            userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.name_code_product_virtual_lottery)
            transactionTime = System.currentTimeMillis()
            transactionSequence = R_string(R.string.transaction_sequences_7).toInt()
        }
    }

    private fun createNumberDTO(codeLottery: String): NumberDTO {
        return NumberDTO().apply {
            lottery = LotteryDTO().apply {
                this.code = codeLottery
            }
        }
    }

    private fun createRequestLotteryResultDTO(value: String, number: String, fractions: String, serie: String, lotteryCode: String,
                                              draw: String, productCode: String, isVirtual: Boolean, transactionTime: Long?,
                                              sequenceTransaction: Int?): RequestLotteryResultOperationDTO {
        return RequestLotteryResultOperationDTO().apply {
            userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = productCode
            canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.transactionTime = transactionTime
            this.transactionSequence = sequenceTransaction
            if(isVirtual) {
                serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
                serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            }
            this.value = value
            this.number = RequestLotteryNumberDTO().apply {
                this.number = number
                this.fractions = fractions
                this.serie = serie
                this.lottery = LotteryDTO().apply {
                    this.code = lotteryCode
                    this.draw = draw
                }
            }
        }
    }

    fun print(printModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        printModel.stub = "${getPreference<String>(R_string(R.string.shared_key_current_serie1))}   ${getPreference<String>(R_string(R.string.shared_key_current_serie2))}"
        printModel.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
        virtualLotteryRepository.print(printModel, isRetry, callback)
    }

    fun printFractionsAvailables(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        virtualLotteryRepository.printFractionsAvailables(printModel, model, isRetry, callback)
    }
}