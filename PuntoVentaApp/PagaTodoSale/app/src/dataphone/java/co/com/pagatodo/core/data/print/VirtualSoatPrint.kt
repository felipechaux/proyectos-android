package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import java.lang.Exception

class VirtualSoatPrint: BasePrint(), IVirtualSoatPrint {

    override fun print(printModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> VirtualSoatPrintQ2().print(printModel, callback)
        }

    }

}