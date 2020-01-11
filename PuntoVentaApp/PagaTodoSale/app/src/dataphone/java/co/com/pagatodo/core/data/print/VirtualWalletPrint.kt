package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import com.pos.device.printer.PrintCanvas

class VirtualWalletPrint : BasePrint(), IVirtualWalletPrint {


    override fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> VirtualWalletPrintQ2().print(printModel, callback)
            DatafonoType.NEW9220.type -> VirtualWalletPrintNEW9220().print(printModel, callback)
        }


    }


}