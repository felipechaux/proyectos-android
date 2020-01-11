package co.com.pagatodo.core.util.print

/**
 * Created by Administrator on 2017/6/12.
 */

interface PrinterCallback {
    val result: String
    fun onReturnString(result: String)
}
