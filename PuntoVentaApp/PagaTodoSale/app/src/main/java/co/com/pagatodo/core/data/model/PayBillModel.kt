package co.com.pagatodo.core.data.model

class PayBillModel {
    var isSuccess: Boolean? = null
    var lstErrors: String? = null
    var user: String? = null
    var cityCode: String? = null
    var oficeCode: String? = null
    var sellerCode: String? = null
    var dateHour: String? = null
    var serviceName: String? = null
    var lstBills: List<BillModel>? = null
}