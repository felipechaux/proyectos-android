package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.MenuModel

class ResponseMenuModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var message: String? = null
    var menus: List<MenuModel>? = null
}