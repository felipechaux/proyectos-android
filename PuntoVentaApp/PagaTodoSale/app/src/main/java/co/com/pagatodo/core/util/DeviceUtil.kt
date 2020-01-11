package co.com.pagatodo.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.telephony.TelephonyManager
import co.com.pagatodo.core.PagaTodoApplication
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import co.com.pagatodo.core.BuildConfig
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.wizarpos.wizarviewagentassistant.aidl.IAPNManagerService
import java.net.NetworkInterface.getNetworkInterfaces
import java.util.*

class DeviceUtil {
    companion object {

        @SuppressLint("MissingPermission")
        fun getDeviceIMEI(): String? {
            val deviceManager = PagaTodoApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceManager?.imei
            } else {
                deviceManager?.deviceId
            }
        }

        @SuppressLint("MissingPermission")
        fun getSerialNumber(): String? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial().takeLast(9)
            } else {
                Build.SERIAL.takeLast(9)
            }
        }

        @SuppressLint("MissingPermission", "HardwareIds")
        fun getDeviceSIMSerialNumber(): String? {
            val deviceManager = PagaTodoApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            return deviceManager?.simSerialNumber
        }

        fun getOperatorSim ():String{
            val tm = PagaTodoApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            return  tm?.simOperatorName?:""
        }

        fun setupAPN(operator:String,context: Context){

            when(getOperatorSim().toUpperCase()){
                R_string(R.string.apn_operator_claro) -> bindApn(R_string(R.string.apn_claro),context)
                R_string(R.string.apn_operator_tigo) -> bindApn(R_string(R.string.apn_tigo),context)
                R_string(R.string.apn_operator_movistar)  -> bindApn(R_string(R.string.apn_movistar),context)
                else -> {
                    //NO SIN O EL OPERADOR NO FUE RECONOSIDO
                    SharedPreferencesUtil.savePreference(R_string(R.string.apn),"OPERADOR NO VÁLIDO")
                }
            }


        }

        fun hideKeyboard(txtField: EditText, context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(txtField.windowToken, 0)
        }

        fun isSalePoint() = BuildConfig.DEVICE_TYPE.equals(R_string(R.string.device_type_salepoint))

        fun isSmartPhone() = BuildConfig.DEVICE_TYPE.equals(R_string(R.string.device_type_smartphone))

        fun getDeviceModel(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }

        }

        private fun capitalize(s: String?): String {
            if (s == null || s.length == 0) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first) + s.substring(1)
            }
        }

        fun getMacAddr(): String {
            var result = ""
            try {

                val all = Collections.list(getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.getName().equals("wlan0",true)) continue

                    val macBytes = nif.getHardwareAddress() ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        //res1.append(Integer.toHexString(b & 0xFF) + ":");
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    result = res1.toString()
                }
            } catch (ex: Exception) {

                result = "02:00:00:00:00:00"
            }

            return result

        }

        private fun bindApn(apn:String,context: Context) {

            SharedPreferencesUtil.savePreference(R_string(R.string.apn),apn)

            val serviceConnectionAPN1 = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName) {
                    // no implement
                }

                override fun onServiceConnected(name: ComponentName, service: IBinder) {

                    val apnManagerService = IAPNManagerService.Stub.asInterface(service)

                    try {
                        apnManagerService.addByAllArgs(
                            apn,
                            apn,
                            "732",
                            "101",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                        apnManagerService.setSelected(apn)

                        Log.d("IAPNManagerService", "IAPNManagerService  bind success.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                       context.unbindService(this)
                    }

                }
            }
            startConnectService(
                context,
                ComponentName(
                    "com.wizarpos.wizarviewagentassistant",
                    "com.wizarpos.wizarviewagentassistant.APNManagerService"
                ),
                serviceConnectionAPN1
            )


        }


        private fun startConnectService(
            context: Context,
            comp: ComponentName,
            connection: ServiceConnection
        ) {
            try {
                val intent = Intent()
                intent.setPackage(comp.packageName)
                intent.component = comp
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }
}

fun String.resetValueFormat() = this.replace("$", "").replace(".", "")

enum class DatafonoType(val type: String) {
    Q2("WIZARPOS Q2"),
    NEW9220("NEWPOS NEW9220")
}

enum class AuthMode (val type:String){
    NONE("N"),
    BIOMETRY("B"),
    TOKEN("T")
}

enum class TypeBillGiro (val raw:String){
    PAY(R_string(R.string.giro_reprint_type_bill_pay)),
    CAPTURE(R_string(R.string.giro_reprint_type_bill_capture)),
}

enum class GiroTypeRequest (val raw:String){
    EXONERATE_FINGERPRINT("11"),
    CANCEL ("56"),
    CHANGE_BENEFICIARY("58"),
    RISING_RESTRICTION("126"),
    LIFTING_EXPIRED("127"),
    AUTHORIZE_GIRO("50")


}

enum class TypeCreateUser(val raw:Int){
    USER_CREATE_DEFAULT(1),
    USER_UPDATE_DEFAULT(2),
    USER_SEND_CREATE_ORIGEN(3),
    USER_SEND_CREATE_DESTINATION(4),
    USER_SEND_UPDATE(5),
    USER_PAY_ENROLL(6),
    USER_SEND_ENROLL(7)

}

enum class ElderlyTypeOperation(val raw:Int){
    USER_CREATE(1),
    USER_ENROLLER(2),
    SEND_ID(3)
}

val PERMISSIONS = arrayOf(
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)