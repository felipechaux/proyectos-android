package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.RequestRegistraduriaCollectionDTO
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.print.RegistraduriaCollectionPrintModel
import co.com.pagatodo.core.data.print.IRegistraduriaCollectionPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintRegistraduriaModule
import co.com.pagatodo.core.di.repository.DaggerRegistraduriaCollectionRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface IRegistraduriaCollectionRepository {
    fun queryRegistraduriaGetCollection(requestRegistraduriaGetCollectionDTO: RequestRegistraduriaGetCollectionDTO
    ): Observable<ResponseRegistraduriaGetCollectionDTO>?
    fun queryRegistraduriaGetServices(requestRegistraduriaGetServiceDTO: RequestRegistraduriaGetServiceDTO
    ): Observable<ResponseRegistraduriaGetServiceDTO>?
    fun queryRegistraduriaCollection(requestRegistraduriaCollectionDTO: RequestRegistraduriaCollectionDTO
    ): Observable<ResponseRegistraduriaCollectionDTO>?
    fun registraduriaCollectionPrint(printModel: RegistraduriaCollectionPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}

@Singleton
class RegistraduriaCollectionRepository: BaseRepository(), IRegistraduriaCollectionRepository {

    @Inject
    lateinit var registraduriaCollectionPrint: IRegistraduriaCollectionPrint

    init {
        DaggerRegistraduriaCollectionRepositoryComponent
            .builder()
            .printRegistraduriaModule(PrintRegistraduriaModule())
            .build()
            .inject(this)
    }

    override fun queryRegistraduriaGetCollection(requestRegistraduriaGetCollectionDTO: RequestRegistraduriaGetCollectionDTO
    ): Observable<ResponseRegistraduriaGetCollectionDTO>? {
        return ApiFactory.build()?.queryRegistraduriaGetCollection(requestRegistraduriaGetCollectionDTO)
    }

    override fun queryRegistraduriaGetServices(requestRegistraduriaGetServiceDTO: RequestRegistraduriaGetServiceDTO
    ): Observable<ResponseRegistraduriaGetServiceDTO>? {
        return ApiFactory.build()?.queryRegistraduriaGetServices(requestRegistraduriaGetServiceDTO)
    }

    override fun queryRegistraduriaCollection(requestRegistraduriaCollectionDTO: RequestRegistraduriaCollectionDTO
    ): Observable<ResponseRegistraduriaCollectionDTO>? {
        return ApiFactory.build()?.queryRegistraduriaCollection(requestRegistraduriaCollectionDTO)
    }

    override fun registraduriaCollectionPrint(printModel: RegistraduriaCollectionPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        registraduriaCollectionPrint.registraduriaCollectionPrint(printModel, callback)
    }

}