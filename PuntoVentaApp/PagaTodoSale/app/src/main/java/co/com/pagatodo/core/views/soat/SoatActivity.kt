package co.com.pagatodo.core.views.soat


import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.InputDataDTO
import co.com.pagatodo.core.data.dto.request.RequestIssuePolicyDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryVirtualSoatDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_soat_virtual.*
import kotlinx.android.synthetic.main.fragment_giro_send.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.layout_buttons_back_next.btnBack as btnBack1


class SoatActivity : BaseActivity() {

    private lateinit var viewModel: VirtualSoatViewModel
    private lateinit var documentType: MutableList<String>
    private var alertDialogo: AlertDialog? = null
    private lateinit var listDocument: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soat_virtual)
        setupView()
        setupViewModel()

    }

    private fun setupViewModel() {

        viewModel = ViewModelProviders.of(this).get(VirtualSoatViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {
                is VirtualSoatViewModel.ViewEvent.GetQueryVirtualSoatSuccess -> showModal(it.responseSoatQuery)
                is VirtualSoatViewModel.ViewEvent.Errors -> showErrors(it.errorMessage)
                is VirtualSoatViewModel.ViewEvent.LoadParameters -> loadParameter(
                    it.documents,
                    it.vehicles
                )

            }
        })

        viewModel.getParameter()


    }

    private fun showErrors(errorMessage: String) {

        showOkAlertDialog("", errorMessage)
        hideDialogProgress()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.virtual_soat_title))
        initListenersViews()

        txtVehiclePlateValue.filters =
            arrayOf<InputFilter>(InputFilter.AllCaps(), InputFilter.LengthFilter(6))
    }

    private fun initListenersViews() {
        initListenersOnClick()
    }

    private fun initListenersOnClick() {

        btnNext.setOnClickListener { sendQuerySoat() }
        btnBack.setOnClickListener { finish() }

        txtDocumentNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                labelErrorDocumentNumber.visibility = View.GONE

            }
        })

        txtPhoneValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                labelErrorPhoneValue.visibility = View.GONE

            }
        })

        txtVehiclePlateValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                labelErrorVehiclePlateValue.visibility = View.GONE

            }
        })

    }

    private fun loadParameter(listDocuments: List<String>, listVehicles: List<String>) {

        documentType = listDocuments as MutableList<String>

        spinnerDocumentType.setItems(listDocuments)
        spinnerDocumentType.selectedIndex =
            listDocuments.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        spinnerVehicleType.setItems(listVehicles)
        spinnerVehicleType.selectedIndex = listVehicles.indexOf(listVehicles.first())

        hideDialogProgress()

    }

    private fun sendQuerySoat() {
        if (validateFields()) {

            val inputDataDTO = InputDataDTO().apply {
                phone = txtPhoneValue.text.toString()
                documentTypeID = spinnerDocumentType.text.toString()
                documentNumber = txtDocumentNumber.text.toString()
                licensePlate = txtVehiclePlateValue.text.toString()
                vehicleType = spinnerVehicleType.text.toString()
                phone = txtPhoneValue.text.toString()
                rePrint = false

            }
            showDialogProgress(getString(R.string.text_load_data))
            viewModel.getQueryVirtualSoat(inputDataDTO)

        }
    }

    private fun showModal(response: ResponseQueryVirtualSoatDTO) {
        hideDialogProgress()

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_virtual_soat_confirm, null)

        dialogView.findViewById<TextView>(R.id.txtLicencePlate).setText(response.licensePlate ?: txtVehiclePlateValue.text.toString())
        dialogView.findViewById<TextView>(R.id.txtVehicleOwner)
            .setText(response.ownerName ?: getString(R.string.giro_not_found))
        dialogView.findViewById<TextView>(R.id.txtDocumentNumber)
            .setText(txtDocumentNumber.text.toString())
        dialogView.findViewById<TextView>(R.id.txtCellNumber)
            .setText(txtPhoneValue.text.toString())
        dialogView.findViewById<TextView>(R.id.txtVehicleBrand)
            .setText(response.brand ?: getString(R.string.giro_not_found))
        dialogView.findViewById<TextView>(R.id.txtVehicleColor)
            .setText(response.color ?: getString(R.string.giro_not_found))

        var date =getString(R.string.giro_not_found)

        if(response.beginDate != null)
            date = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_DASH, Date(response.beginDate!!.toLong()))

        dialogView.findViewById<TextView>(R.id.txtBeginDate)
            .setText(date)


        val valueSoat = getString(
            R.string.text_label_parameter_coin,
            formatValue(response.value?.toDouble() ?: 0.0)
        )

        dialogView.findViewById<TextView>(R.id.txtSoatValue).setText(valueSoat)

        //dialogView.findViewById<TextView>(R.id.labelValueReload).setText(valueReload)

        alertDialogo = AlertDialog.Builder(this).apply {
            setTitle(RUtil.R_string(R.string.ticket_title_soat_virtual))
            setView(dialogView)
            setCancelable(false)
            setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.text_btn_pay)) { dialog, which ->
                dialog.dismiss()

                dispatchPay(response)

            }
        }.show()

        alertDialogo?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.secondaryText
            )
        )

    }

    private fun dispatchPay(response: ResponseQueryVirtualSoatDTO) {

        showDialogProgress(getString(R.string.text_load_data))

        val requestIssuePolicyDTO = RequestIssuePolicyDTO().apply {
            this.brand = response.brand
            this.licensePlate = response.licensePlate
            this.collectingValue = response.value
            this.phone = txtPhoneValue.text.toString()
            this.takerDocumentType = spinnerDocumentType.text.toString()
        }


        viewModel.getIssuePolicy(requestIssuePolicyDTO)

    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (spinnerDocumentType.text.toString().isEmpty()) {
            isValid = false
            labelErrorDocumentType.visibility = View.VISIBLE

        } else {
            labelErrorDocumentType.visibility = View.GONE
        }

        if (txtDocumentNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorDocumentNumber.visibility = View.VISIBLE

        } else {
            labelErrorDocumentNumber.visibility = View.GONE
        }

        if (txtPhoneValue.text.toString().isEmpty()) {
            isValid = false
            labelErrorPhoneValue.visibility = View.VISIBLE

        } else {
            labelErrorPhoneValue.visibility = View.GONE
        }

        if (spinnerVehicleType.text.toString().isEmpty()) {
            isValid = false
            labelErrorVehicleType.visibility = View.VISIBLE

        } else {
            labelErrorVehicleType.visibility = View.GONE
        }

        if (txtVehiclePlateValue.text.toString().isEmpty()) {
            isValid = false
            labelErrorVehiclePlateValue.visibility = View.VISIBLE
        } else {
            labelErrorVehiclePlateValue.visibility = View.GONE
        }

        return isValid
    }

}
