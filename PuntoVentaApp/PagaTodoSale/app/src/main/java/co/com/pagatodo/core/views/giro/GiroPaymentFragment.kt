package co.com.pagatodo.core.views.giro


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ClientFullDTO
import co.com.pagatodo.core.data.dto.FingerPrintGiroDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryGiroDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryThirdDTO
import co.com.pagatodo.core.data.dto.response.ResponseThirdDTO
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.BaseFragment
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity
import co.com.pagatodo.core.views.login.AuthViewModel
import com.datacenter.fingerprintlibrary.IResultListener
import kotlinx.android.synthetic.main.fragment_giro_payment.*

class GiroPaymentFragment : BaseFragment(), IResultListener {


    private lateinit var giroViewModel: GiroViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var unusualOperations: MutableList<String>
    private var responseQueryGiroDTO: ResponseQueryGiroDTO? = null
    private var fingerPrintThird: FingerPrintGiroDTO = FingerPrintGiroDTO().apply { data = ""}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_giro_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()
    }

    private fun setupViewModel() {

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        authViewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is AuthViewModel.ViewEvent.AuthTokenSuccess -> successAuthSeller()
                is AuthViewModel.ViewEvent.AuthFingerPrintSellerSuccess -> successAuthSeller()
                is AuthViewModel.ViewEvent.AuthTokenError -> failAuthSeller(it.errorMessage)
                is AuthViewModel.ViewEvent.AuthFingerPrintSellerError -> failAuthSeller(it.errorMessage)
            }
        })

        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, Observer {


            when (it) {
                is GiroViewModel.ViewEvent.Errors -> errors(it.errorMessage)
                is GiroViewModel.ViewEvent.ParameterSuccess -> loadParameters(it.listUnusualOperations)
                is GiroViewModel.ViewEvent.QueryGiroSuccess -> setupGiro(it.queryGiro)
                is GiroViewModel.ViewEvent.SendVoucherSuccess -> finishSendVoucher()
                is GiroViewModel.ViewEvent.GiroAuthUserSuccess -> fingerPrintThirdSuccess()
                is GiroViewModel.ViewEvent.GiroAuthUserError -> fingerPrintThirdError(it.errorMessage)
            }


        })


    }

    private fun fingerPrintThirdSuccess() {
        hideDialogProgress()

    }

    private fun fingerPrintThirdError(errorMessage: String) {
        hideDialogProgress()
        clearForm()

        if (!DeviceUtil.isSalePoint()) {
            containerConsultReference.visibility = View.VISIBLE
            containerResultReference.visibility = View.GONE
        }

        showOkAlertDialog("", errorMessage)
        responseQueryGiroDTO = null
        fingerPrintThird.apply { data = "" }

    }

    private fun successAuthSeller() {
        hideDialogProgress()
        confirmPaymentGiro()
    }

    private fun failAuthSeller(message: String) {
        hideDialogProgress()
        showOkAlertDialog("", message)
    }

    private fun finishSendVoucher() {

        hideDialogProgress()
        (activity as BaseActivity)
            .showOkAlertDialog(
                RUtil.R_string(R.string.giro_payment_success_title),
                RUtil.R_string(R.string.giro_payment_success_message_mail)
            ) {
                activity?.onBackPressed()
            }
    }

    private fun errors(errorMessage: String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun loadParameters(listUnusualOperations: List<String>?) {

        unusualOperations = listUnusualOperations as MutableList<String>

        val dataUnusualOperations = listUnusualOperations?.map {
            it.split("-")[0]
        }

        val itemsUnusualOperations = mutableListOf<String>()

        itemsUnusualOperations.addAll(dataUnusualOperations)
        itemsUnusualOperations.add(0, getString(R.string.giro_send_placeholder_unusual_operations))

        spinnerUnusualOperations.setItems(itemsUnusualOperations)

        hideDialogProgress()
    }

    @SuppressLint("InflateParams")
    private fun setupGiro(queryGiro: ResponseQueryGiroDTO) {

        hideDialogProgress()
        if (!(queryGiro.clientDestination?.isEnrolled ?: false))
            navigateToEnrollUser(queryGiro)
        else {
            validateThirdFingerPrint(queryGiro)

            lyContainerConcepts.removeAllViews()

            responseQueryGiroDTO = queryGiro

            val nameOrigen = "${queryGiro.clientOrigin?.firstName
                ?: ""} ${queryGiro.clientOrigin?.lastName
                ?: ""} ${queryGiro.clientOrigin?.secondLastName ?: ""}"
            val nameDestination = "${queryGiro.clientDestination?.firstName
                ?: ""} ${queryGiro.clientDestination?.lastName
                ?: ""} ${queryGiro.clientDestination?.secondLastName ?: ""}"

            lblDocumentNumberOrigin.setText(queryGiro.clientOrigin?.id ?: "")
            lblNameUserOrigen.setText(nameOrigen)
            lblDocumentNumberDestination.setText(queryGiro.clientDestination?.id ?: "")
            lblNameUserDestination.setText(nameDestination)


            lblDepartment.setText(queryGiro.agencyOrigen?.city?.departamentName ?: "")
            lblCity.setText(queryGiro.agencyOrigen?.city?.cityName ?: "")

            lblAgency.setText(
                queryGiro.agencyOrigen?.name ?: RUtil.R_string(R.string.giro_not_found)
            )

            lblSendEmail.setText(queryGiro.clientDestination?.email)

            queryGiro.concepts?.forEach {


                val conceptItemLayout =
                    this.layoutInflater.inflate(R.layout.item_giro_payment_concepts, null)

                conceptItemLayout.findViewById<TextView>(R.id.lblCode).setText(
                    it.code
                        ?: RUtil.R_string(R.string.giro_not_found)
                )
                conceptItemLayout.findViewById<TextView>(R.id.lblConcept).setText(
                    it.name
                        ?: RUtil.R_string(R.string.giro_not_found)
                )
                conceptItemLayout.findViewById<TextView>(R.id.lblValue).setText(
                    getString(
                        R.string.text_label_parameter_coin,
                        formatValue(it.value!!.toDouble())
                    )
                )

                lyContainerConcepts.addView(conceptItemLayout)


            }

            lblTotal.setText(
                getString(
                    R.string.text_label_parameter_coin,
                    formatValue(queryGiro.giroValue!!.toDouble())
                )
            )


            if (!DeviceUtil.isSalePoint()) {
                containerConsultReference.visibility = View.GONE
                containerResultReference.visibility = View.VISIBLE
            }
            showDialogProgress(getString(R.string.message_dialog_load_data))
            giroViewModel.getParametersGiros()

            txtNotes.setText(queryGiro.notes)
            txtNotes.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)
            txtNotes.isEnabled = false


        }

    }

    private fun navigateToEnrollUser(queryGiro: ResponseQueryGiroDTO) {

        showOkAlertDialog("", getString(R.string.giro_third_enroll), {
            goToEditUser(queryGiro.clientDestination, TypeCreateUser.USER_PAY_ENROLL)

        }, {
            //dismiss
            clearForm()
        })


    }

    private fun goToEditUser(thirdDestination: ClientFullDTO? = null, typeCreateUser: TypeCreateUser) {


        if (DeviceUtil.isSalePoint()) {

            (activity as GiroMenuActivity).navigateToOptionSalePointCreateUser(
                thirdDestination?.typeId ?: "",
                thirdDestination?.id ?: "",
                typeCreateUser
            )

        } else {

            (activity as GiroActivity).navigateToOptionCreateUser(
                thirdDestination?.typeId ?: "",
                thirdDestination?.id ?: "",
                typeCreateUser
            )

        }

    }

    private fun setupFragmentView() {

        initListenersViews()
        validatePayGiro()
    }

    private fun validatePayGiro() {

        val reference = arguments?.getString(RUtil.R_string(R.string.giro_ext_reference)).toString()

        if (reference.isNotEmpty() && reference != "null") {

            txtReferenceNumber.setText(reference)
            showDialogProgress(RUtil.R_string(R.string.message_dialog_load_data))
            giroViewModel.queryGiro(txtReferenceNumber.text.toString())

        }

    }

    private fun initListenersViews() {

        initListenersClick()
        initListenersTextWatcher()

    }

    private fun initListenersTextWatcher() {

        txtReferenceNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                if (txtReferenceNumber.text.isEmpty())
                    labelErrorReference.visibility = View.VISIBLE
                else
                    labelErrorReference.visibility = View.GONE
            }
        })

    }

    private fun initListenersClick() {

        btnConsult.setOnClickListener {
            hideKeyboard()
            if (txtReferenceNumber.text.toString().isNotEmpty()) {

                labelErrorReference.visibility = View.GONE
                queryGiro()

            } else {
                labelErrorReference.visibility = View.VISIBLE
            }

        }

        btnNext.setOnClickListener {
            hideKeyboard()
            if (responseQueryGiroDTO != null) {

                if (fingerPrintThird.data.isNotEmpty())
                    paymentGiro()
                else
                    failAuthSeller(getString(R.string.error_biometry))


            } else
                showOkAlertDialog("", getString(R.string.giro_payment_error))


        }


        initListenersClickBack()
    }

    fun queryGiro(){
        showDialogProgress("Consultando giro")
        giroViewModel.queryGiro(txtReferenceNumber.text.toString())
    }

    private fun initListenersClickBack() {

        btnBack.setOnClickListener {

            if (!DeviceUtil.isSalePoint())
                activity?.onBackPressed()
            else
                clearForm()
        }

        btnBackResultReference.setOnClickListener {

            responseQueryGiroDTO = null

            if (DeviceUtil.isSalePoint()) {
                activity?.onBackPressed()
            } else {
                containerConsultReference.visibility = View.VISIBLE
                containerResultReference.visibility = View.GONE
            }


        }
    }

    private fun validateAuthSeller() {

        validateAuthSeller(getString(R.string.shared_key_auth_mode_giro_pay)) { authType, data,dataImg ->

            when (authType) {
                AuthMode.NONE -> confirmPaymentGiro()
                AuthMode.TOKEN -> {

                    showDialogProgress(getString(R.string.message_dialog_login_auth))
                    authViewModel.authToken(
                        SharedPreferencesUtil.getPreference(getString(R.string.shared_key_user_id)),
                        data
                    )
                }
                AuthMode.BIOMETRY -> {

                    if (data.isNotEmpty()) {
                        showDialogProgress(getString(R.string.message_dialog_login_auth))
                        authViewModel.authFingerPrintSeller(
                            SharedPreferencesUtil.getPreference(getString(R.string.shared_key_user_id_type)),
                            SharedPreferencesUtil.getPreference(getString(R.string.shared_key_user_id)),
                            data
                        )
                    } else {
                        failAuthSeller(getString(R.string.error_biometry))
                    }


                }

            }

        }

    }

    private fun paymentGiro() {

        activity?.runOnUiThread {

            val inflater = this.layoutInflater

            val dialogView = inflater.inflate(R.layout.dialog_giro_fare_pay, null)

            val nameBeneficiary = "${responseQueryGiroDTO?.clientDestination?.firstName
                ?: ""} ${responseQueryGiroDTO?.clientDestination?.lastName ?: ""}"

            val valueGiro = getString(
                R.string.text_label_parameter_coin, formatValue(
                    responseQueryGiroDTO?.giroValue?.toDouble()
                        ?: 0.0
                )
            )

            val lblSafeValue = getString(R.string.text_label_parameter_coin, formatValue(0.0))

            var totalValue = responseQueryGiroDTO?.giroValue?.toDouble() ?: 0.0

            if (chkEnsure.isChecked) {
                dialogView.findViewById<TextView>(R.id.lblSafeValue).setText(lblSafeValue)
                totalValue += 0

            }

            val totalValueStr =
                getString(R.string.text_label_parameter_coin, formatValue(totalValue))

            dialogView.findViewById<TextView>(R.id.lblBeneficiary).setText(nameBeneficiary)
            dialogView.findViewById<TextView>(R.id.lblDocumentNumber)
                .setText(responseQueryGiroDTO?.clientDestination?.id)
            dialogView.findViewById<TextView>(R.id.lblEmail)
                .setText(responseQueryGiroDTO?.clientDestination?.email)
            dialogView.findViewById<TextView>(R.id.lblGiroValue).setText(valueGiro)

            dialogView.findViewById<TextView>(R.id.lblTotalValue).setText(totalValueStr)

            val dialog = AlertDialog.Builder(context!!).apply {
                setTitle(RUtil.R_string(R.string.dialog_giro_fare_pay_title))
                setView(dialogView)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, which ->
                    dialog.dismiss()

                    validateAuthSeller()

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

    private fun confirmPaymentGiro() {

        showDialogProgress(RUtil.R_string(R.string.message_dialog_request))

        var unusualOperationsTem = ""
        var unusualOperationsCodeTem = ""

        if (spinnerUnusualOperations.selectedIndex != 0) {

            unusualOperationsTem = spinnerUnusualOperations.text.toString()
            unusualOperationsCodeTem =
                unusualOperations.filter { it.contains(unusualOperationsTem) }.last().split("-")[1]

        }

        if (fingerPrintThird.data != getString(R.string.fingerprint_exonerate)){
            responseQueryGiroDTO?.clientDestination?.fingerPrint = fingerPrintThird.data
            responseQueryGiroDTO?.clientDestination?.imgFingerPrint = fingerPrintThird.dataImg
        }

        giroViewModel.paymentGiro(
            responseQueryGiroDTO!!,
            txtReferenceNumber.text.toString(),
            unusualOperationsTem,
            unusualOperationsCodeTem,
            txtObservations.text.toString(),
            txtNotes.text.toString(),
            chkSendEmail.isChecked

        )

    }

    override fun onResultData(huella: Array<out Array<String>>?) {

        if (huella != null) {

            showDialogProgress(getString(R.string.message_dialog_load_data))

            giroViewModel.authFingerPrintUser(
                responseQueryGiroDTO?.clientDestination?.typeId ?: "",
                responseQueryGiroDTO?.clientDestination?.id ?: "",
                huella.get(0).get(1)
            )

        } else {
            // error en biometria
        }

    }

    private fun clearForm() {

        responseQueryGiroDTO = null
        val conceptItemLayout =
            this.layoutInflater.inflate(R.layout.item_giro_payment_concepts, null)

        lyContainerConcepts.removeAllViews()

        lyContainerConcepts.addView(conceptItemLayout)

        lblDocumentNumberOrigin.setText(RUtil.R_string(R.string.giro_not_found))

        lblNameUserOrigen.setText(RUtil.R_string(R.string.giro_not_found))

        lblNameUserDestination.setText(RUtil.R_string(R.string.giro_not_found))

        lblDocumentNumberDestination.setText(RUtil.R_string(R.string.giro_not_found))

        lblDepartment.setText(RUtil.R_string(R.string.giro_not_found))

        lblCity.setText(RUtil.R_string(R.string.giro_not_found))

        lblAgency.setText(RUtil.R_string(R.string.giro_not_found))

        lblTotal.setText(RUtil.R_string(R.string.giro_not_found))

        lblSendEmail.setText(RUtil.R_string(R.string.giro_not_found))

        txtNotes.setText("")

        txtObservations.setText("")


    }

    private fun validateThirdFingerPrint(queryGiro: ResponseQueryGiroDTO) {

        if (!(queryGiro.clientDestination?.exoneratedFootprint ?: false)) {

            showAuthUserBiometria {data,dataImg->

                if (data.isNotEmpty()) {
                    showDialogProgress(getString(R.string.message_dialog_login_auth))

                    fingerPrintThird.apply {
                        this.data = data
                        this.dataImg = dataImg
                    }

                    giroViewModel.authFingerPrintUser(
                        queryGiro.clientDestination?.typeId ?: "",
                        queryGiro.clientDestination?.id ?: "",
                        fingerPrintThird.data)

                } else {
                    //Error en la huella
                    fingerPrintThird.apply { this.data = "" }
                    failAuthSeller(getString(R.string.error_biometry))
                }


            }
        } else {
            fingerPrintThird.apply { this.data = getString(R.string.fingerprint_exonerate) }
        }

    }

}
