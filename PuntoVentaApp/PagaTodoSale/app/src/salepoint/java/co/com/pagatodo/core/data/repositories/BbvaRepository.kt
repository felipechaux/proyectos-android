package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.print.BbvaBillPayPrintModel
import co.com.pagatodo.core.data.print.BbvaPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable


interface IBbvaRepository {

    fun accountDeposit(requestBbvaDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>?
    fun billPayment(requestBbvaBillPaymentDTO: RequestBbvaBillPaymentDTO): Observable<ResponseBbvaBillPaymentDTO>?
    fun paymentObligations(requestBbvaDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>?
    fun withdrawalAndBalance(requestBbvaDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>?
    fun reprintTransaction(requestBbvaReprintTransactionDTO: RequestBbvaReprintTransactionDTO): Observable<ResponseBbvaReprintTransactionDTO>?
    fun closeBox(requestBbvaCloseBoxDTO: RequestBbvaCloseBoxDTO): Observable<ResponseBbvaCloseBoxDTO>?
    fun getCities(requestQueryCitiesDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>?
    fun getParameters(requestQueryParameterDTO: RequestQueryParameterDTO): Observable<ResponseQueryParameterDTO>?
    fun validateBill(requestBbvaValidateAndPayBillDTO: RequestBbvaValidateAndPayBillDTO): Observable<ResponseBbvaValidateBillDTO>?
    fun print(bbvaBillPayPrintModel: BbvaBillPayPrintModel, callback: (printerStatus: PrinterStatus)-> Unit?)

}

class BbvaRepository : IBbvaRepository {

    override fun accountDeposit(requestBbvaDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>? {
        return ApiFactory.build()?.accountDeposit(requestBbvaDTO)
    }

    override fun billPayment(requestBbvaBillPaymentDTO: RequestBbvaBillPaymentDTO): Observable<ResponseBbvaBillPaymentDTO>? {
        return ApiFactory.build()?.billPayment(requestBbvaBillPaymentDTO)
    }

    override fun paymentObligations(requestBbvaDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>? {
        return ApiFactory.build()?.paymentObligations(requestBbvaDTO)
    }

    override fun withdrawalAndBalance(requestBbvaDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>? {
        return ApiFactory.build()?.withdrawalAndBalance(requestBbvaDTO)
    }

    override fun reprintTransaction(requestBbvaReprintTransactionDTO: RequestBbvaReprintTransactionDTO): Observable<ResponseBbvaReprintTransactionDTO>? {
        return ApiFactory.build()?.reprintTransaction(requestBbvaReprintTransactionDTO)
    }

    override fun closeBox(requestBbvaCloseBoxDTO: RequestBbvaCloseBoxDTO): Observable<ResponseBbvaCloseBoxDTO>? {
        return ApiFactory.build()?.closeBox(requestBbvaCloseBoxDTO)
    }

    override fun getCities(requestQueryCitiesDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>? {
        return ApiFactory.build()?.queryCities(requestQueryCitiesDTO)
    }

    override fun getParameters(requestQueryParameterDTO: RequestQueryParameterDTO): Observable<ResponseQueryParameterDTO>? {
        return ApiFactory.build()?.queryParameters(requestQueryParameterDTO)
    }

    override fun validateBill(requestBbvaValidateAndPayBillDTO: RequestBbvaValidateAndPayBillDTO): Observable<ResponseBbvaValidateBillDTO>? {
        return ApiFactory.build()?.validateBill(requestBbvaValidateAndPayBillDTO)
    }

    override fun print(
        bbvaBillPayPrintModel: BbvaBillPayPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        BbvaPrint().print(bbvaBillPayPrintModel,callback)
    }

}