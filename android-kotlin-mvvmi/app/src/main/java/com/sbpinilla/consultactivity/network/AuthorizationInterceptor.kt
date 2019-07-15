package co.com.pagatodo.core.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.RequestBody
import okio.Buffer


var retryCount = 0

/**
 * Esta clase actua como un middleware que intercepta todas las peticiones y respuestas de los servicios
 */
class AuthorizationInterceptor: Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {


        var continueRequest = true
        val request = chain.request()
        val isHeaderRetry = (request.header("isRetry") ?: "false").toBoolean()

        return chain.proceed(request)

    }

}