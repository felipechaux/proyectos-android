package co.com.pagatodo.core.views.giro


import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GiroCriteriaDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryGiroDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.adapters.GiroConsultAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.BaseFragment
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity
import kotlinx.android.synthetic.main.fragment_giro_consult.*
import kotlinx.android.synthetic.main.fragment_giro_consult.btnBack
import java.text.SimpleDateFormat
import java.util.*


class GiroConsultFragment : BaseFragment() {

    private lateinit var giroViewModel: GiroViewModel
    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()
    private lateinit var documentType: MutableList<String>
    private var giroPay: GiroCriteriaDTO? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_giro_consult, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()

    }

    private fun setupViewModel() {

        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, Observer {

            when (it) {
                is GiroViewModel.ViewEvent.Errors -> errors(it.errorMessage)
                is GiroViewModel.ViewEvent.ParameterSuccess -> loadParameters(it.listDocumentType)
                is GiroViewModel.ViewEvent.GiroCriterialSuccess -> loadConsultGiro(it.listGiros)

            }
        })

        showDialogProgress(getString(R.string.message_dialog_load_data))
        giroViewModel.getParametersGiros()

    }

    private fun loadConsultGiro(giros: List<GiroCriteriaDTO>) {

        val giroConsultAdapter = GiroConsultAdapter(giros)

        val call = object : GiroConsultAdapter.OnActionListener {
            override fun onClickItem(giroCriteriaDTO: GiroCriteriaDTO) {

                giroPay = giroCriteriaDTO
            }

        }

        giroConsultAdapter.setListener(call)
        rvGirosConsult.adapter = giroConsultAdapter

        if (!DeviceUtil.isSalePoint()) {
            containerConsult.visibility = View.GONE
            containerResultConsult.visibility = View.VISIBLE
        }
        hideDialogProgress()

        calculateHeightRc(giros)
    }

    private fun calculateHeightRc(giros: List<GiroCriteriaDTO>) {

        if (giros.size > 3) {
            val params = rvGirosConsult.getLayoutParams()

            if (DeviceUtil.isSalePoint())
                params.height = 60 * giros.size

        }

    }

    private fun errors(error: String) {

        hideDialogProgress()
        showOkAlertDialog("", error)

    }

    private fun loadParameters(listDocumentType: List<String>?) {

        documentType = listDocumentType as MutableList<String>

        val dataDocumentType = documentType.map {
            it.split("-")[0]
        }

        spinnerDocumentType.setItems(dataDocumentType)
        spinnerDocumentType.selectedIndex = dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        val listUserType = ArrayList<String>()
        listUserType.add("Destinatario")
        listUserType.add("Remitente")

        spinnerTypeUser.setItems(listUserType)
        spinnerTypeUser.selectedIndex = listUserType.indexOf("Destinatario")
        spinnerTypeUser.isEnabled = false
        spinnerTypeUser.setBackgroundResource(R.drawable.txt_background_inactive)


        hideDialogProgress()
    }

    private fun setupFragmentView() {
        initDateView()
        initListenersViews()
    }

    private fun initDateView() {

        val calendar = Calendar.getInstance()
        val formate = SimpleDateFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH.rawValue, Locale.getDefault())
        val date = formate.format(calendar.time)
        txtDateStart.text = date
        txtDateEnd.text = date

    }

    private fun initListenersViews() {
        initListenersClick()
    }

    private fun initListenersClick() {

        txtDateStart.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                activity!!,
                R.style.DateDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    calendarStart.set(Calendar.YEAR, year)
                    calendarStart.set(Calendar.MONTH, month)
                    calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    txtDateStart.text = SpannableStringBuilder(
                        convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendarStart.time)
                    )

                },
                calendarStart.get(Calendar.YEAR),
                calendarStart.get(Calendar.MONTH),
                calendarStart.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }

        txtDateEnd.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                activity!!,
                R.style.DateDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->


                    calendarEnd.set(Calendar.YEAR, year)
                    calendarEnd.set(Calendar.MONTH, month)
                    calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    txtDateEnd.text = SpannableStringBuilder(
                        convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendarEnd.time)
                    )

                },
                calendarEnd.get(Calendar.YEAR),
                calendarEnd.get(Calendar.MONTH),
                calendarEnd.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }

        btnConsult.setOnClickListener {
            hideKeyboard()
            setupConsultGiro(txtReferenceNumber.text.isNotEmpty())

        }

        btnSearchDocument.setOnClickListener {
            hideKeyboard()
            setupConsultGiro(isReference = false)
        }

        btnSearchReference.setOnClickListener {
            hideKeyboard()
            setupConsultGiro(isReference = true)
        }

        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnConsultResultBack.setOnClickListener {

            if (!DeviceUtil.isSalePoint()) {
                containerConsult.visibility = View.VISIBLE
                containerResultConsult.visibility = View.GONE
            }else{
                onBackPressed()
            }
        }

        btnConsultPayment.setOnClickListener {

            if (giroPay != null) {
                giroPayConfirm()
            } else {

                showOkAlertDialog("", "Debe seleccionar un giro ")
            }


        }

    }

    private fun giroPayConfirm() {

        activity?.runOnUiThread {


            val dialog = AlertDialog.Builder(context!!).apply {
                setTitle(RUtil.R_string(R.string.dialog_giro_consul_pay_title))
                setMessage(RUtil.R_string(R.string.dialog_giro_consul_pay_msm))
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.text_btn_pay)) { dialog, which ->
                    dialog.dismiss()
                    goToPaymentGiro()

                }
            }.show()

            dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.secondaryText
                )
            )
        }

    }

    private fun goToPaymentGiro() {

        if (DeviceUtil.isSalePoint()) {
            (activity as GiroMenuActivity).navigateToOptionSalePointPayGiro(
                giroPay?.reference
                    ?: ""
            )
        } else {
            (activity as GiroActivity).navigateToOptionPayGiro(giroPay?.reference ?: "")
        }

    }

    private fun setupConsultGiro(isReference: Boolean = false) {


        if (validarForm(isReference)) {

            showDialogProgress(getString(R.string.message_dialog_load_data))

            giroViewModel.giroCriteria(
                txtDocumentNumber.text.toString(),
                spinnerDocumentType.text.toString(),
                txtReferenceNumber.text.toString(),
                txtDateStart.text.toString(),
                txtDateEnd.text.toString(),
                spinnerTypeUser.text.toString().toUpperCase()
            )

        }


    }

    private fun validateDateStart(): Boolean = if (txtDateStart.text.toString().isEmpty()) {
        labelErrorDateStart.text = RUtil.R_string(R.string.giro_consult_error_date_start)
        labelErrorDateStart.visibility = View.VISIBLE
        false
    } else {
        labelErrorDateStart.visibility = View.GONE
        true
    }

    private fun validateDateEnd(): Boolean = if (txtDateEnd.text.toString().isEmpty()) {
        labelErrorDateEnd.text = RUtil.R_string(R.string.giro_consult_error_date_end)
        labelErrorDateEnd.visibility = View.VISIBLE
        false
    } else {
        labelErrorDateEnd.visibility = View.GONE
        true
    }

    private fun validarForm(isReference: Boolean = false): Boolean {

        var isValid = true

        if (validateDateStart() && validateDateEnd()) {

            val dateStart = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                txtDateStart.text.toString()
            )
            val dateEnd = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                txtDateEnd.text.toString()
            )

            if (dateStart.compareTo(dateEnd) > 0) {
                isValid = false
                labelErrorDateStart.text = RUtil.R_string(R.string.giro_consult_error_date)
                labelErrorDateStart.visibility = View.VISIBLE
            } else {
                labelErrorDateStart.visibility = View.GONE
            }

        } else isValid = false

        if (!isReference) {

            labelErrorReferenceNumber.visibility = View.GONE
            if (txtDocumentNumber.text.toString().isEmpty()) {
                isValid = false
                labelErrorDocumentNumber.text = RUtil.R_string(R.string.giro_consult_error_dacument_number)
                labelErrorDocumentNumber.visibility = View.VISIBLE
            } else {
                labelErrorDocumentNumber.visibility = View.GONE
            }

        } else {

            labelErrorDocumentNumber.visibility = View.GONE
            if (txtReferenceNumber.text.toString().isEmpty()) {
                isValid = false
                labelErrorReferenceNumber.text = RUtil.R_string(R.string.giro_consult_error_reference_number)
                labelErrorReferenceNumber.visibility = View.VISIBLE
            } else {
                labelErrorReferenceNumber.visibility = View.GONE
            }

        }

        return isValid

    }


}
