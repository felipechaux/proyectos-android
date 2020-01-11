package co.com.pagatodo.core

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import co.com.pagatodo.core.data.database.AppDatabase
import co.com.pagatodo.core.data.database.entities.SessionEntity
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.savePreference
import co.com.pagatodo.core.util.print.AidlUtil
import co.com.pagatodo.core.views.sitp.ClienteRecaudoSitp.ClientSitpFacade
import com.cloudpos.printer.Format
import com.cloudpos.printer.PrinterDevice
import com.pos.device.SDKManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

class PagaTodoApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        setupInitialPreferences()

        val isModePrint = BuildConfig.IS_MODE_PRINT
        if (isModePrint) {

            if(DeviceUtil.isSalePoint()) {
                AidlUtil.instance.connectPrinterService(instance)
            } else if (!DeviceUtil.isSmartPhone()) {
//                configPrinterSDK()
            }
        }

        val locale = Locale("es","ES")
        Locale.setDefault(locale)

        instanceClientSitpFacade = ClientSitpFacade(this)
    }

    private fun setupInitialPreferences() {
        // Define el tipo de dispositivo en el que se esta ejecuntado y asigna el canal correspondiente
        when {
            DeviceUtil.isSalePoint() -> savePreference(R_string(R.string.shared_key_canal_id), R_string(R.string.canalId_pos))
            DeviceUtil.isSmartPhone() -> savePreference(R_string(R.string.shared_key_canal_id), R_string(R.string.canalId_smartphone))
            else -> savePreference(R_string(R.string.shared_key_canal_id), R_string(R.string.canalId_dataphone))
        }
        savePreference(R_string(R.string.shared_key_user_type), R_string(R.string.userType))
        savePreference(R_string(R.string.code_product_recharge), R_string(R.string.code_recharge))
        savePreference(R_string(R.string.code_product_superchance), R_string(R.string.code_superchance))
        savePreference(R_string(R.string.code_product_maxichance), R_string(R.string.code_maxichance))
        savePreference(R_string(R.string.code_product_betplay), R_string(R.string.code_betplay))
        savePreference(R_string(R.string.code_product_superastro), R_string(R.string.code_superastro))
        savePreference(R_string(R.string.code_product_sportings), R_string(R.string.code_sporting))
        savePreference(R_string(R.string.transaction_sequence), R_string(R.string.transaction))
        savePreference(R_string(R.string.shared_key_query_type), R_string(R.string.query_type))
    }

    private fun configPrinterSDK() {
        SDKManager.init(getAppContext()) {
            Log.e("PRINTER ERROR: ", "init SDK success(threadname: " + Thread.currentThread().name + ")")
        }
    }

    companion object {
        private lateinit var instance: PagaTodoApplication
        lateinit var instanceClientSitpFacade: ClientSitpFacade
        lateinit var printerDevice: PrinterDevice
        var format: Format? = null

        private var roomInstance: AppDatabase? = null

        fun getInstance(): PagaTodoApplication {
            return instance
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }

        fun getDatabaseInstance(): AppDatabase? {
            if (roomInstance == null) {
                roomInstance = Room.databaseBuilder( instance,
                    AppDatabase::class.java,
                    "pagatodo-database"
                ).allowMainThreadQueries().build()
            }
            return roomInstance
        }

        fun getClientSitpFacadeInstance(): ClientSitpFacade {
            return instanceClientSitpFacade
        }
    }
}