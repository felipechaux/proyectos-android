package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.OperatorEntityRoom
import co.com.pagatodo.core.data.database.entities.PackageEntityRoom
import co.com.pagatodo.core.data.dto.OperatorDTO
import co.com.pagatodo.core.data.dto.PackageDTO
import co.com.pagatodo.core.data.dto.RechargeDTO
import co.com.pagatodo.core.data.dto.RechargeHistoryDTO
import co.com.pagatodo.core.data.dto.request.RequestRechargeDTO
import co.com.pagatodo.core.data.dto.request.RequestRechargeHistoryDTO
import co.com.pagatodo.core.data.dto.response.RechargeInvoiceDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.request.RequestRechargeHistoryModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeHistoryModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IRechargeRepository
import co.com.pagatodo.core.di.DaggerRechargeComponent
import co.com.pagatodo.core.di.RechargeModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RechargeInteractor {

    @Inject
    lateinit var rechargeRepository: IRechargeRepository

    init {
        DaggerRechargeComponent
            .builder()
            .rechargeModule(RechargeModule())
            .build().inject(this)
    }

    fun getOperatorsForRechargeRoom(): Observable<List<OperatorModel>>? {
        return rechargeRepository
            .getOperatorsForRechargeRoom()
            ?.flatMap {
                val response = mutableListOf<OperatorModel>()
                for (ent in it) {
                    val operatorModel = OperatorModel().apply {
                        operator = ent.operator
                        operatorName = ent.operatorName
                        operatorCode = ent.operatorCode
                        operatorCodePackage = ent.operatorCodePackage
                        packages = convertPackageEntityRoomToModel(ent.packages)
                        minValue = ent.minValue
                        maxValue = ent.maxValue
                        maxDigist = ent.maxDigits
                        minDigits = ent.minDigits
                        multipleDigits = ent.multipleDigits
                        packages = convertPackageEntityRoomToModel(ent.packages)
                    }
                    response.add(operatorModel)
                }
                Observable.just(response)
            }
    }

    fun getProductParametersRoom(): Observable<List<KeyValueModel>>? {
        return rechargeRepository
            .getProductParametersRooms()
            ?.flatMap {
                val response = mutableListOf<KeyValueModel>()
                it.forEach {ent ->
                    val model = KeyValueModel().apply {
                        key = ent.key
                        value = ent.value
                    }
                    response.add(model)
                }
                Observable.just(response)
            }
    }

    private fun convertPackageEntityRoomToModel(packagesEntity: List<PackageEntityRoom>?): List<PackageModel> {
        val packagesModel = mutableListOf<PackageModel>()
        var index: Long = 1
        packagesEntity?.forEach {
            val packagge = PackageModel().apply {
                packageCode = it.packageCode
                packageName = it.packageName
                packageValue = it.packageValue
            }
            packagesModel.add(packagge)
            index++
        }
        return packagesModel
    }

    fun saveOperatorsInLocal(operatorsDTO: List<OperatorDTO>?) {
        val operatorsEntities = mutableListOf<OperatorEntityRoom>()
        var index: Long  = 1
        operatorsDTO?.forEach {
            val operator = OperatorEntityRoom().apply {
                id = index
                operator = it.operator
                operatorName = it.operatorName
                operatorCode = it.operatorCode
                operatorCodePackage = it.operatorCodePackage
                minValue = it.minValue
                maxValue = it.maxValue
                maxDigits = it.maxDigits
                minDigits = it.minDigits
                multipleDigits = it.multipleDigits
                packages = convertListDTOToEntityRoom(index, it.packages)
            }
            operatorsEntities.add(operator)
            index++
        }

        rechargeRepository.saveOperatorsRoom(operatorsEntities)?.subscribe()
    }

    private fun convertListDTOToEntityRoom(operatorId: Long, packagesDto: List<PackageDTO>?): List<PackageEntityRoom> {
        val packages = arrayListOf<PackageEntityRoom>()
        var index: Long = 1
        packagesDto?.forEach { dto ->
            val entity = PackageEntityRoom(
                index,
                operatorId,
                dto.packageCode,
                dto.packageName,
                dto.packageValue
            )
            packages.add(entity)
            index++
        }
        return packages
    }

    fun dispatchRecharge(requestRechargeModel: RequestRechargeModel?, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseRechargeModel>? {
        val requestRechargeDTO: RequestRechargeDTO = createRequestRechargeDTO(requestRechargeModel)
        return rechargeRepository
            .dispatchRecharge(requestRechargeDTO, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val responseModel = ResponseRechargeModel().apply {
                    responseCode = it.responseCode
                    success = it.success
                    transactionAnswerId = it.transactionAnswerId
                    message = it.message
                    serie1 = it.recharge?.serie1
                    currentSerie2 = it.recharge?.currentSerie2
                    recharge = createRechargeModel(it.recharge)
                }
                Observable.just(responseModel)
            }
    }

    private fun createRechargeModel(recharge: RechargeInvoiceDTO?): RechargeModel {
        return RechargeModel().apply {
            serie1 = recharge?.serie1
            serie2 = recharge?.serie2
            currentSerie2 = recharge?.currentSerie2
            number = recharge?.number
            operatorCode = recharge?.operatorCode
            packageCode = recharge?.packageCode
            authNumber = recharge?.authNumber
            idTerminal = recharge?.idTerminal
            hourRecharge = recharge?.hourRecharge
            dateRecharge = recharge?.dateRecharge
            billPrefix = recharge?.billPrefix
            currentBill = recharge?.currentBill
            billMessage = recharge?.billMessage
        }
    }

    private fun createRequestRechargeDTO(requestRechargeModel: RequestRechargeModel?): RequestRechargeDTO {
        return RequestRechargeDTO().apply {
            rechargeDTO = convertRechargeModelToDTO(requestRechargeModel)
            value = requestRechargeModel?.rechargeAmount
            userType = getPreference(R_string(R.string.shared_key_user_type))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.code_recharge)
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            transactionTime = requestRechargeModel?.transactionTime
            transactionSequence = requestRechargeModel?.sequenceTransaction
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
        }
    }

    private fun convertRechargeModelToDTO(modelRequest: RequestRechargeModel?): RechargeDTO {
        return RechargeDTO().apply {
            number = modelRequest?.number
            operatorCode = modelRequest?.operatorCode
            rechargeAmount = modelRequest?.rechargeAmount
            pakageCode = modelRequest?.codePackage
        }
    }

    fun getRechargeHistory(model: RequestRechargeHistoryModel): Observable<ResponseRechargeHistoryModel>? {
        val dto = convertRequestRechargeHistoryModelToDTO(model)
        return rechargeRepository
            .getRechargeHistory(dto)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseRechargeHistoryModel().apply {
                    responseCode = it.responseCode
                    success = it.isSuccess?:false
                    transactionDate = it.transactionDate
                    transactionTime = it.transactionHour
                    message = it.message?:""
                    recharges = convertListDTOToModel(it.recharges)
                }
                Observable.just(response)
            }
    }

    private fun convertRequestRechargeHistoryModelToDTO(model: RequestRechargeHistoryModel): RequestRechargeHistoryDTO {
        return RequestRechargeHistoryDTO().apply {
            number = model.number
            date = model.date
            userType = model.userType
            terminalCode = model.terminalCode
            canalId = model.canalId
            sellerCode = model.sellerCode
            productCode = model.productCode
        }
    }

    private fun convertListDTOToModel(rechargesDto: List<RechargeHistoryDTO>?): List<RechargeHistoryModel>? {
        val recharges = mutableListOf<RechargeHistoryModel>()
        rechargesDto?.forEach { dto ->
            val model = RechargeHistoryModel().apply {
                responseCode = dto.responseCode
                transactionId = dto.transactionId
                companyId = dto.companyId
                number = dto.number
                operatorCode = dto.operatorCode
                state = dto.state
                rechargeAmount = dto.rechargeAmount
                autorizationNumber = dto.autorizationNumber
                hourRecharge = dto.hourRecharge
                dateRecharge = dto.dateRecharge
                serie1 = dto.serie1
                serie2 = dto.serie2
            }
            recharges.add(model)
        }
        return recharges
    }

    fun printRecharge(stub: String, billNumber: String, transactionDate: String, transactionHour: String, number:String, value:String, billPrefix: String, billMessage: String, isRetry: Boolean, headerGelsa:String, decriptionPackage:String,operationName :String , callback: (printerStatus: PrinterStatus) -> Unit?){
        rechargeRepository.printRecharge(stub, billNumber, transactionDate, transactionHour, number, value, billPrefix, billMessage, isRetry,headerGelsa, decriptionPackage,operationName, callback)
    }
}