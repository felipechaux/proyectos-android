package co.com.pagatodo.core.data.model

import java.io.Serializable

class VirtualLotteryModel: Serializable {
    var typeItem: Int = 1
    var lotteryCode: String? = null
    var fullName: String? = null
    var shortName: String? = null
    var date: String? = null
    var hour: String? = null
    var fractions: String? = null
    var fractionValue: Double? = null
    var draw: String? = null
    var award: Double? = null
    var isSelected = false
}