package co.com.pagatodo.core.views.chance

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ProductModel
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.getIntOrNull

class ChanceValidations(val selectedLotteries: List<LotteryModel>,
                        val chanceGames: List<ChanceModel>,
                        val productModel: ProductModel?,
                        var arrayValidations: List<ValidationType>) {

    enum class ValidationType {
        NOT_EMPTY_CHANCE,
        VALIDATE_MODALITIES,
        LOTTERIES_SELECTION,
        VALIDATE_BET_AMOUNTS
    }

    fun isValidChance(closure: (Pair<Boolean, String>) -> Unit) {
        var pair = Pair(true, "")
        run loop@ {
            arrayValidations.forEach {
                when (it) {
                    ValidationType.NOT_EMPTY_CHANCE -> {
                        pair = isNotEmptyChance()
                        if (!pair.first) return@loop
                    }
                    ValidationType.VALIDATE_MODALITIES -> {
                        pair = isValidModalities()
                        if (!pair.first) return@loop
                    }
                    ValidationType.LOTTERIES_SELECTION -> {
                        pair = isSelectedLotteries()
                        if (!pair.first) return@loop
                    }
                    ValidationType.VALIDATE_BET_AMOUNTS -> {
                        pair = isValidBetAmounts()
                        if (!pair.first) return@loop
                    }
                }
            }
        }
        closure(pair)
    }

    fun isValidFieldsChance(): Boolean {
        if (isNotEmptyChance().first || isSelectedLotteries().first){
            return true
        }else{
            return false
        }
    }

    fun isSelectedLotteries(): Pair<Boolean, String> {
        val selected = selectedLotteries.count() > 0
        var message = ""
        if (!selected) {
            //envio de titulo y mensaje concatenado
            message = R_string(R.string.title_message_error_lottery_selected)+"-"+R_string(R.string.message_error_lottery_selected)
        }
        return Pair(selected, message)
    }

    fun isNotEmptyChance(): Pair<Boolean, String> {
        val isEmpty = chanceGames.count() > 0
        var message = ""
        if (!isEmpty) {
            message =  R_string(R.string.title_message_error_chance_number_empty)+"-"+R_string(R.string.message_error_chance_number_empty)
        }
        return Pair(isEmpty, message)
    }

    fun isValidModalities(): Pair<Boolean, String> {
        var isValid = true
        var message = ""
        var pair = Pair(isValid, message)
        run loop@ {
            chanceGames.forEach {
                val count = it.number?.trim()?.length ?: 0
                if (count == 1) {
                    pair = isValidForOneDigit(it)
                    if (!pair.first) {
                        return@loop
                    }
                }
                else if (count == 2) {
                    pair = isValidForTwoDigits(it)
                    if (!pair.first) {
                        return@loop
                    }
                }
                else if (count == 3) {
                    pair = isValidforThreeDigits(it)
                    if (!pair.first) {
                        return@loop
                    }
                }
                else if (count == 4) {
                    pair = isValidForFourDigits(it)
                    if (!pair.first) {
                        return@loop
                    }
                }
            }
        }
        return pair
    }

    private fun isValidForOneDigit(chance: ChanceModel): Pair<Boolean, String> {
        val isDirect = chance.direct?.isNullOrEmpty() ?: true
        val isCombined = chance.combined?.isNullOrEmpty() ?: true
        val isPaw = chance.paw?.isNullOrEmpty() ?: true
        val isNail = chance.nail?.isNotEmpty() ?: false

        var status = isDirect && isCombined && isPaw && isNail
        var message = ""
        if (isDirect && isCombined && isPaw && !isNail) {
            status = false
            message =R_string(R.string.title_message_error_chance_bet_empty)+"-"+R_string(R.string.message_error_chance_bet_empty)
        }
        else if (!isDirect || !isCombined || !isPaw ) {
            status = false
            message = R_string(R.string.message_error_nail_1)
        }
        return Pair(status, message)
    }

    private fun isValidForTwoDigits(chance: ChanceModel): Pair<Boolean, String> {
        val isDirect = chance.direct?.isNullOrEmpty() ?: true
        val isCombined = chance.combined?.isNullOrEmpty() ?: true
        val isPaw = chance.paw?.isNotEmpty() ?: false
        val isNail = chance.nail?.isNotEmpty() ?: false

        var status = isDirect && isCombined && (isPaw || isNail)
        var message = ""
        if(isDirect && isCombined && !isPaw && !isNail) {
            status = false
            message =R_string(R.string.title_message_error_chance_bet_empty)+"-"+R_string(R.string.message_error_chance_bet_empty)
        }
        else if (!isDirect || !isCombined) {
            status = false
            message = R_string(R.string.message_error_chance_number_two1)
        }
        else if (!isPaw && !isNail) {
            status = false
            message = R_string(R.string.message_error_chance_number_two2)
        }
        return Pair(status, message)
    }

    private fun isValidforThreeDigits(chance: ChanceModel): Pair<Boolean, String> {
        var errorMessage = ""
        val isDirect = chance.direct?.isNotEmpty() ?: false
        val isCombined = chance.combined?.isNotEmpty() ?: false
        val isPaw = chance.paw?.isNotEmpty() ?: false
        val isNail = chance.nail?.isNotEmpty() ?: false

        var status = true
        if(!isDirect && !isCombined && !isPaw && !isNail) {
            status = false
            errorMessage =R_string(R.string.title_message_error_chance_bet_empty)+"-"+R_string(R.string.message_error_chance_bet_empty)
        }
      /*  else if(!isDirect && isCombined) {
            if (isNail || isPaw) {
                status = false
                errorMessage = R_string(R.string.message_error_chance_number_three1)
            }
        }
        else if(!isDirect && (isPaw || isNail)) {
            status = false
            errorMessage = R_string(R.string.title_message_error_chance_number_three2)+"-"+R_string(R.string.message_error_chance_number_three2)
        }*/
        return Pair(status, errorMessage)
    }

    private fun isValidForFourDigits(chance: ChanceModel): Pair<Boolean, String> {
        val isDirect = chance.direct?.isNotBlank() ?: false
        val isCombined = chance.combined?.isNotBlank() ?: false
        val isDirectOrCombined = isDirect || isCombined
        val isPaw = chance.paw?.isNullOrBlank() ?: true
        val isNail = chance.nail?.isNullOrBlank() ?: true

        var status = isDirectOrCombined && isPaw && isNail
        var message = ""

        if (!isDirectOrCombined) {
            status = false
            message = R_string(R.string.message_error_chance_number_four2)
        }
        else if (!isPaw || !isNail) {
            status = false
            message = R_string(R.string.message_error_chance_number_four1)
        }
        return Pair(status, message)
    }

    fun isNotRepeatedNumbers(): Pair<Boolean, String> {
        var isValid = true
        var message = ""
        chanceGames.forEachIndexed { index,  item ->
            val filteredValues = chanceGames.filterIndexed { internalIndex, _ -> internalIndex != index }
            val exist = hasValueInArray(item, filteredValues)
            if (exist) {
                isValid = false
                message =R_string(R.string.title_message_error_duplicate_number)+"-"+R_string(R.string.message_error_duplicate_number)
                return@forEachIndexed
            }
        }

        return Pair(isValid, message)
    }

    private fun hasValueInArray(item: ChanceModel, array: List<ChanceModel>): Boolean {
        var exist = false
        array.forEach {
            if (item.number == it.number) {
                exist = true
                return@forEach
            }
        }
        return exist
    }


    private fun isValidBetAmounts(): Pair<Boolean, String> {

        var minTicket = 0
        var maxTicket = 0
        var minBet = 0
        var maxBet = 0

        val value = getTotalBetWithoutIva() * selectedLotteries.count()
        val iva = value * CurrencyUtils.getIvaPercentage()
        val totalValueTikect = (value + iva).toInt()

        productModel?.parameters?.forEach {
            when(it.key) {
                R_string(R.string.key_min_value_ticket) -> minTicket = it.value?.toInt() ?: 0
                R_string(R.string.key_max_value_ticket) -> maxTicket = it.value?.toInt() ?: 0
                R_string(R.string.key_min_value_bet) -> minBet = it.value?.toInt() ?: 0
                R_string(R.string.key_max_value_bet) -> maxBet = it.value?.toInt() ?: 0
            }
        }

        val isValidBetTicket = if(  totalValueTikect<= maxTicket ) true else false
        val templateMinMaxMessageError =R_string(R.string.title_message_error_min_max_modality)+"-"+R_string(R.string.message_error_min_max_modality)
        var status = true
        var message = ""
        var modality = ""
        if (isValidBetTicket) {
            chanceGames.forEach {

                if (it.direct?.isNotEmpty() == true) {
                    val isDirect = isValidModality(getIntOrNull(it.direct), minBet, maxBet)
                    if (!isDirect) {
                        status = false
                        modality = "Directo"
                        return@forEach
                    }
                }

                if (it.combined?.isNotEmpty() == true) {
                    val isCombined = isValidModality(getIntOrNull(it.combined), minBet, maxBet)
                    if (!isCombined) {
                        status = false
                        modality = "Combinado"
                        return@forEach
                    }
                }

                if (it.paw?.isNotEmpty() == true) {
                    val isPaw = isValidModality(getIntOrNull(it.paw), minBet, maxBet)
                    if (!isPaw) {
                        status = false
                        modality = "Pata"
                        return@forEach
                    }
                }


                if (it.nail?.isNotEmpty() == true) {
                    val isNail = isValidModality(getIntOrNull(it.nail), minBet, maxBet)
                    if (!isNail) {
                        status = false
                        modality = "Uña"
                        return@forEach
                    }
                }
            }

            if (!status) {
                message = templateMinMaxMessageError
                    .replace("{{min}}", "$$minBet")
                    .replace("{{max}}", "$$maxBet")
                    //.replace("{{modality}}", "$modality")

            }
        }
        else {
            status = false
            message = R_string(R.string.message_error_min_max_bet_chance)
                .replace("{{min}}", "$$minTicket")
                .replace("{{max}}", "$${formatValue(maxTicket.toDouble())}")
        }

        return Pair(status, message)
    }

    private fun isValidModality(value: Int, minModality: Int, maxModality: Int): Boolean {
        return  (value in minModality..maxModality)
    }

    private fun getTotalBetWithoutIva(): Int {
        var numResponse = 0
        chanceGames.forEach {
            val direct = getIntOrNull(it.direct)
            val combined = getIntOrNull(it.combined)
            val paw = getIntOrNull(it.paw)
            val nail = getIntOrNull(it.nail)
            numResponse += direct + combined + paw + nail
        }
        return numResponse
    }

    companion object {
        fun getAllValidationType(): List<ValidationType> {
            return ValidationType.values().toList()
        }
    }
}