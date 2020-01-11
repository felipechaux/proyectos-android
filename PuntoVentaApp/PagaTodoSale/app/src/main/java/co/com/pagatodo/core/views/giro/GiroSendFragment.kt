package co.com.pagatodo.core.views.giro


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.response.ResponseQueryThirdDTO
import co.com.pagatodo.core.data.dto.response.ResponseThirdDTO
import co.com.pagatodo.core.data.model.CaptureGiroModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.views.base.BaseFragment
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity
import co.com.pagatodo.core.views.login.AuthViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_giro_send.*


class GiroSendFragment : BaseFragment() {

    private lateinit var giroViewModel: GiroViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var documentType: MutableList<String>
    private lateinit var unusualOperations: MutableList<String>
    private lateinit var cities: MutableList<CitiesDTO>
    private lateinit var agencies: MutableList<AgencyDTO>
    private lateinit var listConcepts: MutableList<GiroConceptDTO>
    private var thirdOrigin: QueryThirdDTO? = null
    private var totalGiroValue: Int = 0
    private var thirdDestination: QueryThirdDTO? = null
    private var alertDialogo: AlertDialog? = null
    private var thirdOriginFull: ResponseQueryThirdDTO? = null
    private var thirdDestinationFull: ResponseQueryThirdDTO? = null
    private var concepts: List<GiroConceptDTO>? = null
    private var fingerPrintThird: FingerPrintGiroDTO = FingerPrintGiroDTO().apply { data = ""}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_giro_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()
    }

    private fun setupViewModel() {

        btnEditUserOriginSP.isEnabled = false
        btnEditUserOrigin.isEnabled = false

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
                is GiroViewModel.ViewEvent.ParameterSuccess -> loadParameters(
                    it.listDocumentType,
                    it.listUnusualOperations
                )
                is GiroViewModel.ViewEvent.Errors -> errorLoadParameters(it.errorMessage)
                is GiroViewModel.ViewEvent.CitiesSuccess -> loadCities(it.cities)
                is GiroViewModel.ViewEvent.AgenciesSuccess -> loadAgencies(it.agencies)
                is GiroViewModel.ViewEvent.ThirdSuccess -> loadThird(it.third, it.isOrigin)
                is GiroViewModel.ViewEvent.ThirdNotFound -> showAlertThirdNotFount(it.isOrigin)
                is GiroViewModel.ViewEvent.CalculateConceptsSuccess -> resultCalculateConcepts(it.concepts)
                is GiroViewModel.ViewEvent.CaptureGiroSuccess -> captureGiroSuccess()
                is GiroViewModel.ViewEvent.GiroAuthUserSuccess -> fingerPrintThirdSuccess()
                is GiroViewModel.ViewEvent.GiroAuthUserError -> fingerPrintThirdError(it.errorMessage)
            }


        })

        giroViewModel.getParametersGiros()
        showDialogProgress(getString(R.string.message_dialog_load_data))


    }

    private fun successAuthSeller() {
        hideDialogProgress()
        captureGiro()
    }

    private fun failAuthSeller(message: String) {
        hideDialogProgress()
        showOkAlertDialog("", message)
    }

    private fun captureGiroSuccess() {

        hideDialogProgress()

        showOkAlertDialog(
            getString(R.string.giro_send_success_title),
            getString(R.string.giro_send_success_message)
        ) {
            onBackPressed()
        }


    }

    private fun setupFragmentView() {

        initListenersViews()

    }

    private fun initListenersViews() {

        initListenersTextWatcher()
        initListenersOnClick()
        initListenerSeachView()

    }

    private fun initListenerSeachView() {


        txtDocumentNumberOrigin.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code

                if (txtDocumentNumberOrigin.text.isNotEmpty()) {

                    labelErrorDocumentNumberOrigin.visibility = View.GONE
                    showDialogProgress(getString(R.string.giro_send_search_third))
                    giroViewModel.getThird(
                        txtDocumentNumberOrigin.text.toString(),
                        spinnerDocumentTypeOrigin.text.toString(),
                        true
                    )
                } else {
                    labelErrorDocumentNumberOrigin.visibility = View.VISIBLE
                }

                return@OnKeyListener true
            }
            false
        })

        txtDocumentNumberDestination.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code

                if (txtDocumentNumberDestination.text.isNotEmpty()) {
                    labelErrorDocumentDestination.visibility = View.GONE
                    showDialogProgress(getString(R.string.giro_send_search_third))
                    giroViewModel.getThird(
                        txtDocumentNumberDestination.text.toString(),
                        spinnerDocumentTypeDestination.text.toString(),
                        false
                    )
                } else {
                    labelErrorDocumentDestination.visibility = View.VISIBLE
                }

                return@OnKeyListener true
            }
            false
        })

    }

    private fun initListenersOnClick() {

        txtCities.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            showDialogProgress(getString(R.string.giro_send_load_agency))
            val city = cities[position]

            resetAgency()

            giroViewModel.getAgencies(city.daneCodigo!!)
            hideKeyboard()
        }

        btnCalcular.setOnClickListener {
            hideKeyboard()
            calculateConcepts()

        }

        btnNextCalculate.setOnClickListener {
            hideKeyboard()
            if (validateCalculateConcepts()) {
                if (concepts != null) {

                    if (fingerPrintThird.data.isNotEmpty()) {

                        setupCalculateBackAmount()
                    } else {

                        showOkAlertDialog("", getString(R.string.text_error_fingerprint_origin)) {
                            validateThirdFingerPrint(thirdOriginFull?.third)
                        }
                    }


                } else {
                    showOkAlertDialog("", getString(R.string.giro_send_validate))
                }
            }

        }

        initListenersOnClickSearch()

        initListenersOnClickEditUser()

        initListenersOnClickBack()

    }

    private fun initListenersOnClickSearch() {


        btnSearchUserOrigin.setOnClickListener {
            hideKeyboard()
            //validar el documento
            if (txtDocumentNumberOrigin.text.isNotEmpty()) {

                labelErrorDocumentNumberOrigin.visibility = View.GONE
                searchUserOrigin()
            } else {
                labelErrorDocumentNumberOrigin.visibility = View.VISIBLE
            }


        }

        btnSearchUserDestination.setOnClickListener {
            hideKeyboard()
            //validar el documento
            if (txtDocumentNumberDestination.text.isNotEmpty()) {
                labelErrorDocumentDestination.visibility = View.GONE
                searchUserDestination()
            } else {
                labelErrorDocumentDestination.visibility = View.VISIBLE
            }
        }

    }

    fun searchUserDestination() {
        showDialogProgress(getString(R.string.giro_send_search_third))
        giroViewModel.getThird(
            txtDocumentNumberDestination.text.toString(),
            spinnerDocumentTypeDestination.text.toString(),
            false
        )
    }

    fun searchUserOrigin() {
        showDialogProgress(getString(R.string.giro_send_search_third))
        giroViewModel.getThird(
            txtDocumentNumberOrigin.text.toString(),
            spinnerDocumentTypeOrigin.text.toString(),
            true
        )
    }

    private fun initListenersOnClickBack() {

        btnBackCalculate.setOnClickListener {

            if (!DeviceUtil.isSalePoint()) {
                containerInit.visibility = View.VISIBLE
                containerCalculate.visibility = View.GONE
            } else {
                onBackPressed()
            }

        }

        btnBack.setOnClickListener {

            onBackPressed()
        }

    }

    private fun initListenersOnClickEditUser() {

        btnEditUserDestination.setOnClickListener {
            edituser(isOrigin = false)
        }

        btnEditUserDestinationSP.setOnClickListener {
            edituser(isOrigin = false)
        }

        btnEditUserOrigin.setOnClickListener {
            edituser()
        }

        btnEditUserOriginSP.setOnClickListener {
            edituser()
        }
    }

    private fun fingerPrintThirdSuccess() {
        hideDialogProgress()

    }

    private fun fingerPrintThirdError(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
        thirdOrigin = null
        fingerPrintThird.apply { this.data = "" }
        txtUserNamesOrigin.setText("")


    }

    private fun edituser(isOrigin: Boolean = true) {


        if (isOrigin) {

            if (thirdOrigin != null) {

                labelErrorDocumentNumberOrigin.visibility = View.GONE
                goToEditUser(thirdOrigin, TypeCreateUser.USER_SEND_UPDATE, isOrigin)
            } else {

                labelErrorDocumentNumberOrigin.visibility = View.VISIBLE
                labelErrorDocumentNumberOrigin.setText(RUtil.R_string(R.string.giro_send_error_document_number_origin))
            }

        } else {

            if (thirdDestination != null) {

                labelErrorDocumentDestination.visibility = View.GONE
                goToEditUser(thirdDestination, TypeCreateUser.USER_SEND_UPDATE, isOrigin)
            } else {

                labelErrorDocumentDestination.visibility = View.VISIBLE
                labelErrorDocumentDestination.setText(RUtil.R_string(R.string.giro_send_error_document_number_destination))
            }

        }

    }

    private fun goToEditUser(
        third: QueryThirdDTO? = null,
        typeCreateUser: TypeCreateUser,
        isOrigin: Boolean
    ) {

        saveLocalThird(isOrigin)
        if (DeviceUtil.isSalePoint()) {

            (activity as GiroMenuActivity).navigateToOptionSalePointCreateUser(
                third?.typeId ?: "",
                third?.id ?: "",
                typeCreateUser
            )

        } else {

            (activity as GiroActivity).navigateToOptionCreateUser(
                third?.typeId ?: "",
                third?.id ?: "", typeCreateUser
            )

        }

    }

    private fun setupCalculateBackAmount() {

        activity?.runOnUiThread {

            val inflater = this.layoutInflater

            val dialogView = inflater.inflate(R.layout.dialog_giro_calculate_change, null)


            dialogView.findViewById<TextView>(R.id.lblValue)
                .setText(
                    getString(
                        R.string.text_label_parameter_coin,
                        formatValue(totalGiroValue.toDouble())
                    )
                )

            val txtAmountReceived = dialogView.findViewById<EditText>(R.id.txtAmountReceived)

            val lblAmountBack = dialogView.findViewById<TextView>(R.id.lblAmountBack)

            val dialog = AlertDialog.Builder(context!!).apply {
                setTitle(RUtil.R_string(R.string.dialog_giro_calculate_title))
                setView(dialogView)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, which ->

                    dialog.dismiss()
                    showDialogFareSend()

                }
            }.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false

            editTextAmountTextWatcher(txtAmountReceived, lblAmountBack, dialog)

            dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.secondaryText
                )
            )


        }

    }

    private fun editTextAmountTextWatcher(
        txtAmountReceived: EditText,
        lblAmountBack: TextView,
        dialog: AlertDialog
    ) {

        var istxtAmountReceivedWatcher = false
        txtAmountReceived.addTextChangedListener(object : TextWatcher {

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
                if (!istxtAmountReceivedWatcher) {
                    istxtAmountReceivedWatcher = true

                    if (txtAmountReceived.text.toString().isNotEmpty()) {
                        try {
                            var originalString = txtAmountReceived.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value =
                                if (originalString.isEmpty()) 0.0 else originalString.toDouble()
                            txtAmountReceived.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            txtAmountReceived.setSelection(txtAmountReceived.text.length)

                            validateTextAmount(value,lblAmountBack,dialog)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }

                    }

                    istxtAmountReceivedWatcher = false
                }


            }
        })

    }

    private fun validateTextAmount(value:Double,lblAmountBack:TextView,dialog:AlertDialog){

        if (value >= totalGiroValue) {

            lblAmountBack.setText(
                getString(
                    R.string.text_label_parameter_coin,
                    formatValue(value - totalGiroValue)
                )
            )

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
        } else {
            lblAmountBack.setText("")
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        }
    }

    private fun showDialogFareSend() {

        activity?.runOnUiThread {

            val shippingValue =
                listConcepts.filter { concept -> concept.code == RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_VALUE) }
                    .sumBy { concept -> concept.value!! }
            val freightValue =
                listConcepts.filter { concept -> concept.code == RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_FREIGHT) }
                    .sumBy { concept -> concept.value!! }

            val othersValue =
                listConcepts.filter { concept ->
                    concept.code != RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_FREIGHT) &&
                            concept.code != RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_VALUE)
                }
                    .sumBy { concept -> concept.value!! }


            val inflater = this.layoutInflater

            val dialogView = inflater.inflate(R.layout.dialog_giro_fare_send, null)

            dialogView.findViewById<TextView>(R.id.lblCityDestination)
                .setText(txtCities.text.toString())
            dialogView.findViewById<TextView>(R.id.lblDocumentNumberDestination)
                .setText(thirdDestination?.id)
            dialogView.findViewById<TextView>(R.id.lblSendAmound)
                .setText(
                    getString(
                        R.string.text_label_parameter_coin,
                        formatValue(shippingValue.toDouble())
                    )
                )
            dialogView.findViewById<TextView>(R.id.lblSendFreightValue)
                .setText(
                    getString(
                        R.string.text_label_parameter_coin,
                        formatValue(freightValue.toDouble())
                    )
                )

            dialogView.findViewById<TextView>(R.id.lblSendOthersValue)
                .setText(
                    getString(
                        R.string.text_label_parameter_coin,
                        formatValue(othersValue.toDouble())
                    )
                )

            val dialog = AlertDialog.Builder(context!!).apply {
                setTitle(RUtil.R_string(R.string.dialog_giro_fare_send_title))
                setView(dialogView)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.text_btn_send_giro)) { dialog, which ->
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

    private fun validateAuthSeller() {


        validateAuthSeller(getString(R.string.shared_key_auth_mode_giro_capture)) { authType, data,dataImg ->

            when (authType) {
                AuthMode.NONE -> captureGiro()
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

    private fun captureGiro() {


        showDialogProgress(RUtil.R_string(R.string.message_dialog_request))

        val clientOriginDTO = ClientGiroDTO().apply {
            this.id = thirdOrigin?.id
            this.typeId = thirdOrigin?.typeId
            this.isEnrolled = true
            this.fingerPrint = fingerPrintThird.data
            this.imgFingerPrint = fingerPrintThird.dataImg
        }


        var unusualOperationsTem = ""
        var unusualOperationsCodeTem = ""

        if (spinnerUnusualOperations.selectedIndex != 0) {

            unusualOperationsTem = spinnerUnusualOperations.text.toString()
            unusualOperationsCodeTem =
                unusualOperations.filter { it.contains(unusualOperationsTem) }.last().split("-")[1]

        }

        val captureGiroModel = CaptureGiroModel().apply {
            this.value = totalGiroValue
            this.includesFreight = chkFreight.isChecked
            this.clientGiro = clientOriginDTO
            this.clientDestination = thirdDestination!!
            this.concepts = listConcepts
            this.agency = agencies[spinnerDestinationAgency.selectedIndex]
            this.clientDestinationFull = thirdDestinationFull?.third
            this.clientOriginFull = thirdOriginFull?.third
            this.unusualOperations = unusualOperationsTem
            this.unusualOperationsCode = unusualOperationsCodeTem
            this.description = txtObservations.text.toString()
        }

        giroViewModel.captureGiro(captureGiroModel)
    }

    private fun initListenersTextWatcher() {


        txtCities.addTextChangedListener(object : TextWatcher {

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

                labelErrorDestinationAgency.visibility = View.GONE
                labelErrorCities.visibility = View.GONE
                resetAgency()
                if (txtCities.length() == 3) {
                    showDialogProgress(getString(R.string.message_dialog_load_data))
                    giroViewModel.getCities(txtCities.text.toString().toUpperCase())

                }

            }
        })

        txtDocumentNumberDestination.addTextChangedListener(object : TextWatcher {

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

                labelErrorDocumentDestination.visibility = View.GONE

            }
        })

        txtDocumentNumberOrigin.addTextChangedListener(object : TextWatcher {

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

                labelErrorDocumentNumberOrigin.visibility = View.GONE

            }
        })

        initListenersTextWatcherRX()
    }

    @SuppressLint("CheckResult")
    private fun initListenersTextWatcherRX() {

        var isTextChanged = true

        txtGiroValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (isTextChanged) {

                    isTextChanged = false

                    if (txtGiroValue.text.toString().isNotEmpty()) {
                        labelErrorGiroValue.visibility = View.GONE
                        try {
                            var originalString = txtGiroValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")

                            validateValueValid(txtGiroValue, originalString)

                            txtGiroValue.setSelection(txtGiroValue.text.length)

                            validateMinValueGiro(txtGiroValue)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    }

                    isTextChanged = true

                }

            }
        })


    }

    private fun validateValueValid(txtGiroValue: EditText, originalString: String) {

        val value = originalString.toDouble()
        if (txtGiroValue.text.toString().length > 4) {

            val result = (getString(
                R.string.text_label_parameter_coin,
                formatValue(value)
            ))
            val strValue = "${result.substring(0, result.length - 2)}00"
            txtGiroValue.setText(strValue)
        } else {
            txtGiroValue.setText(
                getString(
                    R.string.text_label_parameter_coin,
                    formatValue(value)
                )
            )
        }
    }

    private fun validateMinValueGiro(txtGiroValue: EditText) {

        if (resetValueFormat(txtGiroValue.text.toString()).toInt() < RUtil.R_string(
                R.string.GIRO_MIN_VALUE
            ).toInt()
        ) {

            labelErrorGiroValue.visibility = View.VISIBLE
            labelErrorGiroValue.setText(RUtil.R_string(R.string.giro_send_error_amount_send_value))
        } else {
            labelErrorGiroValue.visibility = View.GONE

        }

    }

    private fun resultCalculateConcepts(concepts: List<GiroConceptDTO>) {


        // si se caLCULA CORRECTAMENTE
        if (!DeviceUtil.isSalePoint()) {
            containerInit.visibility = View.GONE
            containerCalculate.visibility = View.VISIBLE
        }

        this.concepts = concepts

        setupConcepst(concepts)
        hideDialogProgress()

    }

    private fun showAlertThirdNotFount(origin: Boolean) {

        var temQueryThirdDTO: QueryThirdDTO? = null

        if (origin) {

            temQueryThirdDTO = QueryThirdDTO().apply {
                this.id = txtDocumentNumberOrigin.text.toString()
                this.typeId = spinnerDocumentTypeOrigin.text.toString()
            }

            thirdOrigin = null
            thirdOriginFull = null
            txtUserNamesOrigin.setText("")

        } else {
            temQueryThirdDTO = QueryThirdDTO().apply {
                this.id = txtDocumentNumberDestination.text.toString()
                this.typeId = spinnerDocumentTypeDestination.text.toString()
            }
            thirdDestination = null
            thirdDestinationFull = null
            txtUserNamesDestination.setText("")

        }


        hideDialogProgress()
        if (alertDialogo == null || alertDialogo?.isShowing != true) {

            activity?.runOnUiThread {
                alertDialogo = AlertDialog.Builder(context!!).apply {
                    setTitle(RUtil.R_string(R.string.giro_send_dialog_title_create_user))
                    setMessage(RUtil.R_string(R.string.giro_send_dialog_msm_create_user))
                    setCancelable(false)
                    setNegativeButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, which ->
                        dialog.dismiss()


                    }
                    setPositiveButton(getString(R.string.giro_send_dialog_btn_create_user)) { dialog, which ->
                        dialog.dismiss()

                        goToEditUser(
                            temQueryThirdDTO,
                            if (origin) TypeCreateUser.USER_SEND_CREATE_ORIGEN else TypeCreateUser.USER_SEND_CREATE_DESTINATION,
                            origin
                        )

                    }
                }.show()

                alertDialogo?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.secondaryText
                    )
                )
            }

        }


    }

    private fun loadThird(response: ResponseQueryThirdDTO?, isOrigin: Boolean) {




        val fullName = "${response?.third?.firstName} ${response?.third?.lastName}"

        val temThird = QueryThirdDTO()

        temThird.apply {
            this.id = response?.third?.id
            this.typeId = response?.third?.typeId

        }

        if (isOrigin) {
            fingerPrintThird = FingerPrintGiroDTO().apply { this.data = ""}
            thirdOriginFull = response
            thirdOrigin = temThird

            if (isOrigin && !(response?.third?.isEnrolled ?: false)) {


                showOkAlertDialog("", getString(R.string.giro_third_enroll)) {
                    hideDialogProgress()
                    goToEditUser(thirdOrigin, TypeCreateUser.USER_SEND_ENROLL, isOrigin)
                }

                return;
            }

            txtUserNamesOrigin.setText(fullName)

            validateThirdFingerPrint(response?.third)

        } else {

            thirdDestinationFull = response
            thirdDestination = temThird
            txtUserNamesDestination.setText(fullName)

        }

        hideDialogProgress()

    }

    private fun validateThirdFingerPrint(third: ResponseThirdDTO?) {

        if (third == null)
            return

        if (!(third.isExoneratedFootprint ?: false)) {

            showAuthUserBiometria {data,dataImg ->

                if (data.isNotEmpty()) {
                    showDialogProgress(getString(R.string.message_dialog_login_auth))

                    fingerPrintThird.apply {
                        this.data = data
                        this.dataImg =dataImg
                    }

                    giroViewModel.authFingerPrintUser(
                        thirdOrigin?.typeId ?: "",
                        thirdOrigin?.id ?: "",
                        fingerPrintThird.data
                    )
                    btnEditUserOriginSP.isEnabled = true
                    btnEditUserOrigin.isEnabled = true
                } else {
                    //Error en la huella
                    fingerPrintThird.apply { this.data = "" }
                    failAuthSeller(getString(R.string.error_biometry))

                }


            }
        } else {
            fingerPrintThird.apply { this.data = getString(R.string.fingerprint_exonerate) }

            btnEditUserOriginSP.isEnabled = true
            btnEditUserOrigin.isEnabled = true
        }

    }

    private fun loadAgencies(agencies: List<AgencyDTO>?) {

        this.agencies = agencies as MutableList<AgencyDTO>

        val items = mutableListOf<String>()
        items.add(0, getString(R.string.giro_send_placeholder_agency_destination))
        agencies.forEachIndexed { index, item ->
            if (index != 0) {
                items.add(item.name.toString())
            }
        }
        spinnerDestinationAgency.setItems(items)
        spinnerDestinationAgency.setOnItemSelectedListener { view, position, id, item ->
            labelErrorDestinationAgency.visibility = View.GONE
        }
        hideDialogProgress()

    }

    private fun loadCities(cities: List<CitiesDTO>?) {

        this.cities = cities as MutableList<CitiesDTO>

        val dataCities: List<String>? = cities?.map {
            "${it.cityName} - ${it.departamentName}"
        }

        val adapter = ArrayAdapter<String>(
            this.activity!!, // Context
            android.R.layout.select_dialog_item, // Layout
            dataCities as MutableList<String> // Array
        )

        txtCities.threshold = 1
        txtCities.setAdapter(adapter)

        hideDialogProgress()

    }

    private fun loadParameters(
        listDocumentType: List<String>?,
        listUnusualOperations: List<String>?
    ) {

        documentType = listDocumentType as MutableList<String>
        unusualOperations = listUnusualOperations as MutableList<String>

        val dataDocumentType = documentType.map {
            it.split("-")[0]
        }

        val dataUnusualOperations = unusualOperations.map {
            it.split("-")[0]
        }

        spinnerDocumentTypeOrigin.setItems(dataDocumentType)
        spinnerDocumentTypeOrigin.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        spinnerDocumentTypeDestination.setItems(dataDocumentType)
        spinnerDocumentTypeDestination.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        val itemsUnusualOperations = mutableListOf<String>()

        itemsUnusualOperations.addAll(dataUnusualOperations)
        itemsUnusualOperations.add(0, getString(R.string.giro_send_placeholder_unusual_operations))

        spinnerUnusualOperations.setItems(itemsUnusualOperations)

        hideDialogProgress()


    }

    private fun errorLoadParameters(error: String) {

        hideDialogProgress()
        showOkAlertDialog("", error)

    }

    private fun calculateConcepts() {

        if (validateCalculateConcepts()) {

            var valueString = txtGiroValue.text.toString()
            valueString = valueString.replace(".", "").replace("$", "")

            showDialogProgress(getString(R.string.giro_send_calculate_concepts))

            giroViewModel.calculateConcepts(
                thirdOrigin!!,
                thirdDestination!!,
                valueString.toInt(),
                chkFreight.isChecked,
                agencies[spinnerDestinationAgency.selectedIndex].code
                    ?: ""
            )

        }

    }

    private fun validateCalculateConcepts(): Boolean {

        var isValid = true

        if (txtGiroValue.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroValue.visibility = View.VISIBLE
            labelErrorGiroValue.setText(RUtil.R_string(R.string.giro_send_error_amount_send))
        } else {
            labelErrorGiroValue.visibility = View.GONE
        }

        if (txtCities.text.toString().isEmpty()) {
            isValid = false
            labelErrorCities.visibility = View.VISIBLE
            labelErrorCities.setText(RUtil.R_string(R.string.giro_send_error_city_destination))
        } else {
            labelErrorCities.visibility = View.GONE
        }

        if (spinnerDestinationAgency.text.toString().isEmpty()) {
            isValid = false
            labelErrorDestinationAgency.visibility = View.VISIBLE
            labelErrorDestinationAgency.setText(RUtil.R_string(R.string.giro_send_error_amount_send))
        } else {
            labelErrorDestinationAgency.visibility = View.GONE

        }

        if (thirdOrigin == null) {
            isValid = false
            labelErrorDocumentNumberOrigin.visibility = View.VISIBLE
            labelErrorDocumentNumberOrigin.setText(RUtil.R_string(R.string.giro_send_error_document_number_origin))
        } else {
            labelErrorDocumentNumberOrigin.visibility = View.GONE
        }

        if (thirdDestination == null) {

            isValid = false
            labelErrorDocumentDestination.visibility = View.VISIBLE
            labelErrorDocumentDestination.setText(RUtil.R_string(R.string.giro_send_error_document_number_destination))

        } else {

            labelErrorDocumentDestination.visibility = View.GONE
        }

        if (spinnerDestinationAgency.text.toString().isEmpty() || spinnerDestinationAgency.text.toString() == getString(
                R.string.giro_send_placeholder_agency_destination
            )
        ) {
            isValid = false
            labelErrorDestinationAgency.visibility = View.VISIBLE
        } else {
            labelErrorDestinationAgency.visibility = View.GONE
        }


        return isValid
    }

    private fun setupConcepst(concepts: List<GiroConceptDTO>) {

        this.listConcepts = concepts as MutableList<GiroConceptDTO>
        val shippingValue =
            concepts.filter { concept -> concept.code == RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_VALUE) }
                .sumBy { concept -> concept.value!! }
        val freightValue =
            concepts.filter { concept -> concept.code == RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_FREIGHT) }
                .sumBy { concept -> concept.value!! }
        val others = concepts.filter { concept ->
            concept.code != RUtil.R_string(R.string.GIRO_CAPTURE_CONCEPT_CODE_VALUE) && concept.code != RUtil.R_string(
                R.string.GIRO_CAPTURE_CONCEPT_CODE_FREIGHT
            )
        }
            .sumBy { concept -> concept.value!! }

        totalGiroValue = concepts.sumBy { concept -> concept.value!! }

        lblShippingValue.setText(
            getString(
                R.string.text_label_parameter_coin,
                formatValue(shippingValue.toDouble())
            )
        )
        lblFreightValue.setText(
            getString(
                R.string.text_label_parameter_coin,
                formatValue(freightValue.toDouble())
            )
        )
        lblOthers.setText(
            getString(
                R.string.text_label_parameter_coin,
                formatValue(others.toDouble())
            )
        )
        lblTotalValue.setText(
            getString(
                R.string.text_label_parameter_coin,
                formatValue(totalGiroValue.toDouble())
            )
        )

    }

    private fun resetValueFormat(amount: String) = amount.replace("$", "").replace(".", "")

    private fun resetAgency() {

        val items = mutableListOf<String>()
        spinnerDestinationAgency.setItems(items)
    }

    private fun saveLocalThird(isOrigin: Boolean) {

        val jsonUser = Gson().toJson(if (isOrigin) thirdOriginFull else thirdDestinationFull)
        SharedPreferencesUtil.savePreference(
            RUtil.R_string(R.string.shared_key_user_giro_edit),
            jsonUser
        )
    }

    override fun onDetach() {
        super.onDetach()

        giroViewModel.singleLiveEvent.postValue(null);

    }
}
