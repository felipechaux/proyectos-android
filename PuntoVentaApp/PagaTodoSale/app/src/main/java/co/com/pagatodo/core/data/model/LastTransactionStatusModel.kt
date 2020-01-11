package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference

enum class TransactionStatus(val rawValue: String) {
    PENDING("PENDING"),
    COMPLETED("COMPLETED")
}

enum class ProductName(val rawValue: String) {
    BALOTO("803"),
    PAYMILLIONAIRE("804"),
    CHANCE("01"),
    MAXI_CHANCE("01-1"),
    SUPER_CHANCE("02"),
    SUPER_ASTRO("03"),
    RECHARGE("06"),
    PHYSICAL_LOTTERY("857"),
    VIRTUAL_LOTTERY("811"),
    BETPLAY("449"),
    RAFFLE("23"),
    LEAGUE14("176-1"),
    MEGAGOAL("176-2")
}

enum class TransactionType(val rawValue: String) {
    RETRY("REINTENTO")
}

class LastTransactionStatusModel {
    var sellerCode: String? = null
    var lastStatus: String = TransactionStatus.PENDING.rawValue
    var productName: String? = null
    var serie1: String? = null
    var serie2: String? = null
    var request:String? = null

    init {
        serie1 = getPreference(RUtil.R_string(R.string.shared_key_current_serie1))
        serie2 = getPreference(RUtil.R_string(R.string.shared_key_current_serie2))
    }
}