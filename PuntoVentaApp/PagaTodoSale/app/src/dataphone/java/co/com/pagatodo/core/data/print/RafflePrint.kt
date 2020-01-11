package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil

class RafflePrint: BasePrint(), IRafflePrint {

    override fun print(printModel: RafflePrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> RafflePrintQ2().print(printModel , isRetry , callback )
            DatafonoType.NEW9220.type -> RafflePrintNEW9220().print(printModel , isRetry , callback )
        }

    }
}