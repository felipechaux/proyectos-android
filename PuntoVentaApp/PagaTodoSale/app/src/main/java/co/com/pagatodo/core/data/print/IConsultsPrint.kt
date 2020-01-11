package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.ProductBoxModel

interface IConsultsPrint {
    fun printConsult(products:List<ProductBoxModel>, saleTotal: String, stubs: String, sellerName: String, callback: (printerStatus: PrinterStatus) -> Unit?)
}