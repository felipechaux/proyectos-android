package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.request.RequestLotteryResultDTO
import co.com.pagatodo.core.data.dto.response.ResponseCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseLotteryResultDTO
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class QueryRepository: BaseRepository(),
    IQueryRepository {
    override fun getLotteryResult(drawDate: String, lotteryCode: String?, number: String?): Observable<ResponseLotteryResultDTO>? {
        val request = RequestLotteryResultDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            this.drawDate = drawDate
            this.lotteryCode = lotteryCode
            this.number = number
        }
        return ApiFactory.build()?.getLotteryResult(request)
    }

    override fun checkVirtualLotteryResult(requestCheckResultLotteryDTO: RequestCheckResultLotteryDTO): Observable<ResponseCheckResultLotteryDTO>? {
        return ApiFactory.build()?.checkVirtualLotteryResult(requestCheckResultLotteryDTO)
    }
}