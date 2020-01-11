package co.com.pagatodo.core.data.print

import android.os.Build
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil

class SportingPrint: BasePrint(), ISportingPrint {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun print(printModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> SportingPrintQ2().print(printModel, isRetry, callback)
            DatafonoType.NEW9220.type -> SportingPrintNEW9220().print(printModel, isRetry, callback)
        }

    }



}