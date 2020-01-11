package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestQueryWinnersDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryWinnersDTO
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class QueryWinnersRepository: IQueryWinnersRespository {

    override fun queryWinners(requestQueryWinnersDTO: RequestQueryWinnersDTO): Observable<ResponseQueryWinnersDTO>? {
        return ApiFactory.build()?.queryWinners(requestQueryWinnersDTO)
    }

}