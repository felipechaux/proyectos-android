package co.com.pagatodo.core.views.elderly

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ElderlyPayDTO
import co.com.pagatodo.core.data.dto.ElderlySubsidyPaymentModel
import co.com.pagatodo.core.data.dto.response.ResponseElderlyAuthenticateFootprintDTO
import co.com.pagatodo.core.data.dto.response.ResponseElderlyQueryIdDTO
import co.com.pagatodo.core.data.dto.response.ResponsePointedFingersDTO
import co.com.pagatodo.core.data.dto.response.ResponseThirdDTO
import co.com.pagatodo.core.data.model.ElderlyThirdModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_elderly_pay.*
import kotlinx.android.synthetic.main.activity_elderly_pay.btnBack
import kotlinx.android.synthetic.main.activity_elderly_pay.btnNext
import kotlinx.android.synthetic.main.activity_elderly_pay.btnQueryB
import kotlinx.android.synthetic.main.activity_elderly_pay.btnQueryT
import kotlinx.android.synthetic.main.activity_elderly_pay.checkCurador
import kotlinx.android.synthetic.main.activity_elderly_pay.labelErrorDocumentNumberA
import kotlinx.android.synthetic.main.activity_elderly_pay.labelErrorDocumentNumberB
import kotlinx.android.synthetic.main.activity_elderly_pay.lyElderlyA
import kotlinx.android.synthetic.main.activity_elderly_pay.lyElderlyB
import kotlinx.android.synthetic.main.activity_elderly_pay.rdA
import kotlinx.android.synthetic.main.activity_elderly_pay.rdB
import kotlinx.android.synthetic.main.activity_elderly_pay.spinnerDocumentTypeA
import kotlinx.android.synthetic.main.activity_elderly_pay.spinnerDocumentTypeB
import kotlinx.android.synthetic.main.activity_elderly_pay.spinnerSubsidyReference
import kotlinx.android.synthetic.main.activity_elderly_pay.txtDocumentNumberA
import kotlinx.android.synthetic.main.activity_elderly_pay.txtDocumentNumberB

class ElderlyPayActivity : BaseActivity() {

    private lateinit var viewModel: ElderlyViewModel
    private lateinit var documentType: MutableList<String>
    private var cont: Int = 0
    private var authenticateFingerprint: String? = null
    private var isCurador: Boolean? = false
    private var mResponseThirdDTO: ResponseThirdDTO? = null

    private var mElderlyPayDTO: ElderlyPayDTO? = null

    private var  mREAutFootprintDTO: ResponseElderlyAuthenticateFootprintDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elderly_pay)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.elderly_general_title))
        initListenersViews()
        setupViewModel()
    }

    private fun setupViewModel() {
        // Not implemented
        viewModel = ViewModelProviders.of(this).get(ElderlyViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {
                is ElderlyViewModel.ViewEvent.Errors -> showErros(it.errorMessage)
                is ElderlyViewModel.ViewEvent.ParameterSuccess -> loadParameter(it.listDocumentType)
                is ElderlyViewModel.ViewEvent.PointedFingersSuccess -> pointedFingersSuccess(
                    it.responseDTO ?: ResponsePointedFingersDTO()
                )
                is ElderlyViewModel.ViewEvent.QuerySubsidy -> loadQuerySubsidy(
                    it.responseDTO.payments ?: listOf()
                )
                is ElderlyViewModel.ViewEvent.AuthenticateFootprintSuccess -> authenticateFootprintSuccess(it.responseDTO)
                is ElderlyViewModel.ViewEvent.QueryIdSuccess -> queryIdSuccess(it.responseDTO)
                is ElderlyViewModel.ViewEvent.QueryIdNotFound -> showErros(it.errorMessage)
            }
        })

        showDialogProgress(getString(R.string.message_dialog_load_data))

        viewModel.getParametersElderly()

    }

    private fun queryIdSuccess(responseDTO: ResponseThirdDTO) {

        mResponseThirdDTO = responseDTO
        val userName = "${responseDTO.firstName} ${responseDTO.lastName}"

        if (rdA.isChecked)
            txtUserNameA.setText(userName)
        else
            txtUserNameB.setText(userName)


        pointedFingers(responseDTO.id ?: "", responseDTO.typeId ?: "")

    }

    private fun initListenersViews() {

        initListenersOnClick()

        initListenerOnChange()
    }

    private fun initListenerOnChange() {
        rdB.isChecked = true

        rdA.setOnCheckedChangeListener { compoundButton, b ->
            clearContainerPayment()
            if (b) {
                activeAuthorizer()
            } else {
                activeBeneficiary()
            }

        }

        rdB.setOnCheckedChangeListener { compoundButton, b ->
            clearContainerPayment()
            if (b) {
                activeBeneficiary()
            } else {
                activeAuthorizer()
            }

        }

        checkCurador.setOnCheckedChangeListener { compoundButton, b ->

            isCurador = b

        }

    }

    private fun activeBeneficiary() {

        lyElderlyB.visibility = View.VISIBLE

        lyElderlyA.visibility = View.GONE

        spinnerDocumentTypeB.selectedIndex = 1

        txtDocumentNumberB.setText("")

        txtUserNameB.setText("")

        labelErrorDocumentNumberB.visibility = View.GONE

        isCurador = false
    }

    private fun activeAuthorizer() {

        lyElderlyB.visibility = View.GONE

        lyElderlyA.visibility = View.VISIBLE

        spinnerDocumentTypeA.selectedIndex = 1

        txtDocumentNumberA.setText("")

        txtUserNameA.setText("")

        labelErrorDocumentNumberA.visibility = View.GONE

    }

    private fun initListenersOnClick() {

        btnBack.setOnClickListener { finish() }

        btnQueryB.setOnClickListener { submitCollection() }

        btnQueryT.setOnClickListener { submitCollection() }

        btnNext.setOnClickListener { payment() }

        validateInputsTextDocumentNumber()

    }

    private fun loadQuerySubsidy(payments: List<ElderlyPayDTO>) {

        val listData = payments.map {
            "Referencia ${it.reference} - valor $${formatValue(
                (it.value ?: "0.0").toDouble()
            )} "
        } as MutableList<String>

        listData.add(0, getString(R.string.elderly_pay_place_holder_reference))

        spinnerSubsidyReference.setItems(listData)

        containerPayment.visibility = View.VISIBLE

        spinnerSubsidyReference.setOnItemSelectedListener { view, position, id, item ->

            if (position != 0) {

                val selectPayments = payments.get(position - 1)
                initReferencePayContainer(selectPayments)

            } else {
                clearReferencePayContainer()
            }


        }

        hideDialogProgress()

    }

    private fun initReferencePayContainer(selectPayments: ElderlyPayDTO) {

        clearReferencePayContainer()

        mElderlyPayDTO = selectPayments

        val thirdModel = createElderlyThirdModel()

        txtPayTypeId.setText(thirdModel.documentType)

        txtPayId.setText(thirdModel.document)

        txtPayThirdOrigin.setText(selectPayments.beneficiaryPay?.id ?: "")

        txtPayNameB.setText(selectPayments.beneficiaryPay?.name ?: "")

        txtPayLastNameB.setText(selectPayments.beneficiaryPay?.lastName ?: "")

        txtPayReference.setText(selectPayments.reference ?: "")

        val value = "$${formatValue((selectPayments.value ?: "0.0").toDouble())}"

        txtPayValue.setText(value)

        txtPayDate.setText("-")

    }

    private fun clearReferencePayContainer() {

        mElderlyPayDTO = null
        txtPayTypeId.setText(getString(R.string.giro_not_found))
        txtPayId.setText(getString(R.string.giro_not_found))
        txtPayThirdOrigin.setText(getString(R.string.giro_not_found))
        txtPayNameB.setText(getString(R.string.giro_not_found))
        txtPayLastNameB.setText(getString(R.string.giro_not_found))
        txtPayReference.setText(getString(R.string.giro_not_found))
        txtPayValue.setText(getString(R.string.giro_not_found))
        txtPayDate.setText(getString(R.string.giro_not_found))

    }

    private fun loadParameter(listDocumentType: List<String>?) {

        documentType = listDocumentType as MutableList<String>

        val dataDocumentType = documentType.map {
            it.split("-")[0]
        }

        spinnerDocumentTypeB.setItems(dataDocumentType)
        spinnerDocumentTypeB.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        spinnerDocumentTypeA.setItems(dataDocumentType)
        spinnerDocumentTypeA.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        spinnerDocumentTypeB.setOnItemSelectedListener { view, position, id, item ->

        }

        hideDialogProgress()
    }

    private fun showErros(errorMessage: String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage)

    }

    private fun submitCollection() {

        hideKeyboard()
        clearContainerPayment()

        if (validateFields()) {

            getElderly()
        }

    }

    private fun clearContainerPayment() {

        clearReferencePayContainer()
        containerPayment.visibility = View.GONE
        spinnerSubsidyReference.setItems(listOf<String>())
        mResponseThirdDTO = null
        mREAutFootprintDTO = null
        mElderlyPayDTO = null
    }

    private fun getElderly() {

        showDialogProgress(getString(R.string.text_load_data))

        viewModel.elderlyQueryId(createElderlyThirdModel())

    }

    private fun createElderlyThirdModel(): ElderlyThirdModel {

        val elderlyThirdModel = ElderlyThirdModel()

        if (rdA.isChecked) {
            elderlyThirdModel.apply {
                this.document = txtDocumentNumberA.text.toString()
                this.documentType = spinnerDocumentTypeA.text.toString()
            }
        } else {
            elderlyThirdModel.apply {
                this.document = txtDocumentNumberB.text.toString()
                this.documentType = spinnerDocumentTypeB.text.toString()
            }
        }

        return elderlyThirdModel

    }

    private fun payment() {


        if(spinnerSubsidyReference.selectedIndex !=0){

            showConfirmPayment()


        }else{

            showOkAlertDialog("",getString(R.string.elderly_pay_error_payment))
        }

    }

    private fun showConfirmPayment() {


        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_elderly_pay, null)

        dialogView.findViewById<TextView>(R.id.dialog_id).setText(mElderlyPayDTO?.beneficiaryPay?.id)

        var name = "${mElderlyPayDTO?.beneficiaryPay?.name} ${mElderlyPayDTO?.beneficiaryPay?.lastName} "

        dialogView.findViewById<TextView>(R.id.dialog_name).setText(name    )
        dialogView.findViewById<TextView>(R.id.dialog_references).setText(mElderlyPayDTO?.reference)
        val value = "$${formatValue((mElderlyPayDTO?.value ?: "0.0").toDouble())}"
        dialogView.findViewById<TextView>(R.id.dialog_value).setText(value)

        val dialog = AlertDialog.Builder(this).apply {
            setTitle(RUtil.R_string(R.string.dialog_elderly_title))
            setView(dialogView)
            setCancelable(false)
            setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.text_btn_accept)) { dialog, which ->

                dialog.dismiss()
                isForeground = false
                subsidyPayment()

            }
        }.show()

        dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.secondaryText
            )
        )


    }

    private fun querySubsidy() {

        showDialogProgress(getString(R.string.elderly_pay_load_sub_references))
        val third = createElderlyThirdModel()
        third.apply {
            this.indexFingerPrintRight = authenticateFingerprint
        }

        viewModel.elderlyQuerySubsidy(third)


    }

    private fun pointedFingers(id: String, idType: String) {

        val third = ElderlyThirdModel().apply {

            this.documentType = idType
            this.document = id
        }

        viewModel.elderlyPointedFingers(third)

    }

    private fun authenticateFootprint() {

        val third = createElderlyThirdModel()

        third.apply {
            this.indexFingerPrintRight = authenticateFingerprint
        }

        showDialogProgress(getString(R.string.message_dialog_login_auth))
        viewModel.elderlyQueryAuthenticateFootprint(third)
    }

    private fun subsidyPayment() {

        showDialogProgress(getString(R.string.text_load_data))

        val elderlySubsidyPaymentModel = ElderlySubsidyPaymentModel().apply {
            this.reference = mElderlyPayDTO?.reference
            this.payValue = (mElderlyPayDTO?.value)?.toDouble().toString()
            this.curator = isCurador
            this.footprintName = mREAutFootprintDTO?.footprintName
            this.footprintImage = authenticateFingerprint
        }

        if(rdB.isChecked){

            elderlySubsidyPaymentModel.apply {
                this.titularDocumentType  = mElderlyPayDTO?.beneficiaryPay?.typeId
                this.titularDocumentId = mElderlyPayDTO?.beneficiaryPay?.id
                this.titularName = mElderlyPayDTO?.beneficiaryPay?.name
                this.titularFirstLastName  = mElderlyPayDTO?.beneficiaryPay?.lastName
                this.titularSecondLastName  = mResponseThirdDTO?.secondLastName
                this.documentTypeAuthorired  = null
                this.authoriredDocumentId  = null
                this.authorizedName = null
                this.authorizedFirstLastName = null
                this.authorizedSecondLastName = null
            }
        }else{

            elderlySubsidyPaymentModel.apply {
                this.titularDocumentType  = mElderlyPayDTO?.beneficiaryPay?.typeId
                this.titularDocumentId = mElderlyPayDTO?.beneficiaryPay?.id
                this.titularName = mElderlyPayDTO?.beneficiaryPay?.name
                this.titularFirstLastName  = mElderlyPayDTO?.beneficiaryPay?.lastName
                this.titularSecondLastName  = mResponseThirdDTO?.secondLastName

                this.documentTypeAuthorired  = mResponseThirdDTO?.typeId
                this.authoriredDocumentId = mResponseThirdDTO?.id
                this.authorizedName = "${mResponseThirdDTO?.firstName} ${mResponseThirdDTO?.lastName}"
                this.authorizedFirstLastName = mResponseThirdDTO?.firstName
                this.authorizedSecondLastName = mResponseThirdDTO?.lastName
            }

        }

        viewModel.elderlyQueryMakeSubsidyPayment(elderlySubsidyPaymentModel)

    }

    private fun pointedFingersSuccess(response: ResponsePointedFingersDTO) {

        hideDialogProgress()
        if (response.ringFingerR != "N") {
            savePrint()

        } else {
            showOkAlertDialog("", getString(R.string.elderly_pay_error_pointer_finger))
        }

    }

    private fun authenticateFootprintSuccess(responseDTO: ResponseElderlyAuthenticateFootprintDTO) {

        mREAutFootprintDTO = responseDTO

        hideDialogProgress()

        querySubsidy()

    }

    private fun savePrint() {

        val call = object : com.datacenter.fingerprintlibrary.IResultListener {
            override fun onResultData(huella: Array<out Array<String>>?) {


                if ((huella?.get(0)?.get(1) ?: "").isNotEmpty()) {

                    authenticateFingerprint = huella?.get(0)?.get(1) ?: ""
                    authenticateFootprint()

                } else {
                    showOkAlertDialog("", getString(R.string.text_error_login))

                }


            }

        }
        bioFacade.setListener(call)

        bioFacade.procesar(
            arrayOf(com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_INDICE),
            true
        )
    }

    private fun validateFields(): Boolean {

        var isValid = true

        if (rdB.isChecked) {

            if (txtDocumentNumberB.text.isEmpty()) {
                labelErrorDocumentNumberB.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorDocumentNumberB.visibility = View.GONE
            }
        }
        if (rdA.isChecked) {

            if (txtDocumentNumberA.text.isEmpty()) {
                labelErrorDocumentNumberA.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorDocumentNumberA.visibility = View.GONE
            }

        }

        return isValid;
    }

    private fun validateInputsTextDocumentNumber() {

        txtDocumentNumberA.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDocumentNumberA.text.toString().isEmpty())
                    labelErrorDocumentNumberA.visibility = View.VISIBLE
                else
                    labelErrorDocumentNumberA.visibility = View.GONE
            }
        })

        txtDocumentNumberB.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDocumentNumberB.text.toString().isEmpty())
                    labelErrorDocumentNumberB.visibility = View.VISIBLE
                else
                    labelErrorDocumentNumberB.visibility = View.GONE
            }
        })

    }
}
