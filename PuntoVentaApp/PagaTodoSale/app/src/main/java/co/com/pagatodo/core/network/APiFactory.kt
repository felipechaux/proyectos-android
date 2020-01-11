package co.com.pagatodo.core.network

import android.util.Log
import co.com.pagatodo.core.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.util.StorageUtil

/**
 * Clase usada para realizar el llamado de los servicios por medio de la librería retrofit
 */
class ApiFactory {

    // Inicialización del api factory
    init {
        setup()
    }

    companion object {
        // Timeout usado por defecto al momento de llamar los servicios
        private val defaultTimeOut: Long = StorageUtil.getDefaultTimeout()

        // Timeout usado por defecto al momento de llamar los servicios
        private val isGZIPCompress: Boolean = false


        // URL con la cual se llama a los servicios
        private var BASE_URL = BuildConfig.BASE_URL
        private var retrofit: Retrofit.Builder? = null

        /**
         * Método público usado para llamar la configuración de la api
         */
        fun build(timeOut: Long = defaultTimeOut , isCompress: Boolean = false): PagaTodoService? {
            return setup(timeOut,isCompress)
                ?.build()?.create<PagaTodoService>(PagaTodoService::class.java)
        }

        /**
         * Método privado usado para retornar la librería de retrofit ya configurada con los parámetros necesarios
         * para realizar el llamado de los servicios
         */
        private fun setup(timeOut: Long = defaultTimeOut, isCompress: Boolean = false): Retrofit.Builder? {

            if(retrofit != null){

                return retrofit
            }

            retrofit = Retrofit.Builder()

            // Creación del cliente HTTP para realizar el llamado de los servicios
            val clientBuilder = createOkHttpClient(isCompress,timeOut)

            // Configuración del cliente de retrofit que será retornado para poder proceder con el llamado de los servicios
            retrofit?.client(clientBuilder.build())
                    ?.baseUrl(BASE_URL)
                    ?.addConverterFactory(GsonConverterFactory.create())
                    ?.addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            return retrofit
        }


        private fun createOkHttpClient(isCompress:Boolean = false,timeOut: Long):OkHttpClient.Builder{

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            var clientBuilder:OkHttpClient.Builder? = null

            if(isCompress){
                clientBuilder = OkHttpClient.Builder()
                    .cache(null)
                    .readTimeout(timeOut, TimeUnit.SECONDS)
                    .addInterceptor(AcceptGzipInterceptor())
                    .addInterceptor(AuthorizationInterceptor())
                    .addInterceptor(UngzippingInterceptor())
                    .addInterceptor(logging)
            }else{
                clientBuilder = OkHttpClient.Builder()
                    .cache(null)
                    .readTimeout(timeOut, TimeUnit.SECONDS)
                    .addInterceptor(AuthorizationInterceptor())
                    .addInterceptor(logging)
            }

            // En caso de ser necesario se asigna el certificado SSL a la petición de los servicios
            if (BuildConfig.SHOULD_USE_SSL) {
                clientBuilder = SSLConfig().addSSLToHttpClientBuilder(clientBuilder, PagaTodoApplication.getAppContext())
            }

            return clientBuilder!!

        }

    }
}