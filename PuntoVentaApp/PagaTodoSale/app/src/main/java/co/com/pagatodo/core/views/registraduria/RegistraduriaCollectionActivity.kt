package co.com.pagatodo.core.views.registraduria

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.RegistraduriaServicesDTO
import co.com.pagatodo.core.data.dto.response.ResponseRegistraduriaGetServiceDTO
import co.com.pagatodo.core.data.model.RegistraduriaCollectionModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.base.BaseActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_registraduria_collection.*
import kotlinx.android.synthetic.main.activity_registraduria_collection.btnRegistraduriaCollectionBack
import kotlinx.android.synthetic.main.activity_registraduria_collection.btnRegistraduriaCollectionNext
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorCellphoneNumber
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorDocumentNumber
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorDocumentNumberConfirm
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorDocumentNumberConfirmNotMatch
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorDocumentTypeProcedure
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorEmail
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorFirstName
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorFirstSurname
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorParticle
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorProcedureValue
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorSecondName
import kotlinx.android.synthetic.main.activity_registraduria_collection.labelErrorSecondSurname
import kotlinx.android.synthetic.main.activity_registraduria_collection.spinnerDocumentTypeProcedure
import kotlinx.android.synthetic.main.activity_registraduria_collection.spinnerOtherTypeProcedure
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtCellphoneNumber
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtDocumentNumber
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtDocumentNumberConfirm
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtEmail
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtFirstName
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtFirstSurname
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtParticle
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtProcedureValue
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtSecondName
import kotlinx.android.synthetic.main.activity_registraduria_collection.txtSecondSurname
import kotlinx.android.synthetic.salepoint.activity_registraduria_collection.*
import java.util.concurrent.TimeUnit

class RegistraduriaCollectionActivity : BaseActivity() {

    private lateinit var viewModel: RegistraduriaCollectionViewModel
    private var typeProcedure: String? = null
    private var typeProcedureOther: String? = null
    private var identificationNumber: String? = null
    private var username = StringBuilder()
    private var procedureValue: String? = null
    private lateinit var listServices: MutableList<RegistraduriaServicesDTO>
    private lateinit var listServicesOther: MutableList<RegistraduriaServicesDTO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registraduria_collection)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.home_menu_title_registraduria_collection))
        setupViewModel()
        initListeners()
        initListenersTextWatcher()

        txtProcedureValue.isEnabled = false

    }

    @SuppressLint("CheckResult")
    private fun initListenersTextWatcher() {

        var isView2 = false
        RxTextView.textChanges(txtProcedureValue)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (txtProcedureValue.text.toString().isNotEmpty()) {
                        labelErrorProcedureValue.visibility = View.GONE
                        try {
                            var originalString = txtProcedureValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            txtProcedureValue.setText(getString(R.string.text_label_parameter_coin, formatValue(value)))
                            txtProcedureValue.setSelection(txtProcedureValue.text.length)


                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        if(isView2){
                            labelErrorProcedureValue.visibility = View.VISIBLE
                        }else{
                            isView2 = true
                        }
                    }
                }, {})
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(RegistraduriaCollectionViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {

                is RegistraduriaCollectionViewModel.ViewEvent.Errors -> showErros(it.errorMessage)
                is RegistraduriaCollectionViewModel.ViewEvent.GetServiceSuccess -> loadServices(it.responseDTO)
            }
        })

        viewModel.queryRegistraduriaGetServices()
        showDialogProgress(getString(R.string.message_dialog_load_data))

    }

    private fun loadServices(responseDTO: ResponseRegistraduriaGetServiceDTO) {

        listServices = responseDTO.services!! as MutableList<RegistraduriaServicesDTO>
        listServicesOther =
                responseDTO.services?.dropLast(1)!! as MutableList<RegistraduriaServicesDTO>

        val listNameServices = responseDTO.services?.map { it.service }
        spinnerDocumentTypeProcedure.setItems(listNameServices!!)
        spinnerDocumentTypeProcedure.selectedIndex =
                listNameServices.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        val valueService = listServices.filter { it.service == spinnerDocumentTypeProcedure.text.toString() }.last().value.toString()

        txtProcedureValue.setText(getString(R.string.text_label_parameter_coin, formatValue(valueService.toDouble())))

        hideDialogProgress()
    }

    private fun showErros(errorMessage: String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage) {
            onBackPressed()
        }

    }

    private fun initListeners() {

        validateTypeProcedure()

        btnRegistraduriaCollectionBack.setOnClickListener {
            onBackPressed()
        }

        btnRegistraduriaCollectionNext.setOnClickListener {
            submitCollection()
        }

        validateInputsTextWatcherFirst()

        validateInputsTextWatcherSecond()

        validateInputsSecondLayout()

        validateInputsThirdLayout()

        validarInputsFourLayout()


    }

    private fun validateInputsTextWatcherSecond() {

        txtFirstName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtFirstName.text.toString().isEmpty())
                    labelErrorFirstName.visibility = View.VISIBLE
                else
                    labelErrorFirstName.visibility = View.GONE

            }
        })

        txtEmail.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (!validateEmail(txtEmail.text.toString()) && txtEmail.text.toString().length > 0 )
                    labelErrorEmailFormat.visibility = View.VISIBLE
                else
                    labelErrorEmailFormat.visibility = View.GONE

            }
        })

    }

    private fun validateInputsTextWatcherFirst() {

        txtDocumentNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDocumentNumber.text.toString().isEmpty())
                    labelErrorDocumentNumber.visibility = View.VISIBLE
                else
                    labelErrorDocumentNumber.visibility = View.GONE

                if (txtDocumentNumber.text.toString() != txtDocumentNumberConfirm.text.toString() &&
                    txtDocumentNumber.text.toString().length > 0 &&  txtDocumentNumberConfirm.text.toString().length > 0)
                    labelErrorDocumentNumberConfirmNotMatch.visibility = View.VISIBLE
                else
                    labelErrorDocumentNumberConfirmNotMatch.visibility = View.GONE

            }
        })

        txtDocumentNumberConfirm.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDocumentNumberConfirm.text.toString().isEmpty())
                    labelErrorDocumentNumberConfirm.visibility = View.VISIBLE
                else
                    labelErrorDocumentNumberConfirm.visibility = View.GONE

                if (txtDocumentNumberConfirm.text.toString() != txtDocumentNumber.text.toString() &&
                    txtDocumentNumber.text.toString().length > 0 &&  txtDocumentNumberConfirm.text.toString().length > 0)
                    labelErrorDocumentNumberConfirmNotMatch.visibility = View.VISIBLE
                else
                    labelErrorDocumentNumberConfirmNotMatch.visibility = View.GONE

            }
        })

    }

    private fun validateInputsSecondLayout() {


        txtFirstSurname.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtFirstSurname.text.toString().isEmpty())
                    labelErrorFirstSurname.visibility = View.VISIBLE
                else
                    labelErrorFirstSurname.visibility = View.GONE

            }
        })


    }

    private fun validateInputsThirdLayout() {




    }

    private fun validarInputsFourLayout() {
        txtProcedureValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtProcedureValue.text.toString().isEmpty())
                    labelErrorProcedureValue.visibility = View.VISIBLE
                else
                    labelErrorProcedureValue.visibility = View.GONE

            }
        })
    }

    private fun validateTypeProcedure() {

        spinnerDocumentTypeProcedure.setOnItemSelectedListener { _, _, _, item ->

            if (item.toString() == getString(R.string.DOCUMENT_TYPE_OTHER)) {

                spinnerOtherTypeProcedure.isEnabled = true
                val listNameServices = listServicesOther.map { it.service }
                spinnerOtherTypeProcedure.setItems(listNameServices)
                typeProcedure = getString(R.string.registraduria_ot_service)
                txtProcedureValue.setText("")

                txtProcedureValue.isEnabled = true

                txtProcedureValue.setText("")

            } else {

                txtProcedureValue.isEnabled = false

                val valueService = listServices.filter { it.service == item.toString() }.last().value.toString()

                txtProcedureValue.setText(getString(R.string.text_label_parameter_coin, formatValue(valueService.toDouble())))
                spinnerOtherTypeProcedure.setItems(listOf<String>())
                spinnerOtherTypeProcedure.isEnabled = false
                typeProcedure = item.toString()
            }

        }

        spinnerOtherTypeProcedure.setOnItemSelectedListener { _, _, _, item ->

            typeProcedureOther = item.toString()
        }

    }

    private fun submitCollection() {
        if (validateFields()) {
            validateCollection()
        }
    }

    private fun validateCollection() {

        val inflater = this.layoutInflater
        identificationNumber = txtDocumentNumber.text.toString()
        procedureValue = txtProcedureValue.text.toString()
        username = StringBuilder()
        username.append(txtFirstName.text).append(" ").append(txtSecondName.text).append(" ")
                .append(txtFirstSurname.text).append(" ").append(txtSecondSurname.text)
        val mDialogView = inflater.inflate(R.layout.layout_registraduria_confirm_collection, null)
        mDialogView.findViewById<TextView>(R.id.lblProcedureType).text = spinnerDocumentTypeProcedure.text.toString()
        mDialogView.findViewById<TextView>(R.id.lblIdentificationNumber).text = identificationNumber
        mDialogView.findViewById<TextView>(R.id.lblUsername).text = username.toString()
        mDialogView.findViewById<TextView>(R.id.lblProcedureValue).text = procedureValue
        val alertDialog = this.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(R_string(R.string.registraduria_modal_collection_title))
                setView(mDialogView)
                setCancelable(false)
                setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(R_string(R.string.text_btn_pay)) { _, _ ->
                    payCollection()
                }
            }.show()
        }

        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.secondaryText
                )
        )

    }

    private fun payCollection() {


        val docTypeProcess = listServices.filter { it.service == spinnerDocumentTypeProcedure.text.toString() }.last().id.toString()

        var typeDoc = typeProcedure

        if(typeProcedure == getString(R.string.registraduria_ot_service)){
            typeDoc = spinnerOtherTypeProcedure.text.toString()
        }


        val model = RegistraduriaCollectionModel().apply {
            this.serviceName = typeProcedure
            this.documentNumber = txtDocumentNumber.text.toString()
            this.documentType = typeDoc
            this.firstName = txtFirstName.text.toString()
            this.secondName = txtSecondName.text.toString()
            this.lastName = txtFirstSurname.text.toString()
            this.secondLastName = txtSecondSurname.text.toString()
            this.particle = txtParticle.text.toString()
            this.movile = txtCellphoneNumber.text.toString()
            this.docTypeProcess = docTypeProcess
            this.registraduriaOffice = ""
            this.email = txtEmail.text.toString()
            this.value = txtProcedureValue.text.toString().resetValueFormat().toInt()

        }

        showDialogProgress(getString(R.string.text_load_data))
        viewModel.queryRegistraduriaCollection(model)

        // Not implemented
    }

    private fun validateFields(): Boolean {

        var isValid = true

        if (spinnerDocumentTypeProcedure.text.toString() == getString(R.string.registraduria_collection_document_type_procedure_placeholder) || spinnerDocumentTypeProcedure.text.toString().isEmpty()) {
            labelErrorDocumentTypeProcedure.visibility = View.VISIBLE
            // isValid = false
        } else {
            labelErrorDocumentTypeProcedure.visibility = View.GONE
        }

        if (txtDocumentNumber.text.isEmpty()) {
            labelErrorDocumentNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDocumentNumber.visibility = View.GONE
        }

        if (txtDocumentNumberConfirm.text.isEmpty()) {
            labelErrorDocumentNumberConfirm.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDocumentNumberConfirm.visibility = View.GONE
        }

        if (txtFirstName.text.isEmpty()) {
            labelErrorFirstName.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorFirstName.visibility = View.GONE
        }

        if (txtFirstSurname.text.isEmpty()) {
            labelErrorFirstSurname.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorFirstSurname.visibility = View.GONE
        }

        if (!validateInputsTwo()) isValid = false


        return isValid


    }

    private fun validateInputsTwo(): Boolean {

        var isValid = true






        if (!validateEmail(txtEmail.text.toString()) && txtEmail.text.toString().length > 0 ) {
            isValid = false
            labelErrorEmailFormat.visibility = View.VISIBLE
        } else {
            labelErrorEmailFormat.visibility = View.GONE
        }

        if (txtProcedureValue.text.isEmpty()) {
            labelErrorProcedureValue.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorProcedureValue.visibility = View.GONE
        }

        return isValid
    }

    private fun validateEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}
