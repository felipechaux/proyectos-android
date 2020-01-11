package co.com.pagatodo.core.data.print

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager

class BluetoothBroadCastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                println("Dispositivo encontrado: "+device?.name)
            }
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                println("Dispositivo conectado: "+device?.name)
            }
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                println("Busqueda iniciada")
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                println("Busqueda finalizada")
            }
            BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED -> {
                println("El dispositivo está a punto de desconectarse")
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                println("Dispositivo desconectado")
            }
            BluetoothDevice.ACTION_NAME_CHANGED -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                println("Dispositivo cambio: "+device.name)
            }
        }
    }
}