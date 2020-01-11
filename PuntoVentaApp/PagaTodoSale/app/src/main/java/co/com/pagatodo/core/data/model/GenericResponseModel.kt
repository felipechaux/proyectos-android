package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.response.BetsAnswer

class GenericResponseModel {
    var isSuccess = false
    var message: String? = null
    var serie1: String? = null
    var currentSerie2: String? = null
    var uniqueSerial: String? = null
    var digitChecking: String? = null
    var checkNumber: String? = null
    var totalValuePaid: Double? = null
    var totalValueOverloaded: Double? = null
    var totalValueBetting: Double? = null
    var totalValueIva: Double? = null
    var answerBets: List<BetsAnswer>? = null
    var hour: String? = null
    var date: String? = null
}