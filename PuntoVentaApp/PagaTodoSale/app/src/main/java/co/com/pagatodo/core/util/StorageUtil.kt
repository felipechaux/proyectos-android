package co.com.pagatodo.core.util

import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.KeyValueParameterEntityRoom
import co.com.pagatodo.core.data.model.LastTransactionStatusModel
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.RequestRechargeModel
import co.com.pagatodo.core.data.model.request.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.removePreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.savePreference
import com.google.gson.Gson

var lastTransactionStatusObject: Any? = null

/**
 * Clase utilizada para el manejo de la información almacenada en el dispositivo, esta información es la permitida para almacenar
 */
class StorageUtil {

    companion object {

        /**
         * Metodo que obtiene el número de secuencia de las transacciones
         */
        fun getSequenceTransaction(): Int {
            val sequence = getPreference<Int>(R_string(R.string.shared_key_sequence_transaction))
            return if (sequence < 0) 1 else sequence
        }

        /**
         * Metodo usado para actualizar el número de secuencia
         */
        fun updateSequenceTransaction() {
            var sequence = getSequenceTransaction()
            if (sequence >= 7) {
                sequence = 1
            } else {
                sequence++
            }
            savePreference(R_string(R.string.shared_key_sequence_transaction), sequence)
        }

        /**
         * Metodo usado para actualizar la colilla actual en la aplicación
         */
        fun updateStub(serie1: String, serie2: String) {
            savePreference(R_string(R.string.shared_key_current_serie1), serie1)
            savePreference(R_string(R.string.shared_key_current_serie2), serie2)
        }

        /**
         * Metodo usado para almacenar los minimos datos necesarios de la ultima transacción  25x 0000382
         */
        fun saveLastTransactionStatus(lastTransactionStatus: LastTransactionStatusModel) {
            val jsonString = Gson().toJson(lastTransactionStatus)
            PagaTodoApplication.getDatabaseInstance()?.keyValueDao()?.deleteByKeyAll("LastTransactionStatus")
            PagaTodoApplication.getDatabaseInstance()?.keyValueDao()?.insertAll(listOf(
                KeyValueParameterEntityRoom().apply {
                    key = "LastTransactionStatus"
                    value = jsonString
                }
            ))
            savePreference("LastTransactionStatus", jsonString)
        }

        /**
         * Metodo usado para eliminar la ultima transacción almacenada
         */
        fun removeLastTransaction() {
            PagaTodoApplication.getDatabaseInstance()?.keyValueDao()?.deleteByKeyAll("LastTransactionStatus")
            val key = "LastTransactionStatus"
            removePreference(key)
        }

        /**
         * Metodo que obtiene el estado de la ultima transación almacenada
         */
        fun getLastTransactionStatus(): LastTransactionStatusModel? {

            return Gson().fromJson(PagaTodoApplication.getDatabaseInstance()?.keyValueDao()?.getByKey("LastTransactionStatus")?.value,LastTransactionStatusModel::class.java)

        }

        /**
         * Metodo que obtiene el número de reintentos actuales
         */
        fun getRetryCount(): Long {
            val retryCount = getPreference<String>(R_string(R.string.sp_retry_count)).toLong()
            return if (retryCount > 0) retryCount - 1 else retryCount
        }

        /**
         * Metodo que obtiene el valor del timeout por defecto
         */
        fun getDefaultTimeout(): Long {
            var timeout = getPreference<String>(R_string(R.string.sp_timeout))
            if (timeout.isEmpty()) {
                timeout = R_string(R.string.default_timeout)
            }
            return timeout.toLong()
        }

        /**
         * Metodo que obtiene el valor del timeout cuando es un reintento
         */
        fun getTimeoutRetry(): Long {
            return getPreference<String>(R_string(R.string.sp_timeout_retry)).toLong()
        }

        fun isMessageDisplayed(): Boolean {
            val isDisplayed =
                getPreference<Boolean>(R_string(R.string.shared_key_is_message_displayed))
            savePreference(R_string(R.string.shared_key_is_message_displayed), true)
            return isDisplayed
        }

        /**
         * Metodo que obtiene en número de colunmas para los productos dependiendo del ambiente
         */
        fun numberOfColumnsForEnvironment(numRows: Int = 5): Int {
            return if (DeviceUtil.isSalePoint()) numRows else 2
        }


        /**
         * Metodo que obtiene la referencia del objeto sobre la transaccion pendiente
         */
        fun getLastTransactionObject(lastTransaction: LastTransactionStatusModel): Any {

            var lastTransactionObject: Any? = null

            when (lastTransaction.productName) {

                ProductName.PAYMILLIONAIRE.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestPayMillonaireModel::class.java)

                ProductName.CHANCE.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestChanceModel::class.java)

                ProductName.MAXI_CHANCE.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSuperChanceModel::class.java)

                ProductName.SUPER_CHANCE.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSuperChanceModel::class.java)

                ProductName.RECHARGE.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestRechargeModel::class.java)

                ProductName.SUPER_ASTRO.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSuperAstroModel::class.java)

                ProductName.PHYSICAL_LOTTERY.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSaleOfLotteries::class.java)

                ProductName.VIRTUAL_LOTTERY.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSaleOfLotteries::class.java)

                ProductName.BALOTO.rawValue ->  lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestBalotoModel::class.java)

                ProductName.RAFFLE.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestRaffleModel::class.java)

                ProductName.BETPLAY.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestBetplayModel::class.java)

                ProductName.MEGAGOAL.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSportingModel::class.java)

                ProductName.LEAGUE14.rawValue -> lastTransactionObject = Gson().fromJson(lastTransaction.request, RequestSportingModel::class.java)
            }

            return lastTransactionObject!!
        }
    }
}