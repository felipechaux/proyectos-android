package co.com.pagatodo.core.data.model.request

import co.com.pagatodo.core.data.model.InputDataModel

class RequestQueryVirtualSoatModel {
    var mac: String? = null
    var channelId: String? = null
    var terminalCode: String? = null
    var productCode: String? = null
    var sellerCode: String? = null
    var entityCode: String? = null
    var userType: String? = null
    var inputData: InputDataModel? = null
    var transactionData: TransactionDataModel? = null
}

class ClientDataModel {
    var userType: String? = null
    var userName: String? = null
}

class TransactionDataModel {
    var clientData: ClientDataModel? = null
}