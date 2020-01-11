package co.com.pagatodo.core.data.model.request

class RequestResetPasswordModel {
    var channelId: String? = null
    var sellerCode: String? = null
    var terminalCode: String? = null
    var userType: String? = null
    var currentPassword: String = ""
    var newPassword: String? = null
}