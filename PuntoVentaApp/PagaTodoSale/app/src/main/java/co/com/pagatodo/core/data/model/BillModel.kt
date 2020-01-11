package co.com.pagatodo.core.data.model

class BillModel {
    var serviceId: String? = null
    var nameService: String? = null
    var userCode: String? = null
    var payReference: String? = null
    var barCode: String? = null
    var expirationDate: String? = null
    var billValue: String? = null
    var valueToPay: String? = null
    var isBillExpirated: Boolean? = null
    var userName: String? = null
    var billNumber: String? = null
    var description: String? = null
    var billStatus: String? = null
    var accountNumber: String? = null
    var companyId: String? = null
    var companyNit: String? = null
    var codeSeqBill: String? = null
    var collectionDate: String? = null
    var collectionHour: String? = null
    var voucherNumber: String? = null
    var macAddress: String? = null
    var teamVol: String? = null
    var codePointSale: String? = null
    var sellerNumberDocument: String? = null
    var terminal: String? = null
    var flatIdBill: String? = null
    var billService: BillServiceModel? = null
    var alcanos: AlcanosModel? = null
    var isPartialPayment: Boolean? = false

}