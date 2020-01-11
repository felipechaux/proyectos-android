package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference

abstract class BaseRepository {

    protected val thirtySecondsTimeOut: Long = 30
    protected  fun getCanalId() = getPreference<String>(R_string(R.string.shared_key_canal_id))
    protected fun getSellerCode() = getPreference<String>(R_string(R.string.shared_key_seller_code))
    protected fun getSellerName() = getPreference<String>(R_string(R.string.shared_key_seller_name))
    protected fun getUserType() = getPreference<String>(R_string(R.string.shared_key_user_type))
    protected fun getTerminalCode() = getPreference<String>(R_string(R.string.shared_key_terminal_code))
}