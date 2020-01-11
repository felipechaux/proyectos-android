package co.com.pagatodo.core.util

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import co.com.pagatodo.core.PagaTodoApplication

/**
 * Clase usada para el manejo de las preferencias en la aplicación
 */
class SharedPreferencesUtil {

    companion object {

        /**
         * Metodo utilizado para obtener un objeto tipo SharedPreferences
         */
        fun getSharedPreferenceObject(): SharedPreferences {
            return PagaTodoApplication.getInstance().getSharedPreferences("pagaTodoSharedPreferences", MODE_PRIVATE)
        }

        fun <T: Any>savePreference(key: String, value: T) {
            val prefs = getSharedPreferenceObject()
            val editor = prefs.edit()

            when (value) {
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is String -> editor.putString(key, value)
            }
            editor.apply()
        }

        /**
         * Metodo utilizado para obtener una preferencia segun la llave que se le envia
         * @param key llave necesaria para obtener la preferencia almacenada
         */
        inline fun <reified T: Any>getPreference(key: String): T {
            val prefs = getSharedPreferenceObject()
            return if(T::class == Int::class) {
                (prefs.getInt(key, -1) as T)
            } else if(T::class == Long::class) {
                (prefs.getFloat(key, (-0.0).toFloat()) as T)
            } else if(T::class == Float::class) {
                (prefs.getFloat(key, (-0.0).toFloat()) as T)
            } else if(T::class == Boolean::class) {
                (prefs.getBoolean(key, false) as T)
            } else {
                (prefs.getString(key, "") as T)
            }
        }

        /**
         * Metodo utilizado para eliminar una preferencia en especifico
         * @param key llave necesaria para eliminar una preferencia
         */
        fun removePreference(key: String) {
            getSharedPreferenceObject().edit().remove(key).commit()
        }

        fun removeAllPreference() {
            getSharedPreferenceObject().edit().clear().commit()
        }
    }

}