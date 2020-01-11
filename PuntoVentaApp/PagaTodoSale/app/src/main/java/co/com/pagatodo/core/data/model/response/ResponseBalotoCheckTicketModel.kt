package co.com.pagatodo.core.data.model.response

class  ResponseBalotoCheckTicketModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var message: String? = null
    var status: String? = null
    var canPay: Boolean? = null
    var ticketStatus: String? = null
    var cashAmount: String? = null
    var netAmount: String? = null
    var serialNumber: String? = null
    var rejectReason: String? = null
    var transactionTime: Long? = null
}