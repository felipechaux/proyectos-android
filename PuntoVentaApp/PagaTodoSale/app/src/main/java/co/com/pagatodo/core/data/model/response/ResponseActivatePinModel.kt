package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.PinModel

class ResponseActivatePinModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionHour: String? = null
    var transactionDate: String? = null
    var transactionResponseId: String? = null
    var message: String? = null
    var pin: PinModel? = null
    var serie1: String? = null
    var serie2: String? = null
    var currentSerie2: String? = null
}