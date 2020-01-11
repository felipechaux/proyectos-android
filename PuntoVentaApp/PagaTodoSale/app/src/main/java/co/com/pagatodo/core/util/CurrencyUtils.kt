package co.com.pagatodo.core.util

import android.annotation.SuppressLint
import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.RepeatChanceModel
import co.com.pagatodo.core.data.model.RepeatSuperAstroModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.floor

var CURRENT_CHANCE_BET: RepeatChanceModel? = null
var CURRENT_SUPERASTRO_BET: RepeatSuperAstroModel? = null

class CurrencyUtils {

    companion object {

        fun calculateSuggestedValue(valor: Double): Int {

            if ((valor % 100) > 0){
                val fraccion = valor.toBigDecimal().divide(100.toBigDecimal())
                val aux = floor(fraccion.toDouble()).toBigDecimal()
                var resto = fraccion.add(aux.negate())

                if (resto.toInt() in 0..9) {
                    resto = resto.multiply(100.toBigDecimal())
                }

                val adicion = 100 - resto.toInt()
                return (valor + adicion).toInt()
            }else{
                return valor.toInt()
            }
        }

        fun getIvaPercentage(): Double {
            return getPreference<String>(R_string(R.string.iva_param_service)).toDouble() / 100
        }

        fun getIvaPercentageSporting(): Double {
            return getPreference<String>(R_string(R.string.iva_param_service)).toDouble() / 100 + 1
        }

        fun isNumeric(input: String): Boolean =
            try {
                input.toDouble()
                true
            } catch(e: NumberFormatException) {
                false
            }

        fun generateRandomAlphanumericString(length: Int): String{
            val random = Random()
            val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val digits = "0123456789"
            val alphanum = (upper + digits).toCharArray()
            var randomString = ""
            if (length > 0){
                for(i in 0..length){
                    randomString = randomString + alphanum.get(random.nextInt(alphanum.count()))
                }
            }
            return randomString
        }

        @SuppressLint("CheckResult")
        fun validateMenuStatus(productCode: String): Boolean {
            val dbInstance = PagaTodoApplication.getDatabaseInstance()
            val menusEntity = dbInstance?.menuDao()?.getAllByProductCode(productCode)
            return menusEntity?.isEmpty() ?: false
        }
    }
}

fun getIntOrNull(number: String?): Int {
    return number?.replace("$","")?.replace(".","")?.toIntOrNull() ?: 0
}

fun String.removeLeadingZero(): String {
    return this.replaceFirst("^0+(?!$)", "")
}

fun formatValue(value: Double): String {
    val df = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale.ITALIAN))
    return df.format(value)
}

