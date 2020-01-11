package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.ProductBoxModel

class ResponseCurrentBoxModel {
    var responseCode: String? = null
    var success: Boolean? = null
    var transactionDate: String? = null
    var transactionTime: String? = null
    var message: String? = null
    var sale: Int? = null
    var lottery: Int? = null
    var services: Int? = null
    var others: String? = null
    var totalSale: String? = null
    var productsBox: List<ProductBoxModel>? = null
}