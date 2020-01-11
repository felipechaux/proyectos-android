package co.com.pagatodo.core.data.model.request

class RequestIssuePolicyModel {
    var collectingValue: String? = null
    var licensePlate: String? = null
    var brand: String? = null
    var takerDocumentType: String? = null
    var paymentType: String? = null
    var phone: String? = null
    var channelId: String? = null
    var productCode: String? = null
    var sellerCode: String? = null
    var paymentMethod: PaymentMethodModel? = null
    var terminal: String? = null
    var userType: String? = null
    var entityCode: String? = null
    var transactionId: String? = null
}

class PaymentMethodModel {
    var paymentMethod: String? = null
}