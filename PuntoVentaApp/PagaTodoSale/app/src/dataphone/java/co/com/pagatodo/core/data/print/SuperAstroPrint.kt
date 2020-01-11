package co.com.pagatodo.core.data.print

import android.graphics.Paint
import android.graphics.Typeface
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.pos.device.printer.PrintCanvas
import java.lang.StringBuilder

class SuperAstroPrint: BasePrint(), ISuperAstroPrint {

    override fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> SuperAstroPrintQ2().print(printModel, isRetry, callback)
            DatafonoType.NEW9220.type -> SuperAstroPrintNEW9220().print(printModel, isRetry, callback)
        }

    }


}