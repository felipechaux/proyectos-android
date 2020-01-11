package co.com.pagatodo.core.util

import android.util.Base64
import co.com.pagatodo.core.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.DESedeKeySpec

class SecurityUtil {

    companion object {

        private const val DESEDE_ENCRYPTION_SCHEME = "DESede"

        @Throws(Exception::class)
        fun encryptTextTo3Des(textToEncrypt: String, key: String): String {
            val cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME)
            cipher.init(Cipher.ENCRYPT_MODE, getKey3DES(key))
            val plainText = textToEncrypt.toByteArray(Charsets.UTF_8)
            val encryptedText = cipher.doFinal(plainText)
            return Base64.encodeToString(encryptedText, Base64.DEFAULT).replace("\n","")
        }

        @Throws(Exception::class)
        private fun getKey3DES(key: String): SecretKey {
            val keyAsBytes = key.toByteArray(Charsets.UTF_8)
            val myKeySpec = DESedeKeySpec(keyAsBytes)
            val mySecretKeyFactory = SecretKeyFactory.getInstance(DESEDE_ENCRYPTION_SCHEME)
            return mySecretKeyFactory.generateSecret(myKeySpec)
        }

        fun convertStringToSH256(input: String): String {
            val HEX_CHARS = "0123456789ABCDEF"
            val bytes = MessageDigest
                .getInstance("SHA-256")
                .digest(input.toByteArray())
            val result = StringBuilder(bytes.size * 2)

            bytes.forEach {
                val i = it.toInt()
                result.append(HEX_CHARS[i shr 4 and 0x0f])
                result.append(HEX_CHARS[i and 0x0f])
            }

            return result.toString()
        }

        fun cifrarTextoSHA1256(texto: String): String? {
            var textoCifrado: String? = null
            val md: MessageDigest

            try {
                md = MessageDigest.getInstance("SHA-256")
                md.update(texto.toByteArray())
                val output = md.digest()
                textoCifrado = bytesToHex(output)
            } catch (e: NoSuchAlgorithmException) {

            }

            return textoCifrado
        }

        private fun bytesToHex(b: ByteArray): String {
            val hexDigit = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
            val buf = StringBuilder()
            for (j in 0..b.size) {
                buf.append(hexDigit[j shr 4 and 0x0f])
                buf.append(hexDigit[j and 0x0f])
            }
            return buf.toString()
        }

        fun updateSessionTimeOut(timeAdd: String) {
            if(timeAdd!=""){
                val gcal = GregorianCalendar()
                gcal.time = Date()
                gcal.add(Calendar.SECOND, timeAdd.toInt() )
                val newDate = gcal.time
                SharedPreferencesUtil.savePreference(RUtil.R_string(R.string.shared_key_session_time_out_date), DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,newDate) );
            }
        }

    }
}