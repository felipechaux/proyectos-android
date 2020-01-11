package co.com.pagatodo.core.views.deviceprinters

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.savePreference
import co.com.pagatodo.core.util.print.UsbPrintManagerInfo
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.salepoint.activity_device_printers.*


class DevicePrintersActivity : BaseActivity() {

    var listUsbDevices = listOf<UsbPrintManagerInfo>()
    private lateinit var jsaPrinter: UsbPrintManagerInfo
    private lateinit var otherPrinter: UsbPrintManagerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_printers)
        setupView()
        setupViewModel()
        initListeners()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.menu_device_printers))
        btnNext.setText(R.string.text_btn_save)
        btnNext.setOnClickListener {
            savePrinters()
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        val devicePrintersViewModel = ViewModelProviders.of(this).get(DevicePrintersViewModel::class.java)
        devicePrintersViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is DevicePrintersViewModel.ViewEvent.LoadUsbDevices -> {
                    listUsbDevices = it.listUbsDevices
                    val usbDevicesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listUsbDevices)
                    jsaPrinterSpinner.setAdapter(usbDevicesAdapter)
                    otherPrinterSpinner.setAdapter(usbDevicesAdapter)
                    setupUsbDevices()
                }
            }
        })
        devicePrintersViewModel.loadUsbDevices()
    }

    private fun initListeners(){
        jsaPrinterSpinner.setOnItemSelectedListener { view, position, id, item ->
            jsaPrinter = listUsbDevices[position]
        }
        otherPrinterSpinner.setOnItemSelectedListener { view, position, id, item ->
            otherPrinter = listUsbDevices[position]
        }
    }

    private fun setupUsbDevices(){
        val jsaPrinter = getPreference<String>(R_string(R.string.shared_key_jsa_printer))
        val otherPrinter = getPreference<String>(R_string(R.string.shared_key_other_printer))

        listUsbDevices.forEachIndexed { index, printer ->
            if (jsaPrinter == printer.productPath){
                jsaPrinterSpinner.selectedIndex = index
                this.jsaPrinter = printer
            }
        }

        listUsbDevices.forEachIndexed { index, printer ->
            if (otherPrinter == printer.productPath){
                otherPrinterSpinner.selectedIndex = index
                this.otherPrinter = printer
            }
        }
    }

    private fun savePrinters(){
        validatePrinters {
            savePreference(R_string(R.string.shared_key_jsa_printer), jsaPrinter.productPath)
            savePreference(R_string(R.string.shared_key_other_printer), otherPrinter.productPath)
            showOkAlertDialog(R_string(R.string.title_dialog_success_changed), R_string(R.string.message_dialog_success_changed)){
                finish()
            }
        }
    }

    private fun validatePrinters(closure:()->Unit?){
        if(jsaPrinterSpinner.selectedIndex != 0 && otherPrinterSpinner.selectedIndex != 0){
            closure()
        }else{
            showOkAlertDialog("",R_string(R.string.text_title_device_printers))
        }
    }

}
