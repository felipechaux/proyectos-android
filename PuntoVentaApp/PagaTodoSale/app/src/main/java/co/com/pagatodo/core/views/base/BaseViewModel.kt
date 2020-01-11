package co.com.pagatodo.core.views.base

import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.cloudpos.Device
import com.cloudpos.POSTerminal
import com.cloudpos.printer.PrinterDevice

open class BaseViewModel : ViewModel() {

    fun validatePrinterStatus(printerStatusInfo: IPrinterStatusInfo, closure: () -> Unit) {

        // VALIDAR SI ES UN DATAFONO Q2
        if (DeviceUtil.getDeviceModel() == DatafonoType.Q2.type) {

            validatePrinterStatusQ2(closure)

        } else {

            validatePrinterStatusGeneric(printerStatusInfo, closure)

        }

    }


    private fun validatePrinterStatusQ2(closure: () -> Unit) {

        try{

            printerDevice = POSTerminal.getInstance(PagaTodoApplication.getAppContext()).getDevice(
                "cloudpos.device.printer"
            ) as PrinterDevice

            printerDevice.open()

            when (printerDevice.queryStatus()) {
                PrinterDevice.STATUS_PAPER_EXIST -> {
                    closure()
                }
                PrinterDevice.STATUS_OUT_OF_PAPER -> {
                    val baseEvent = BaseEvents.ShowAlertDialogInMenu(
                        R_string(R.string.message_error_title_paper_not_found),
                        R_string(R.string.message_error_paper_not_found)
                    )
                    BaseObservableViewModel.baseSubject.onNext(baseEvent)
                }
                else -> {
                    val baseEvent = BaseEvents.ShowAlertDialogInMenu(
                        R_string(R.string.message_error_title_print_device),
                        R_string(R.string.message_error_print_device)
                    )
                    BaseObservableViewModel.baseSubject.onNext(baseEvent)
                }
            }

            printerDevice.close()

        }catch (err:Exception){
            val baseEvent = BaseEvents.ShowAlertDialogInMenu(
                R_string(R.string.message_error_title_print_device),
                R_string(R.string.message_error_print_device)
            )
            BaseObservableViewModel.baseSubject.onNext(baseEvent)

        }

    }

    private fun validatePrinterStatusGeneric(printerStatusInfo: IPrinterStatusInfo, closure: () -> Unit) {

        when (printerStatusInfo.getStatus()) {
            PrinterStatus.OK -> {
                closure()
            }
            PrinterStatus.PAPER_LACK -> {
                val baseEvent = BaseEvents.ShowAlertDialogInMenu(
                    R_string(R.string.message_error_title_paper_not_found),
                    R_string(R.string.message_error_paper_not_found)
                )
                BaseObservableViewModel.baseSubject.onNext(baseEvent)
            }
            PrinterStatus.ERROR -> {
                val baseEvent = BaseEvents.ShowAlertDialogInMenu(
                    R_string(R.string.message_error_title_pint_bluetooth_error),
                    R_string(R.string.message_error_bluetooth_connection_error)
                )
                BaseObservableViewModel.baseSubject.onNext(baseEvent)
            }
            else -> {
                val baseEvent = BaseEvents.ShowAlertDialogInMenu(
                    R_string(R.string.message_error_title_print_device),
                    R_string(R.string.message_error_print_device)
                )
                BaseObservableViewModel.baseSubject.onNext(baseEvent)
            }
        }

    }


}