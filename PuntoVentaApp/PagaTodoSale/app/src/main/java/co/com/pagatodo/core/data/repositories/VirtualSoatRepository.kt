package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestIssuePolicyDTO
import co.com.pagatodo.core.data.dto.request.RequestPolicyConfirmDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryVirtualSoatDTO
import co.com.pagatodo.core.data.dto.response.BaseResponseDTO
import co.com.pagatodo.core.data.dto.response.ResponseIssuePolicyDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryVirtualSoatDTO
import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.data.print.IVirtualSoatPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintVirtualSoatModule
import co.com.pagatodo.core.di.repository.DaggerVirtualSoatRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface IVirtualSoatRepository {
    fun getQueryVirtualSoat(requestQueryVirtualSoatDTO: RequestQueryVirtualSoatDTO): Observable<ResponseQueryVirtualSoatDTO>?
    fun getIssuePolicy(requestIssuePolicyDTO: RequestIssuePolicyDTO): Observable<ResponseIssuePolicyDTO>?
    fun policyConfirm(requestPolicyConfirmDTO: RequestPolicyConfirmDTO): Observable<BaseResponseDTO>?
    fun printSoat(virtualSoatPrintModel: VirtualSoatPrintModel,callback: (printerStatus: PrinterStatus) -> Unit?)

}

@Singleton
class VirtualSoatRepository : IVirtualSoatRepository {


    @Inject
    lateinit var soatPrint: IVirtualSoatPrint

    init {
        DaggerVirtualSoatRepositoryComponent
            .builder()
            .printVirtualSoatModule(PrintVirtualSoatModule())
            .build()
            .inject(this)
    }

    override fun getQueryVirtualSoat(requestQueryVirtualSoatDTO: RequestQueryVirtualSoatDTO): Observable<ResponseQueryVirtualSoatDTO>? {
        return ApiFactory.build()?.queryVirtualSoat(requestQueryVirtualSoatDTO)
    }

    override fun getIssuePolicy(requestIssuePolicyDTO: RequestIssuePolicyDTO): Observable<ResponseIssuePolicyDTO>? {
        return ApiFactory.build()?.issuePolicy(requestIssuePolicyDTO)
    }

    override fun policyConfirm(requestPolicyConfirmDTO: RequestPolicyConfirmDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build()?.policyConfirm(requestPolicyConfirmDTO)
    }

    override fun printSoat(virtualSoatPrintModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {

        soatPrint.print(virtualSoatPrintModel,callback)
    }

}