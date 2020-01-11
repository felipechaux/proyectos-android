package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil

class ConsultsPrint : BasePrint(), IConsultsPrint {

    override fun printConsult(
        products: List<ProductBoxModel>,
        saleTotal: String,
        stubs: String,
        sellerName: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> ConsultsPrintQ2().printConsult(products, saleTotal, stubs, sellerName, callback)
            DatafonoType.NEW9220.type -> ConsultsPrintNEW9220().printConsult(
                products,
                saleTotal,
                stubs,
                sellerName,
                callback
            )
        }

    }


}

