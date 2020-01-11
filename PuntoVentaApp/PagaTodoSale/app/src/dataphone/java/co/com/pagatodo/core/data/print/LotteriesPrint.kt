package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil

class LotteriesPrint: BasePrint(), ILotteriesPrint {
    override fun printFractionsAvailable(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> LotteriesPrintQ2().printFractions(printModel , model, isRetry , callback )
            //DatafonoType.NEW9220.type -> LotteriesPrintNEW9220().printFractions(printModel , isRetry , callback )
        }
    }

    override fun print(printModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> LotteriesPrintQ2().print(printModel , isRetry , callback )
            DatafonoType.NEW9220.type -> LotteriesPrintNEW9220().print(printModel , isRetry , callback )
        }

    }

}

