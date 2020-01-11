package co.com.pagatodo.core.data.print.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.os.Handler
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import java.io.*
import java.util.*
import kotlin.concurrent.schedule

class PrintBluetoothManager {

    companion object {
        var bluetoothAdapter: BluetoothAdapter? = null
        var socket: BluetoothSocket? = null
        var bluettoothDevice: BluetoothDevice? = null

        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        var workerThread: Thread? = null

        var readBuffer: ByteArray? = null
        var readBufferPosition: Int? = null
        var stopWorker: Boolean? = null

        fun checkPrinterStatus(): Int {
            if (socket == null || socket?.isConnected == false){
                return 1
            }else{
                return 0
            }
        }

        fun findAndConnectDevices(success: () -> Unit?, error: () -> Unit?) {
            try {
                isSocketBluetoothConnected(
                    success,
                    {
                        val pairedDevices = getDevicePaired()
                        val saved_uuid = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_device_bluetooth_uuid))
                        val device = pairedDevices.filter { it.uuids.first().uuid.toString() == saved_uuid }

                        if (device.isNotEmpty()) {
                            bluettoothDevice =
                                device.first()
                            openConnection(
                                device.first(),
                                success,
                                error
                            )
                        } else {
                            error()
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
                error()
            }
        }

        fun isBluetoothAdapter(success: () -> Unit?, error: () -> Unit?) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null && bluetoothAdapter?.isEnabled == true) {
                success()
            }else{
                error()
            }
        }

        fun isSocketBluetoothConnected(success: () -> Unit?, error: () -> Unit?) {
            if (socket == null || socket?.isConnected == false){
                error()
            }else{
                success()
            }
        }

        fun isBluetoothUUIDSaved(success: () -> Unit?, error: () -> Unit?) {
            val uuid = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_device_bluetooth_uuid))
            if (uuid.isNotEmpty()){
                success()
            }else{
                error()
            }
        }

        fun getDevicePaired(): List<BluetoothDevice>{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter?.bondedDevices?.toList() ?: arrayListOf()
        }

        fun getDeviceInfo(): BluetoothDevice? {
            return bluettoothDevice
        }

        fun openConnection(device: BluetoothDevice,success: () -> Unit?, error: () -> Unit?) {
            try {
                val uuid = device.uuids.first().uuid

                bluettoothDevice = device
                socket = bluettoothDevice?.createRfcommSocketToServiceRecord(uuid)
                socket?.connect()
                outputStream = socket?.outputStream
                inputStream = socket?.inputStream

                SharedPreferencesUtil.savePreference(R_string(R.string.shared_key_device_bluetooth_uuid), uuid.toString())
                beginListenForData()
                success()
            } catch (e: Exception) {
                e.printStackTrace()
                error()
            }
        }

        private fun beginListenForData() {
            try {
                val handler = Handler()
                val delimiter: Byte = 10

                stopWorker = false
                readBufferPosition = 0
                readBuffer = ByteArray(1024)

                workerThread = Thread(Runnable {

                    while (!Thread.currentThread().isInterrupted && !stopWorker!!) {
                        try {

                            val bytesAvailable = inputStream?.available() ?: 0

                            if (bytesAvailable > 0) {

                                val packetBytes = ByteArray(bytesAvailable)
                                inputStream?.read(packetBytes)

                                for (i in 0 until bytesAvailable) {

                                    val b = packetBytes[i]
                                    if (b == delimiter) {

                                        val encodedBytes = ByteArray(readBufferPosition!!)
                                        System.arraycopy(
                                            readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.size
                                        )

                                        val data = String(encodedBytes, Charsets.US_ASCII)
                                        readBufferPosition = 0

                                        handler.post { println(data)}

                                    } else {
                                        var position  = readBufferPosition
                                            ?: 0
                                        readBuffer!![position++] = b
                                    }
                                }
                            }

                        } catch (ex: IOException) {
                            stopWorker = true
                        }

                    }
                })

                workerThread?.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun printBitmap(image: Bitmap, callback: (printerStatus: PrinterStatus) -> Unit?) {
            val pc = PrintImageManager()
            pc.initCanvas(380)
            pc.initPaint()
            pc.canvas?.drawBitmap(image,0F,0F,pc.paint)
            pc.length = image.height.toFloat()

            val sendData = pc.printImage()
            outputStream?.write(sendData)

            Timer().schedule(6000){
                callback(PrinterStatus.OK)
            }
        }
    }
}