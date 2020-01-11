package co.com.pagatodo.core.util

import co.com.pagatodo.core.PagaTodoApplication

class RUtil {
    companion object {
        fun R_string(resId: Int): String {
            return PagaTodoApplication.getInstance().getString(resId)
        }
    }
}