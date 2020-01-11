package co.com.pagatodo.core.views.sitp.ClienteRecaudoSitp

import android.content.Context
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import apirecargaandroidjni.rbsas.com.proxyapi.ApiRecargaAndroid
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseQueryInitDateDTO
import java.io.File

class HandlerFiles(
    val context: Context,
    var api: ApiRecargaAndroid
) {

    private lateinit var data: ResponseQueryInitDateDTO
    private var directory: File? = null
    val TAG = "logcat"

    fun createDirectory(data: ResponseQueryInitDateDTO) {
        this.data = data
        val externalDirectoryName = context.getString(R.string.directory_name)
        directory = createExternalFolder(externalDirectoryName)
        val ExternalDirectory = File(directory.toString())

        if (ExternalDirectory.exists() && ExternalDirectory.isDirectory) {
            Log.i(TAG, context.getString(R.string.sms_directorio) + externalDirectoryName + context.getString(R.string.sms_directorio_existe))
            addFile()
        } else {
            addFile()
            Log.i(TAG, context.getString(R.string.sms_directorio) + externalDirectoryName + context.getString(R.string.sms_directorio_no_existe))
        }
    }

    fun createExternalFolder(nombreDirectorio: String?): File? {

        val filepath: String = getExternalStorageDirectory().getPath()
        val directorio = File(filepath, nombreDirectorio)
        if (!directorio.exists()) {
            directorio.mkdirs()
        }
        return directorio
    }


    fun addFile() {
        api.createConfigDat(data.sitpAccount, data.sitpDevice)
    }

    fun init(): Boolean {
        var resultInit = false
        var result =
            api.init(directory.toString(), data.sitpAccount, data.sitpDevice, data.sitpIp, Integer.parseInt(data.sitpPort))

        Log.i(TAG, context.getString(R.string.result) + result)

        if (result > 0) {
            resultInit = true
        }

        return resultInit

    }

}