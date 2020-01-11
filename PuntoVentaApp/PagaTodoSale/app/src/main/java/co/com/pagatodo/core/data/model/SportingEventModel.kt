package co.com.pagatodo.core.data.model

class SportingEventModel {
    var eventId: Int? = null
    var code: String? = null
    var name: String? = null
    var betValue: Int? = null
    var multipleVale: String? = null
    var closeDate: String? = null
    var closeTime: String? = null
    var bets: List<SportingBetModel>? = null
    var accumulated: Int? = null
}