package co.com.pagatodo.core.views.giro


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.CitiesDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import co.com.pagatodo.core.data.dto.response.CountryDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryThirdDTO
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.views.base.BaseFragment
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_giro_send.btnBack
import kotlinx.android.synthetic.main.fragment_giro_users.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import java.text.SimpleDateFormat
import java.util.*

data class EditDates(val day: Int, val month: Int, val year: Int)

class GiroUsersFragment : BaseFragment() {

    private lateinit var bioFacade: com.datacenter.fingerprintlibrary.BioFacade
    private lateinit var bioFacadeQ2: com.datacenter.fingerprintlibraryq2.BioFacade
    private lateinit var giroViewModel: GiroViewModel
    private lateinit var documentType: MutableList<String>
    private lateinit var economyActivity: MutableList<String>
    private lateinit var typePerson: MutableList<String>
    private lateinit var socialStratum: MutableList<String>
    private lateinit var countries: MutableList<CountryDTO>
    private lateinit var cities: MutableList<CitiesDTO>
    private var city: Boolean = false
    private var cityResidenceCode: String = ""
    private var cityExpedition: Boolean = false
    private var cityExpeditionCode: String = ""
    private var countryExpeditionCode: String = ""
    private var datesBirth: EditDates = EditDates(0, 0, 0)
    private var datesExpedition: EditDates = EditDates(0, 0, 0)
    private var datesEntryCountry = EditDates(0, 0, 0)
    private var datesExpirePassport = EditDates(0, 0, 0)
    var typeOperation = TypeCreateUser.USER_CREATE_DEFAULT
    private var documentTypeSend: String = ""
    private var documentNumberSend: String = ""
    private var fingerPrintThirdL = ""
    private var fingerPrintThirdR = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giro_users, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        documentNumberSend =
            arguments?.getString(RUtil.R_string(R.string.giro_ext_document)).toString()
        documentTypeSend =
            arguments?.getString(RUtil.R_string(R.string.giro_ext_type_document)).toString()

        when (arguments?.getInt(RUtil.R_string(R.string.giro_ext_type_operation)) ?: 0) {

            1 -> typeOperation = TypeCreateUser.USER_CREATE_DEFAULT
            2 -> typeOperation = TypeCreateUser.USER_UPDATE_DEFAULT
            3 -> typeOperation = TypeCreateUser.USER_SEND_CREATE_ORIGEN
            4 -> typeOperation = TypeCreateUser.USER_SEND_CREATE_DESTINATION
            5 -> typeOperation = TypeCreateUser.USER_SEND_UPDATE
            6 -> typeOperation = TypeCreateUser.USER_PAY_ENROLL
            7 -> typeOperation = TypeCreateUser.USER_SEND_ENROLL

        }

        setupViewModel()
        setupFragmentView()


    }

    private fun setupViewModel() {

        giroViewModel = ViewModelProviders.of(this@GiroUsersFragment).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this@GiroUsersFragment, Observer {

            when (it) {
                is GiroViewModel.ViewEvent.ParameterSuccess -> loadParameters(
                    it.listDocumentType,
                    it.listEconomicActivity,
                    it.listTypePerson,
                    it.listSocialStratum,
                    it.listCountryDTO
                )
                is GiroViewModel.ViewEvent.CitiesSuccess -> loadCities(it.cities)
                is GiroViewModel.ViewEvent.CreateThirdSuccess -> createThirdSuccess()
                is GiroViewModel.ViewEvent.UpdateThirdSuccess -> updateThirdSuccess()
                is GiroViewModel.ViewEvent.ThirdSuccess -> {
                    if (typeOperation == TypeCreateUser.USER_CREATE_DEFAULT)
                        typeOperation = TypeCreateUser.USER_UPDATE_DEFAULT

                    loadThird(it.third)
                }
                is GiroViewModel.ViewEvent.ThirdNotFound -> {
                    hideDialogProgress()
                }
                is GiroViewModel.ViewEvent.Errors -> showErrors(it.errorMessage)
            }
        })
        showDialogProgress(getString(R.string.message_dialog_load_data))

        giroViewModel.getParametersGiros()

    }

    private fun setupFragmentView() {

        initListenersViews()
        bioFacade = com.datacenter.fingerprintlibrary.BioFacade.getInstance(activity)
        bioFacadeQ2 = com.datacenter.fingerprintlibraryq2.BioFacade.getInstance(activity, activity)

    }

    private fun initListenersViews() {

        initListenersTextWatcher()
        initListenersOnClick()
        initFocusChange()
        initSearchEvent()

    }

    private fun initSearchEvent() {

        txtDocumentNumber.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code

                if (txtDocumentNumber.text.isNotEmpty()) {

                    showDialogProgress(getString(R.string.text_load_data))
                    giroViewModel.getThird(
                        txtDocumentNumber.text.toString(),
                        spinnerTypeIdentification.text.toString(),
                        false
                    )
                }

                return@OnKeyListener true
            }
            false
        })


    }

    private fun initFocusChange() {

        initFocusChange1()
        initFocusChange2()
        initFocusChange3()


    }

    private fun initFocusChange1() {

        txtDocumentNumber.setOnFocusChangeListener { view, focus ->

            if (!focus && txtDocumentNumber.text.isEmpty())
                labelErrorGiroDocumentNumber.visibility = View.VISIBLE
            else
                labelErrorGiroDocumentNumber.visibility = View.GONE
        }

        txtNames.setOnFocusChangeListener { view, focus ->

            if (!focus && txtNames.text.isEmpty())
                labelErrorGiroNames.visibility = View.VISIBLE
            else
                labelErrorGiroNames.visibility = View.GONE
        }

        txtFirstSurname.setOnFocusChangeListener { view, focus ->

            if (!focus && txtFirstSurname.text.isEmpty())
                labelErrorGiroFirstSurname.visibility = View.VISIBLE
            else
                labelErrorGiroFirstSurname.visibility = View.GONE
        }


    }

    private fun initFocusChange2() {

        txtNumber.setOnFocusChangeListener { view, focus ->

            if (!focus && txtNumber.text.isEmpty())
                labelErrorGiroNumber.visibility = View.VISIBLE
            else
                labelErrorGiroNumber.visibility = View.GONE
        }

    }

    private fun initFocusChange3() {

        txtAddress.setOnFocusChangeListener { view, focus ->

            if (!focus && txtAddress.text.isEmpty())
                labelErrorGiroAddress.visibility = View.VISIBLE
            else
                labelErrorGiroAddress.visibility = View.GONE
        }


        txtCity.setOnFocusChangeListener { view, focus ->

            if (!focus && txtCity.text.isEmpty())
                labelErrorGiroCity.visibility = View.VISIBLE
            else
                labelErrorGiroCity.visibility = View.GONE
        }

        txtCountryExpedition.setOnFocusChangeListener { view, focus ->

            if (!focus && txtCountryExpedition.text.isEmpty())
                labelErrorCountryExpedition.visibility = View.VISIBLE
            else
                labelErrorCountryExpedition.visibility = View.GONE
        }


    }

    private fun initListenersTextWatcher() {

        txtDocumentNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroDocumentNumber.visibility = View.GONE

            }
        })

        spinnerTypePerson.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroTypePerson.visibility = View.GONE

            }
        })

        txtNames.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroNames.visibility = View.GONE

            }
        })

        txtFirstSurname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroFirstSurname.visibility = View.GONE

            }
        })

        txtNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroNumber.visibility = View.GONE

            }
        })

        txtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }


            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (!validateEmail(txtEmail.text.toString()) && txtEmail.text.toString().length > 0)
                    labelErrorGiroEmailInvalid.visibility = View.VISIBLE
                else
                    labelErrorGiroEmailInvalid.visibility = View.GONE

            }
        })

        spinnerSocialStratum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroSocialStratum.visibility = View.GONE

            }
        })

        txtAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroAddress.visibility = View.GONE

            }
        })

        txtCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroCity.visibility = View.GONE
                cityResidenceCode = ""
                if (txtCity.length() == 3) {
                    Log.e("city", "citychangued")
                    showDialogProgress(getString(R.string.message_dialog_load_data))
                    giroViewModel.getCities(txtCity.text.toString().toUpperCase())
                    city = true
                }

            }
        })

        txtDateBirth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroDateBirth.visibility = View.GONE

            }
        })

        txtDateExpedition.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorDateExpedition.visibility = View.GONE

            }
        })

        txtCountryExpedition.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorCountryExpedition.visibility = View.GONE
                countryExpeditionCode = ""

            }
        })

        txtCityExpedition.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorGiroCityExpedition.visibility = View.GONE
                cityExpeditionCode = ""
                if (txtCityExpedition.length() == 3) {
                    showDialogProgress(getString(R.string.message_dialog_load_data))
                    giroViewModel.getCities(txtCityExpedition.text.toString().toUpperCase())
                    cityExpedition = true
                }

            }
        })


    }

    private fun initListenersOnClick() {


        btnBack.setOnClickListener {

            if (typeOperation == TypeCreateUser.USER_PAY_ENROLL ||
                typeOperation == TypeCreateUser.USER_SEND_ENROLL ||
                typeOperation == TypeCreateUser.USER_SEND_CREATE_DESTINATION ||
                typeOperation == TypeCreateUser.USER_SEND_CREATE_ORIGEN ||
                typeOperation == TypeCreateUser.USER_SEND_UPDATE
            ) {

                if (DeviceUtil.isSalePoint()) {

                    giroMenuActivity().navigateToBackSalePoint(typeOperation)

                } else {
                    giroActivity().navigateToBack(typeOperation)
                }

            } else {
                activity?.onBackPressed()
            }


        }

        btnNext.setOnClickListener {
            listenersBtnNext()
        }

        txtDateBirth.setOnClickListener {
            showDialogDatePicker(txtDateBirth)
        }

        txtDateExpedition.setOnClickListener {
            showDialogDatePicker(txtDateExpedition)
        }

        txtDateEntryCountry.setOnClickListener {
            showDialogDatePicker(txtDateEntryCountry)
        }

        txtDateExpirePassport.setOnClickListener {
            showDialogDatePicker(txtDateExpirePassport)
        }

        initListenersOnClick2()

    }


    private fun initListenersOnClick2() {


        txtCity.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            val city = cities[position]
            cityResidenceCode = city?.daneCodigo.toString()
            Log.e("city", "${city.daneCodigo}")
            labelErrorGiroCityInvalid.visibility = View.GONE

        }

        txtCityExpedition.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->


                val cityexp = cities[position]
                cityExpeditionCode = cityexp?.daneCodigo.toString()
                Log.e("cityexp", "$cityExpeditionCode")
                labelErrorGiroCityExpeditionInvalid.visibility = View.GONE
            }

        txtCountryExpedition.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->


                val countryExp = countries[position]
                countryExpeditionCode = countryExp?.code.toString()
                labelErrorCountryExpeditionInvalid.visibility = View.GONE
            }

        initListenersFingerPrint()


    }

    private fun initListenersFingerPrint() {

        btnFingerPrint.setOnClickListener {

            if (fingerPrintThirdL.isEmpty() && fingerPrintThirdR.isEmpty()) {


                if (DeviceUtil.isSalePoint()) {
                    initListenersFingerPrintSalePoint()

                } else {

                    if (DatafonoType.Q2.type == DeviceUtil.getDeviceModel()) {

                        initListenersFingerPrintQ2()

                    }

                }


            }

        }
    }


    private fun initListenersFingerPrintSalePoint() {

        val call = object : com.datacenter.fingerprintlibrary.IResultListener {
            override fun onResultData(huella: Array<out Array<String>>?) {

                if ((huella?.get(0)?.get(1) ?: "").isNotEmpty() && (huella?.get(1)?.get(
                        1
                    ) ?: "").isNotEmpty()
                ) {

                    fingerPrintThirdR = huella?.get(0)?.get(1) ?: ""
                    fingerPrintThirdL = huella?.get(1)?.get(1) ?: ""
                    btnFingerPrint.setImageResource(R.drawable.ic_fingerprint_correct)
                    btnFingerPrint.isEnabled = false

                } else {
                    showErrors(getString(R.string.error_biometry))
                }


            }

        }
        bioFacade.setListener(call)

        bioFacade.procesar(
            arrayOf(
                com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_INDICE,
                com.datacenter.fingerprintlibrary.HuellaEnum.IZQUIERDA_INDICE
            ),
            false
        )
    }

    private fun initListenersFingerPrintQ2() {

        val call = object : com.datacenter.fingerprintlibraryq2.IResultListener {
            override fun onResultData(huella: Array<out Array<String>>?) {

                if ((huella?.get(0)?.get(1)
                        ?: "").isNotEmpty() && (huella?.get(1)?.get(1)
                        ?: "").isNotEmpty()
                ) {

                    fingerPrintThirdR = huella?.get(0)?.get(1) ?: ""
                    fingerPrintThirdL = huella?.get(1)?.get(1) ?: ""
                    btnFingerPrint.setImageResource(R.drawable.ic_fingerprint_correct)
                    btnFingerPrint.isEnabled = false

                } else {
                    showErrors(getString(R.string.error_biometry))
                }


            }

        }
        bioFacadeQ2.setListener(call)

        bioFacadeQ2.procesar(
            arrayOf(
                com.datacenter.fingerprintlibraryq2.HuellaEnum.DERECHA_INDICE,
                com.datacenter.fingerprintlibraryq2.HuellaEnum.IZQUIERDA_INDICE
            ),
            false
        )
    }

    private fun loadParameters(
        listDocumentType: List<String>?,
        listEconomyActivity: List<String>?,
        listTypePerson: List<String>?,
        listSocialStratum: List<String>?,
        countries: List<CountryDTO>?
    ) {

        documentType = listDocumentType as MutableList<String>
        economyActivity = listEconomyActivity as MutableList<String>
        typePerson = listTypePerson as MutableList<String>
        socialStratum = listSocialStratum as MutableList<String>


        typePerson.add(0, getString(R.string.giro_users_placeholder_type_person))
        socialStratum.add(0, getString(R.string.giro_users_placeholder_social_stratum))

        val dataDocumentType = documentType.map {
            it.split("-")[0]
        }
        val economyActivity = economyActivity.map {
            it.split("-")[0].trim()
        }

        val typePerson = typePerson.map {
            it.split("(")[0].trim()
        }
        val socialStratum = socialStratum.map {
            it.split("-")[0]
        }

        spinnerTypeIdentification.setItems(dataDocumentType)
        spinnerTypeIdentification.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        txtCountryExpedition.isEnabled = false
        txtCountryExpedition.setText("COLOMBIA")
        countryExpeditionCode = "56"
        txtCountryExpedition.setBackgroundResource(R.drawable.txt_background_inactive)

        spinnerTypeIdentification.setOnItemSelectedListener { view, position, id, item ->

            listenerSpinnerTypeIdentification(item.toString())

        }

        spinnerTypePerson.isEnabled = false
        spinnerTypePerson.setItems(typePerson)
        spinnerTypePerson.selectedIndex =
            typePerson.indexOf(getString(R.string.giro_user_type_person_natural))

        spinnerEconomyActivity.setItems(economyActivity)
        spinnerEconomyActivity.selectedIndex = 0


        spinnerSocialStratum.setItems(socialStratum)


        this.countries = countries as MutableList<CountryDTO>
        val dataCountries: List<String>? = countries?.map {
            "${it.name}"
        }
        val adapter = ArrayAdapter<String>(
            this.activity!!, // Context
            android.R.layout.select_dialog_item, // Layout
            dataCountries as MutableList<String> // Array
        )
        txtCountryExpedition.threshold = 1
        txtCountryExpedition.setAdapter(adapter)

        hideDialogProgress()


        if (documentTypeSend != "null" && documentNumberSend != "null") {


            spinnerTypeIdentification.selectedIndex =
                spinnerTypeIdentification.getItems<String>().indexOf(documentTypeSend)
            txtDocumentNumber.setText(documentNumberSend)

            if (typeOperation == TypeCreateUser.USER_SEND_UPDATE) {

                btnFingerPrint.setImageResource(R.drawable.ic_fingerprint_correct)
                btnFingerPrint.isEnabled = false
                txtDocumentNumber.isEnabled = false
                spinnerTypeIdentification.isEnabled = false

                val temThird = getLocalThird()
                loadThird(temThird)

            } else if (typeOperation == TypeCreateUser.USER_PAY_ENROLL || typeOperation == TypeCreateUser.USER_SEND_ENROLL) {

                btnFingerPrint.setImageResource(R.drawable.ic_fingerprint_standart)
                btnFingerPrint.isEnabled = true
                showDialogProgress(getString(R.string.text_load_data))
                giroViewModel.getThird(documentNumberSend, documentTypeSend, false)
            }

        }


    }

    private fun listenerSpinnerTypeIdentification(item: String) {


        if (item == getString(R.string.giro_user_type_document_nit) ||
            item == getString(R.string.giro_user_type_document_ti) ||
            item == getString(R.string.DOCUMENT_TYPE_DEFAULT)
        ) {

            txtCountryExpedition.isEnabled = false
            txtCountryExpedition.setText("COLOMBIA")
            countryExpeditionCode = "56"
            txtCountryExpedition.setBackgroundResource(R.drawable.txt_background_inactive)
            txtCountryExpedition.setText("")

        } else {

            txtCountryExpedition.setText("")
            countryExpeditionCode = ""
            txtCountryExpedition.setBackgroundResource(R.drawable.txt_background_active)
            txtCountryExpedition.isEnabled = true

        }

        if (item == getString(R.string.giro_user_type_document_nit)) {
            spinnerTypePerson.selectedIndex =
                typePerson.indexOf(getString(R.string.giro_user_type_person_legal))
        } else {
            spinnerTypePerson.selectedIndex =
                typePerson.indexOf(getString(R.string.giro_user_type_person_natural))
        }

        if (item == getString(R.string.giro_user_type_document_pas)) {
            txtDateExpirePassport.isEnabled = true
            txtDateEntryCountry.isEnabled = true

            txtDateExpirePassport.setBackgroundResource(R.drawable.txt_background_active)
            txtDateEntryCountry.setBackgroundResource(R.drawable.txt_background_active)


        } else {
            txtDateExpirePassport.setText("")
            txtDateEntryCountry.setText("")
            txtDateExpirePassport.isEnabled = false
            txtDateEntryCountry.isEnabled = false

            txtDateExpirePassport.setBackgroundResource(R.drawable.txt_background_inactive)
            txtDateEntryCountry.setBackgroundResource(R.drawable.txt_background_inactive)

        }

    }

    private fun loadCities(cities: List<CitiesDTO>?) {

        if(DeviceUtil.isSalePoint()){
            baseMenuActivity().progressDialog.hide()
        }else{
            baseActivity().progressDialog.hide()
        }

        hideDialogProgress()
        this.cities = cities as MutableList<CitiesDTO>

        val dataCities: List<String>? = cities?.map {
            "${it.cityName}"
        }

        val adapter = ArrayAdapter<String>(
            this.activity!!, // Context
            android.R.layout.select_dialog_item, // Layout
            dataCities as MutableList<String> // Array
        )


        if (city) {
            txtCity.threshold = 1
            txtCity.setAdapter(adapter)
            txtCity.showDropDown()

        }
        if (cityExpedition) {
            txtCityExpedition.threshold = 1
            txtCityExpedition.setAdapter(adapter)
            txtCityExpedition.showDropDown()

        }
        city = false
        cityExpedition = false


    }

    private fun listenersBtnNext() {

        if (validateCreateThird()) {

            if (!validateEnrollet() && validateTypeOperationNoEnrollet()) {
                showOkAlertDialog("", "Se debe enrolar el usuario")
                return
            }

            val third = createThirdDTO()

            if (validateTypeOperationUpdate()) {

                updateThird(third)


            } else {

                createThird(third)

            }

        }

    }

    private fun createThird(third: ThirdDTO) {

        third.apply {
            this.enrolled = false
        }

        if (typeOperation == TypeCreateUser.USER_SEND_CREATE_DESTINATION) {
            third.apply {
                this.biometrics = "N"
            }
        }

        showDialogProgress("creando usuario..")
        giroViewModel.createThird(
            third,
            fingerPrintThirdL,
            fingerPrintThirdR,
            if (activity is GiroMenuActivity) (activity as GiroMenuActivity) else null,
            if (activity is GiroActivity) (activity as GiroActivity) else null,
            typeOperation
        )
    }

    private fun updateThird(third: ThirdDTO) {

        showDialogProgress("modificacion usuario..")

        third.apply {
            this.enrolled = true
        }

        if (validateTypeOperationEnroll()) {

            third.apply {
                this.fPrintThirdL = fingerPrintThirdL
                this.fPrintThirdR = fingerPrintThirdR
                this.enrolled = false
                this.exoneratedFootprint = false
            }

        }

        giroViewModel.updateThird(
            third,
            if (activity is GiroMenuActivity) (activity as GiroMenuActivity) else null,
            if (activity is GiroActivity) (activity as GiroActivity) else null,
            typeOperation
        )
    }

    private fun validateTypeOperationEnroll() =
        typeOperation == TypeCreateUser.USER_PAY_ENROLL || typeOperation == TypeCreateUser.USER_SEND_ENROLL

    private fun validateTypeOperationUpdate() =
        typeOperation == TypeCreateUser.USER_SEND_UPDATE || typeOperation == TypeCreateUser.USER_UPDATE_DEFAULT || typeOperation == TypeCreateUser.USER_PAY_ENROLL || typeOperation == TypeCreateUser.USER_SEND_ENROLL

    private fun validateTypeOperationNoEnrollet() =
        typeOperation != TypeCreateUser.USER_UPDATE_DEFAULT && typeOperation != TypeCreateUser.USER_SEND_UPDATE && typeOperation != TypeCreateUser.USER_SEND_CREATE_DESTINATION

    private fun createThirdDTO(): ThirdDTO {

        val codePerson =
            typePerson.filter { it.contains(spinnerTypePerson.text.toString()) }.last().split("-")[1]

        val codeEconomicActivity =
            economyActivity.filter { it.contains(spinnerEconomyActivity.text.toString()) }.last().split(
                "-"
            )[1]

        return ThirdDTO().apply {
            this.idtype = spinnerTypeIdentification.text.toString()
            this.id = txtDocumentNumber.text.toString()
            this.codeTypePerson = codePerson
            this.firstName = txtNames.text.toString()
            this.lastName = txtFirstSurname.text.toString()
            this.secondLastName = txtSecondSurname.text.toString()
            this.address = txtAddress.text.toString()
            this.email = txtEmail.text.toString()
            this.dateExpeditionDocument = txtDateExpedition.text.toString().replace("/", "")
            this.birthDate = txtDateBirth.text.toString().replace("/", "")
            this.telephone = txtPhone.text.toString()
            this.mobile = txtNumber.text.toString()
            this.exoneratedFootprint = false
            this.stratum =
                if (spinnerSocialStratum.text.toString() != getString(R.string.giro_users_placeholder_social_stratum)) spinnerSocialStratum.text.toString() else "1"
            this.codeActivity = codeEconomicActivity
            this.daneCityResidence = cityResidenceCode
            this.countryExpeditionCode = countryExpeditionCode
            this.daneCityExpedition = cityExpeditionCode
            this.dateEntryCountry = txtDateEntryCountry.text.toString().replace("/", "")
            this.dateExpirePassport = txtDateExpirePassport.text.toString().replace("/", "")
        }
    }


    private fun loadThird(respose: ResponseQueryThirdDTO?) {

        hideDialogProgress()

        val third = respose?.third
        val cityResicense = respose?.cityResidence
        val cityExpedition = respose?.cityExpedition

        spinnerTypeIdentification.selectedIndex =
            spinnerTypeIdentification.getItems<String>().indexOf(third?.typeId ?: "")
        txtDocumentNumber.setText(third?.id.toString())

        val strTypePerson =
            typePerson.filter { it.contains(third?.codeTypePerson.toString()) }.last().split("(")[0].trim()
        spinnerTypePerson.selectedIndex =
            spinnerTypePerson.getItems<String>().indexOf(strTypePerson)

        txtNames.setText(third?.firstName ?: "")
        txtFirstSurname.setText(third?.lastName ?: "")
        txtSecondSurname.setText(validateNullString(third?.secondLastName ?: ""))
        txtAddress.setText(validateNullString(third?.address ?: ""))
        txtEmail.setText(third?.email ?: "")

        val formatDateExpedition = DateUtil.convertStringToDateFormat(
            DateUtil.StringFormat.DDMMYY,
            third?.dateExpeditionDocument ?: ""
        )
        val strDateExpedition = DateUtil.convertDateToStringFormat(
            DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
            formatDateExpedition
        )
        txtDateExpedition.text = strDateExpedition
        val cal = Calendar.getInstance()
        cal.time = formatDateExpedition
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        datesExpedition = EditDates(day, month, year)

        val formatDateBirth = DateUtil.convertStringToDateFormat(
            DateUtil.StringFormat.DDMMYY,
            third?.birthDate.toString()
        )
        val strDateBirth = DateUtil.convertDateToStringFormat(
            DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
            formatDateBirth
        )
        txtDateBirth.text = strDateBirth
        val calB = Calendar.getInstance()
        calB.time = formatDateBirth
        val yearB = calB.get(Calendar.YEAR)
        val monthB = calB.get(Calendar.MONTH)
        val dayB = calB.get(Calendar.DAY_OF_MONTH)

        datesBirth = EditDates(dayB, monthB, yearB)


        val strCountry = countries.filter { it.code == third?.countryExpeditionCode }.last().name
        txtCountryExpedition.setText(strCountry)
        countryExpeditionCode = third?.countryExpeditionCode.toString()

        txtCity.setText(validateNullString(cityResicense?.cityName))

        cityResidenceCode = cityResicense?.codeDane ?: ""

        txtCityExpedition.setText(cityExpedition?.cityName)
        cityExpeditionCode = cityExpedition?.codeDane ?: ""

        txtPhone.setText(third?.telephone ?: "")
        txtNumber.setText(third?.mobile ?: "")

        if (third?.stratum != null && third.stratum!!.isNotEmpty()) {

            spinnerSocialStratum.selectedIndex =
                spinnerSocialStratum.getItems<String>().indexOf(third.stratum)
        }


        if (third?.dateEntryCountry != "null") {

            val formatDateEntryCountry = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY,
                third?.dateEntryCountry ?: ""
            )
            val strDateEntryCountry = DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                formatDateEntryCountry
            )
            txtDateEntryCountry.text = strDateEntryCountry
            val calEC = Calendar.getInstance()
            calEC.time = formatDateEntryCountry
            val yearEC = calEC.get(Calendar.YEAR)
            val monthEC = calEC.get(Calendar.MONTH)
            val dayEC = calEC.get(Calendar.DAY_OF_MONTH)
            datesEntryCountry = EditDates(dayEC, monthEC, yearEC)
        }
        if (third?.dateExpirePassport != "null") {
            val formatDateExpirePassport = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY,
                third?.dateExpirePassport ?: ""
            )
            val strDateExpirePassport = DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                formatDateExpirePassport
            )
            txtDateExpirePassport.text = strDateExpirePassport
            val calEP = Calendar.getInstance()
            calEP.time = formatDateExpirePassport
            val yearEP = calEP.get(Calendar.YEAR)
            val monthEP = calEP.get(Calendar.MONTH)
            val dayEP = calEP.get(Calendar.DAY_OF_MONTH)
            datesExpirePassport = EditDates(dayEP, monthEP, yearEP)
        }

        if (typeOperation == TypeCreateUser.USER_PAY_ENROLL || typeOperation == TypeCreateUser.USER_SEND_ENROLL) {

            btnFingerPrint.setImageResource(R.drawable.ic_fingerprint_standart)
            btnFingerPrint.isEnabled = true

        }

        if (typeOperation == TypeCreateUser.USER_UPDATE_DEFAULT) {
            btnFingerPrint.setImageResource(R.drawable.ic_fingerprint_correct)
            btnFingerPrint.isEnabled = false
        }

        hideKeyboard()

    }

    private fun validateEmail(email: String): Boolean {

        return email.contains("@")

    }

    private fun validateCreateThird(): Boolean {

        var isValid = true
        var personalInfoIsValid = true
        var otherInfoIsValid = true

        if (txtDocumentNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroDocumentNumber.visibility = View.VISIBLE
        } else {
            labelErrorGiroDocumentNumber.visibility = View.GONE
        }

        if (spinnerTypePerson.text.toString() == getString(R.string.giro_users_placeholder_type_person)) {
            isValid = false
            labelErrorGiroTypePerson.visibility = View.VISIBLE
        } else {
            labelErrorGiroTypePerson.visibility = View.GONE
        }

        if (txtNames.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroNames.visibility = View.VISIBLE
        } else {
            labelErrorGiroNames.visibility = View.GONE
        }

        if (txtFirstSurname.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroFirstSurname.visibility = View.VISIBLE
        } else {
            labelErrorGiroFirstSurname.visibility = View.GONE
        }

        if (txtNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroNumber.visibility = View.VISIBLE
        } else {
            labelErrorGiroNumber.visibility = View.GONE
        }

        if (typeOperation != TypeCreateUser.USER_SEND_CREATE_DESTINATION) {

            personalInfoIsValid = validateCreateThirdPersonalInfo()
            otherInfoIsValid = validateCreateThirdOtherInfo()
        }

        return isValid && personalInfoIsValid && otherInfoIsValid
    }

    private fun validateCreateThirdOtherInfo(): Boolean {

        var isValid = true

        if (txtDateBirth.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroDateBirth.visibility = View.VISIBLE
        } else {
            labelErrorGiroDateBirth.visibility = View.GONE
        }

        if (txtDateExpedition.text.toString().isEmpty()) {
            isValid = false
            labelErrorDateExpedition.visibility = View.VISIBLE
        } else {
            labelErrorDateExpedition.visibility = View.GONE
        }

        if (txtCountryExpedition.text.toString().isEmpty()) {
            isValid = false
            labelErrorCountryExpedition.visibility = View.VISIBLE
        } else if (countryExpeditionCode.isEmpty()) {
            isValid = false
            labelErrorCountryExpeditionInvalid.visibility = View.VISIBLE
        } else {
            labelErrorCountryExpedition.visibility = View.GONE
            labelErrorCountryExpeditionInvalid.visibility = View.GONE
        }

        if (txtCityExpedition.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroCityExpedition.visibility = View.VISIBLE
        } else if (cityExpeditionCode.isEmpty()) {
            isValid = false
            labelErrorGiroCityExpeditionInvalid.visibility = View.VISIBLE
        } else {
            labelErrorGiroCityExpedition.visibility = View.GONE
            labelErrorGiroCityExpeditionInvalid.visibility = View.GONE
        }

        return isValid

    }

    private fun validateCreateThirdPersonalInfo(): Boolean {

        var isValid = true

        if (!validateEmail(txtEmail.text.toString()) && txtEmail.text.toString().length > 0) {
            isValid = false
            labelErrorGiroEmailInvalid.visibility = View.VISIBLE
        } else {
            labelErrorGiroEmailInvalid.visibility = View.GONE
        }

        if (spinnerSocialStratum.text.toString() == (getString(R.string.giro_users_placeholder_social_stratum))) {
            isValid = false
            labelErrorGiroSocialStratum.visibility = View.VISIBLE
        } else {
            labelErrorGiroSocialStratum.visibility = View.GONE
        }

        if (txtAddress.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroAddress.visibility = View.VISIBLE
        } else {
            labelErrorGiroAddress.visibility = View.GONE
        }

        if (txtCity.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroCity.visibility = View.VISIBLE
        } else if (cityResidenceCode.isEmpty()) {
            isValid = false
            labelErrorGiroCityInvalid.visibility = View.VISIBLE
        } else {
            labelErrorGiroCity.visibility = View.GONE
            labelErrorGiroCityInvalid.visibility = View.GONE
        }

        return isValid
    }

    private fun validateEnrollet() =
        fingerPrintThirdR.isNotEmpty() && fingerPrintThirdL.isNotEmpty()

    @SuppressLint("SetTextI18n")
    private fun showDialogDatePicker(txtView: TextView) {

        val calendar = Calendar.getInstance()
        val formate = SimpleDateFormat(
            DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH.rawValue,
            Locale.getDefault()
        )

        var currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        var currentMonth = calendar.get(Calendar.MONTH)
        var currentYear = calendar.get(Calendar.YEAR)

        if (typeOperation == TypeCreateUser.USER_SEND_UPDATE || typeOperation == TypeCreateUser.USER_UPDATE_DEFAULT) {
            if (txtView == txtDateBirth) {
                currentDay = datesBirth.day
                currentMonth = datesBirth.month
                currentYear = datesBirth.year
            } else if (txtView == txtDateExpedition) {
                currentDay = datesExpedition.day
                currentMonth = datesExpedition.month
                currentYear = datesExpedition.year
            } else if (txtView == txtDateEntryCountry && txtDateEntryCountry.text.isNotEmpty()) {
                currentDay = datesEntryCountry.day
                currentMonth = datesEntryCountry.month
                currentYear = datesEntryCountry.year
            } else if (txtView == txtDateExpirePassport && txtDateExpirePassport.text.isNotEmpty()) {
                currentDay = datesExpirePassport.day
                currentMonth = datesExpirePassport.month
                currentYear = datesExpirePassport.year
            }
        }

        val datePicker = DatePickerDialog(
            context,
            R.style.DateDialogTheme,
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val date = formate.format(calendar.time)
                txtView.text = date
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePicker.datePicker.maxDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun createThirdSuccess() {

        hideDialogProgress()
        showOkAlertDialog(
            getString(R.string.giro_create_third_success_title),
            getString(R.string.giro_create_third_success_message)
        ) {
            if (DeviceUtil.isSalePoint())
                onBackPressed(GiroUsersFragment())
            else
                onBackPressed()
        }

    }

    private fun updateThirdSuccess() {

        hideDialogProgress()
        showOkAlertDialog(
            getString(R.string.giro_update_third_success_title),
            getString(R.string.giro_update_third_success_message)
        ) {
            if (DeviceUtil.isSalePoint())
                onBackPressed(GiroUsersFragment())
            else
                onBackPressed()
        }

    }

    private fun showErrors(error: String) {

        hideDialogProgress()
        showOkAlertDialog("", error)

    }

    fun getLocalThird(): ResponseQueryThirdDTO {
        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_giro_edit))
        return Gson().fromJson(json, ResponseQueryThirdDTO::class.java);
    }

    private fun validateNullString(str: String?): String {
        val response = str ?: ""

        return if (response == "null" || response == ".") "" else response
    }


}
