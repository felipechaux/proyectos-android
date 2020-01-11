package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.*
import co.com.pagatodo.core.data.dto.request.RequestRechargeDTO
import co.com.pagatodo.core.data.dto.request.RequestRechargeHistoryDTO
import co.com.pagatodo.core.data.dto.response.ResponseRechargeDTO
import co.com.pagatodo.core.data.dto.response.ResponseRechargeHistoryDTO
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IRechargeRepository {
    fun getOperatorsForRechargeRoom(): Observable<List<OperatorEntityRoom>>?
    fun getProductParametersRooms(): Observable<List<ProductParameterEntityRoom>>?
    fun saveOperatorsRoom(operatorsEntity: List<OperatorEntityRoom>): Observable<DBHelperResponse>?
    fun dispatchRecharge(requestRechargeDTO: RequestRechargeDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseRechargeDTO>?
    fun getRechargeHistory(requestRechargeHistoryDTO: RequestRechargeHistoryDTO): Observable<ResponseRechargeHistoryDTO>?
    fun printRecharge(stub: String, billNumber: String, transactionDate: String, transactionHour: String, number:String, value:String, billPrefix: String, billMessage: String, isRetry: Boolean,headerGelsa:String, decriptionPackage:String,operationName:String, callback: (printerStatus: PrinterStatus) -> Unit?)
}