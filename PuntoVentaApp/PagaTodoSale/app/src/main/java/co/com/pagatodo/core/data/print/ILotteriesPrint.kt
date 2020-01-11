package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel

interface ILotteriesPrint {
    fun print(printModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printFractionsAvailable(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}