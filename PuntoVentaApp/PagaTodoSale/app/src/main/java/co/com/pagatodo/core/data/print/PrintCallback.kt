package co.com.pagatodo.core.data.print

interface PrintCallback {
    fun onResponse(status:Int, code:String = "", message:String = "")
}