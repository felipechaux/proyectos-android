package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestWalletDTO
import co.com.pagatodo.core.data.dto.response.ResponseWalletDTO
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable

interface IWalletRepository {
    fun getWallet(requestWalletDTO: RequestWalletDTO): Observable<ResponseWalletDTO>?
}

class WalletRepository: BaseRepository(),
    IWalletRepository {
    override fun getWallet(requestWalletDTO: RequestWalletDTO): Observable<ResponseWalletDTO>? {
        return ApiFactory.build()?.getWallet(requestWalletDTO)
    }
}