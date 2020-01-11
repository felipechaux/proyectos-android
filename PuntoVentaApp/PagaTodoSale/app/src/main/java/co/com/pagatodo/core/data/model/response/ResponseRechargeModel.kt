package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.RechargeModel

class ResponseRechargeModel {
    var responseCode: String? = null
    var success: Boolean? = null
    var transactionAnswerId: Long? = null
    var message: String? = null
    var serie1: String? = null
    var currentSerie2: String? = null
    var recharge: RechargeModel? = null
    var decriptionPackage:String? = null
    var operatorName: String? = null
}