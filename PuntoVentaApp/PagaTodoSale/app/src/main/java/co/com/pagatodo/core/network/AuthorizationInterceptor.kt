package co.com.pagatodo.core.network

import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SecurityUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.NetworkEvents
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
class AuthorizationInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {


        var continueRequest = true
        val request = chain.request()
        val isHeaderRetry = (request.header("isRetry") ?: "false").toBoolean()

        if (bodyToString(request.body()).contains("REINTENTO")) {
            retryCount++
            if (retryCount == StorageUtil.getRetryCount().toInt()) {
                BaseObservableViewModel.networkSubject.onNext(NetworkEvents.MaxNumberRetry)
                continueRequest = false
            }
        } else {
            retryCount = 0
        }

        if (continueRequest) {
            try {

                val isLogin = isEndpointLogin(request)

                val response = chain.proceed(getRequestBuilder(request))


                val responseString = response.body()?.string()

                if (!isLogin) {
                    actionWhenIsNotLogin(responseString)
                    val token = response.header("Autorizador")
                    if(token!=null && token.isNotEmpty())
                        SharedPreferencesUtil.savePreference<String>(R_string(R.string.shared_key_token),token)

                }

                if (response.code() == 500) {

                    if(actionWhenIsError(responseString)){
                        return response.newBuilder()
                            .code(200)
                            .body(ResponseBody.create(response.body()?.contentType(), responseString))
                            .build()

                    }else{

                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                "",
                                errorServerMessage(responseString)
                            )
                        )

                        return Response.Builder()
                            .code(600) //aborted request.
                            .request(chain.request())
                            .build()
                    }




                } else if (response.code() == 404) {
                    BaseObservableViewModel.baseSubject.onNext(
                        BaseEvents.ShowAlertDialogInMenu(
                            R_string(R.string.title_error_server_connection),
                            R_string(R.string.message_error_server_connection)
                        )
                    )
                    return Response.Builder()
                        .code(600) //aborted request.
                        .request(chain.request())
                        .build()
                } else {
                    return response.newBuilder()
                        .body(
                            ResponseBody.create(response.body()?.contentType(), responseString)
                        )
                        .build()
                }
            } catch (e: Exception) {
                if (!isHeaderRetry) {
                    BaseObservableViewModel.networkSubject.onNext(NetworkEvents.SocketTimeoutException)
                }
            }
            return chain.proceed(request)
        } else {
            return Response.Builder()
                .code(600) //aborted request.
                .request(chain.request())
                .build()
        }
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }
    }

    /**
     * Obtención de los headers requeridos para el llamado de los servicios
     */
    private fun getRequestBuilder(request: Request): Request {
        val newRequestBuilder = request.newBuilder()
            .addHeader(R_string(R.string.sp_accept_encoding),R_string(R.string.sp_accept_encoding_value))
            .method(request.method(), request.body())

        if (!isEndpointLogin(request)) {
            newRequestBuilder
                .addHeader(R_string(R.string.sp_authorizer), getTokenAndSessionId().first)
                .addHeader(R_string(R.string.sp_session_id), getTokenAndSessionId().second)
        }
        return newRequestBuilder.build()
    }

    /**
     * Metodo utilizado para validar si el servicio es el del login
     */
    private fun isEndpointLogin(request: Request) =
        request.url().toString().contains("autenticarUsuario") && !request.url().toString().contains(
            "autenticarUsuarioEnrolado"
        )

    private fun actionWhenIsNotLogin(responseString: String?) {
        responseString?.let {
            try {
                val jsonObject = JSONObject(it)
                val code = jsonObject.getString("codigoRespuesta")
                if (code == "STX_036") {
                    BaseObservableViewModel.networkSubject.onNext(NetworkEvents.UnauthorizedService)
                } else {

                    SecurityUtil.updateSessionTimeOut(SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_session_time_out)))

                }
            } catch (e: JSONException) {
                // not implemented
            }
        }
    }


    private fun actionWhenIsError(responseString: String?):Boolean {

            try {
                val jsonObject = JSONObject(responseString)
                val code = jsonObject.getString("codigoRespuesta")
                if (code == "99") {
                   return true
                } else{

                    return false
                }
            } catch (e: JSONException) {

                return false
            }

    }

    private fun errorServerMessage(responseString: String?): String {

        try {
            val jsonObject = JSONObject(responseString)
            val message = jsonObject.getString("mensaje")
            return message
        } catch (e: JSONException) {
            return R_string(R.string.message_error_server_connection)
        }

    }

    /**
     * Metodo utilizado para obtener el token y el session id para enviar dentro de los headers de los servicios
     */
    private fun getTokenAndSessionId(): Pair<String, String> {
        val token = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_token))
        val sessionId =
            SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_session_id))
        return Pair(token, sessionId)
    }
}