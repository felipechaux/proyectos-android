package co.com.pagatodo.core.data.model

class PayBillResponseAgreementsModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionTime: String? = null
    var message: String? = null
    var agreements: List<AgreementModel>? = null
}