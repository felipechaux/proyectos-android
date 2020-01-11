package co.com.pagatodo.core.views.information

import android.os.Bundle
import android.view.View
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_information_device.*

class InformationDeviceActivity : BaseActivity() {

    private var inSession = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_device)
        showSessionInfo()
        setupView()
    }

    private fun showSessionInfo() {
        inSession = intent.extras?.get(R_string(R.string.bundle_in_session)) as Boolean
        if(inSession) {
            layoutInfoBranchOffice.visibility = View.VISIBLE
            setupInfoBranchOffice()
        }
    }

    private fun setupView() {
        setupBaseView()
        if(inSession) {
            if(DeviceUtil.isSalePoint() || DeviceUtil.isSmartPhone())
                updateTitle(RUtil.R_string(R.string.device_title_in_session))
            else
                updateTitle(RUtil.R_string(R.string.dataphone_title_in_session))
        } else {
            updateTitle(RUtil.R_string(R.string.dataphone_title))
        }

        labelAPN.text = getString(R.string.text_label_parameter_apn, getPreference(R_string(R.string.apn)))
        labelImei.text = getString(R.string.text_label_parameter_imei, DeviceUtil.getDeviceIMEI()?:DeviceUtil.getSerialNumber() )

        var textSimSerial = DeviceUtil.getDeviceSIMSerialNumber()
        if (textSimSerial.isNullOrEmpty()){
            textSimSerial = R_string(R.string.message_error_no_sim)
        }
        labelSimSerialNumber.text = getString(R.string.text_label_parameter_sim_serial_number, textSimSerial)

        if(DeviceUtil.isSmartPhone()){
            val listOfDevices= arrayListOf<String>()
            val device = PrintBluetoothManager.getDeviceInfo()
            if (device != null){
                listOfDevices .add(device.name.toString())
                spinnerSelectPrinter.setItems(listOfDevices)
                spinnerSelectPrinter.selectedIndex = 0
            }else{
                spinnerSelectPrinter.setItems(listOfDevices)
            }

            spinnerSelectPrinter.setOnClickListener {
                validateInfoDevicePrint {}
            }
        }else{
            container_print.visibility = View.GONE
        }
    }

    private fun setupInfoBranchOffice() {
        labelAdvisor.text = getString(R.string.text_label_parameter_advisor, getPreference(R_string(R.string.shared_key_seller_code)))
        labelMunicipality.text = getString(R.string.text_label_parameter_municipality, getPreference(R_string(R.string.shared_key_municipality_code)))
        labelOffice.text = getString(R.string.text_label_parameter_office, getPreference(R_string(R.string.shared_key_office_code)))
        labelSalePoint.text = getString(R.string.text_label_parameter_salepoint, getPreference(R_string(R.string.shared_key_code_point_sale)))
        labelSerie1.text = getString(R.string.text_label_parameter_serie_1, getPreference(R_string(R.string.shared_key_current_serie1)))
        labelSerie2.text = getString(R.string.text_label_parameter_serie_2, getPreference(R_string(R.string.shared_key_current_serie2)))
    }
}
