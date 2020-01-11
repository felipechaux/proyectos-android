package co.com.pagatodo.core.data.model.print

import android.graphics.Bitmap
import co.com.pagatodo.core.data.model.SportingBetModel

class SportingPrintModel {
    var header: String? = null
    var footer: String? = null
    var productName: String? = null
    var productCode: Int? = null
    var contractDate: String? = null
    var dateOfSale: String? = null
    var stub: String? = null
    var sellerCode: String? = null
    var grid: String? = null
    var digitChecked: String? = null
    var teams: List<SportingBetModel>? = null
    var totalValue: String? = null
    var iva: String? = null
    var value: String? = null
    var digitCheck: String? = null
    var imageFooter: Bitmap? = null
}