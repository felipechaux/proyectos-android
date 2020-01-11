package co.com.pagatodo.core.data.model.response

class ResponseUpdateStubModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var message: String? = null
    var values: List<ValuesUpdateStubModel>? = null
}

class ValuesUpdateStubModel {
    var id: String? = null
    var value: String? = null
}