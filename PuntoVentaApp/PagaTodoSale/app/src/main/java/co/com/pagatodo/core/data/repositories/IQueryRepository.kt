package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseLotteryResultDTO
import io.reactivex.Observable

interface IQueryRepository {
    fun getLotteryResult(drawDate: String, lotteryCode: String? = null, number: String? = null): Observable<ResponseLotteryResultDTO>?
    fun checkVirtualLotteryResult(requestCheckResultLotteryDTO: RequestCheckResultLotteryDTO): Observable<ResponseCheckResultLotteryDTO>?
}
