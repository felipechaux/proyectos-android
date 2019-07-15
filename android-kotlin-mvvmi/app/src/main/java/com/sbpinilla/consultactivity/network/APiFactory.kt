package com.sbpinilla.consultactivity.network

import co.com.pagatodo.core.network.AuthorizationInterceptor
import co.com.pagatodo.core.network.ConsultaService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
        // URL con la cual se llama a los servicios
        private var retrofit: Retrofit.Builder? = null


        fun build(timeOut: Long = 60000): ConsultaService? {
            return setup(timeOut)
                ?.build()?.create<ConsultaService>(ConsultaService::class.java)
        }


        /**
         * Método privado usado para retornar la librería de retrofit ya configurada con los parámetros necesarios
         * para realizar el llamado de los servicios
         */
        private fun setup(timeOut: Long = 60000): Retrofit.Builder? {
            retrofit = Retrofit.Builder()

            // Creación del cliente HTTP para realizar el llamado de los servicios
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            var clientBuilder = OkHttpClient.Builder()
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .addInterceptor(logging)
                //.addInterceptor(AuthorizationInterceptor())



            // Configuración del cliente de retrofit que será retornado para poder proceder con el llamado de los servicios
            retrofit?.client(clientBuilder.build())
                    ?.baseUrl("http://dummy.restapiexample.com/api/v1/")
                    ?.addConverterFactory(GsonConverterFactory.create())
                    ?.addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            return retrofit
        }
    }



}