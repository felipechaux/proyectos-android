package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestQueryWinnersDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryWinnersDTO
import io.reactivex.Observable

interface IQueryWinnersRespository {
    fun queryWinners(requestQueryWinnersDTO: RequestQueryWinnersDTO): Observable<ResponseQueryWinnersDTO>?
}