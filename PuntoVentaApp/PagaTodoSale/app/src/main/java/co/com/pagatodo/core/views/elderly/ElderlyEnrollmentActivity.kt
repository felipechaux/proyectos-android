package co.com.pagatodo.core.views.elderly

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.CitiesDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import co.com.pagatodo.core.data.dto.response.CountryDTO
import co.com.pagatodo.core.data.dto.response.ResponseThirdDTO
import co.com.pagatodo.core.data.model.ElderlyPointedFingerModel
import co.com.pagatodo.core.data.model.ElderlyThirdModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.ElderlyTypeOperation
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.base.BaseActivity
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_elderly_enrollment.*
import kotlinx.android.synthetic.main.activity_elderly_enrollment.btnAttachDocumentExFootPrint
import kotlinx.android.synthetic.main.activity_elderly_enrollment.btnBack
import kotlinx.android.synthetic.main.activity_elderly_enrollment.btnClean
import kotlinx.android.synthetic.main.activity_elderly_enrollment.btnFingerEnrollment
import kotlinx.android.synthetic.main.activity_elderly_enrollment.btnNext
import kotlinx.android.synthetic.main.activity_elderly_enrollment.containerEnrollment
import kotlinx.android.synthetic.main.activity_elderly_enrollment.containerUserInfo
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorCountryExpedition
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorCountryExpeditionInvalid
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorDateExpedition
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorDocumentNumber
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroAddress
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroCity
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroCityExpedition
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroCityExpeditionInvalid
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroCityInvalid
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroDateBirth
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroDocumentNumber
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroEmailInvalid
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroFirstSurname
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroNames
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroNumber
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroSocialStratum
import kotlinx.android.synthetic.main.activity_elderly_enrollment.labelErrorGiroTypePerson
import kotlinx.android.synthetic.main.activity_elderly_enrollment.nextButtom
import kotlinx.android.synthetic.main.activity_elderly_enrollment.pdfButtom
import kotlinx.android.synthetic.main.activity_elderly_enrollment.spinnerDocumentType
import kotlinx.android.synthetic.main.activity_elderly_enrollment.spinnerEconomyActivity
import kotlinx.android.synthetic.main.activity_elderly_enrollment.spinnerSocialStratum
import kotlinx.android.synthetic.main.activity_elderly_enrollment.spinnerTypeIdentification
import kotlinx.android.synthetic.main.activity_elderly_enrollment.spinnerTypePerson
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtAddress
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtCity
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtCityExpedition
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtCountryExpedition
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtDateBirth
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtDateEntryCountry
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtDateExpedition
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtDateExpirePassport
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtDocumentNumber
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtDocumentNumberForm
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtEmail
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtFirstSurname
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtNames
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtNumber
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtPhone
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtSecondSurname
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtUserName
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtIdentificationBack
import kotlinx.android.synthetic.main.activity_elderly_enrollment.txtIdentificationFront
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ElderlyEnrollmentActivity : BaseActivity() {

    private lateinit var viewModel: ElderlyViewModel
    private lateinit var socialStratum: MutableList<String>
    private lateinit var economyActivity: MutableList<String>
    private lateinit var documentType: MutableList<String>
    private lateinit var typePerson: MutableList<String>
    private lateinit var cities: MutableList<CitiesDTO>
    private lateinit var countries: MutableList<CountryDTO>
    private var cityResidenceCode: String = ""
    private var city: Boolean = false
    private var countryExpeditionCode: String = ""
    private var cityExpedition: Boolean = false
    private var cityExpeditionCode: String = ""
    private val READ_REQUEST_CODE: Int = 42 //Variable para cargar documento adjunto
    private var readPdf: ByteArray? = null
    private var elderlyTypeOperation = ElderlyTypeOperation.USER_CREATE
    val CAMERA_REQUEST_CODE = 0
    private var dataPictureFront: Bitmap? = null
    private var dataPictureBack: Bitmap? = null
    private var documentSide: Boolean? = false
    private val STORAGE_CODE: Int = 100 //Variable para acceder a la memoria interna


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elderly_enrollment)
        setupView()

    }

    private fun setupView() {

        setupBaseView()
        updateTitle(getString(R.string.home_menu_title_elderly))
        initListenersViews()
        setupViewModel()

    }

    private fun setupViewModel() {

        viewModel = ViewModelProviders.of(this).get(ElderlyViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {

                is ElderlyViewModel.ViewEvent.QueryIdSuccess -> loadQueryId(it.responseDTO)
                is ElderlyViewModel.ViewEvent.QueryIdNotFound -> queryIdNotFound()
                is ElderlyViewModel.ViewEvent.Errors -> showErros(it.errorMessage)
                is ElderlyViewModel.ViewEvent.ParameterSuccess -> loadParameter(
                    it.listDocumentType,
                    it.listEconomicActivity,
                    it.listTypePerson,
                    it.listSocialStratum,
                    it.listCountryDTO)
                is ElderlyViewModel.ViewEvent.CitiesSuccess -> loadCities(it.cities)
                is ElderlyViewModel.ViewEvent.CreateThirdSuccess -> createThirdSuccess()
                is ElderlyViewModel.ViewEvent.SaveFingerPrint -> saveFingerPrintSuccess()
                is ElderlyViewModel.ViewEvent.CheckSaveIdSuccess -> elderlySaveIdSuccess()
            }
        })

        showDialogProgress(getString(R.string.message_dialog_load_data))
        viewModel.getParametersElderly()

    }

    private fun loadQueryId(responseDTO: ResponseThirdDTO) {

        enableDataPay()
        enablePictures()
        val thirdName = "${responseDTO.firstName} ${responseDTO.lastName}"
        txtUserName.setText(thirdName)

    }

    private fun queryIdNotFound() {

        createEditUser()
        txtDocumentNumberForm.setText(txtDocumentNumber.text.toString())
        txtDocumentNumberForm.isEnabled = false
        spinnerTypeIdentification.selectedIndex = spinnerDocumentType.selectedIndex
        spinnerTypeIdentification.isEnabled = false

    }

    private fun showErros(errorMessage: String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage)

    }

    private fun loadParameter(

        listDocumentType: List<String>?,
        listEconomyActivity: List<String>?,
        listTypePerson: List<String>?,
        listSocialStratum: List<String>?,
        countries: List<CountryDTO>?
    ) {

        socialStratum = listSocialStratum as MutableList<String>
        economyActivity = listEconomyActivity as MutableList<String>
        typePerson = listTypePerson as MutableList<String>
        documentType = listDocumentType as MutableList<String>

        typePerson.add(0, getString(R.string.giro_users_placeholder_type_person))
        socialStratum.add(0, getString(R.string.giro_users_placeholder_social_stratum))

        val dataDocumentType = documentType.map {
            it.split("-")[0]
        }

        val socialStratum = socialStratum.map {
            it.split("-")[0]
        }

        val economyActivity = economyActivity.map {
            it.split("-")[0].trim()
        }

        val typePerson = typePerson.map {
            it.split("(")[0].trim()
        }


        spinnerTypeIdentification.setItems(dataDocumentType)

        spinnerDocumentType.setItems(dataDocumentType)

        spinnerTypeIdentification.selectedIndex = dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        spinnerDocumentType.selectedIndex = dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        txtCountryExpedition.isEnabled = false

        txtCountryExpedition.setText("COLOMBIA")

        countryExpeditionCode = "56"

        txtCountryExpedition.setBackgroundResource(R.drawable.txt_background_inactive)

        spinnerTypeIdentification.setOnItemSelectedListener { view, position, id, item ->

            if (item == getString(R.string.giro_user_type_document_nit) ||
                item == getString(R.string.giro_user_type_document_ti) ||
                item == getString(R.string.DOCUMENT_TYPE_DEFAULT)
            ) {

                txtCountryExpedition.isEnabled = false
                txtCountryExpedition.setText("COLOMBIA")
                countryExpeditionCode = "56"
                txtCountryExpedition.setBackgroundResource(R.drawable.txt_background_inactive)


            } else {

                txtCountryExpedition.setText("")
                countryExpeditionCode = ""
                txtCountryExpedition.setBackgroundResource(R.drawable.txt_background_active)
                txtCountryExpedition.isEnabled = true

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
            this, // Context
            android.R.layout.select_dialog_item, // Layout
            dataCountries as MutableList<String> // Array
        )

        txtCountryExpedition.threshold = 1
        txtCountryExpedition.setAdapter(adapter)

    }

    private fun loadCities(cities: List<CitiesDTO>?) {

        hideDialogProgress()
        this.cities = cities as MutableList<CitiesDTO>

        val dataCities: List<String>? = cities?.map {
            "${it.cityName}"
        }

        val adapter = ArrayAdapter<String>(
            this, // Context
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

    private fun createThirdSuccess() {

        hideDialogProgress()
        showOkAlertDialog(
            getString(R.string.giro_create_third_success_title),
            getString(R.string.giro_create_third_success_message)
        ) {
            elderlyTypeOperation = ElderlyTypeOperation.USER_ENROLLER
            enableFingerprint()

        }
    }

    private fun saveFingerPrintSuccess() {

        containerUserInfo.visibility = View.VISIBLE
        hideDialogProgress()
        showOkAlertDialog(

            getString(R.string.elderly_save_fingerprint_success_title),
            getString(R.string.elderly_save_fingerprint_success_message)

        ) {

            elderlyTypeOperation = ElderlyTypeOperation.SEND_ID
            disableFingerprint()
            enablePictures()

        }

    }

    private fun elderlySaveIdSuccess() {

        hideDialogProgress()
        showOkAlertDialog(
            getString(R.string.elderly_save_id_success_title),
            getString(R.string.elderly_save_id_success_message)
        ) {
            finish()
        }
    }

    private fun initListenersViews() {

        initListenersTextWatcher()
        initListenersOnClick()

    }

    private fun initListenersOnClick() {

        btnConsultId.setOnClickListener { submitCollection() }

        btnClean.setOnClickListener { cleanForm() }

        btnNext.setOnClickListener { enrollUser() } // Evento botón siguiente.

        btnBack.setOnClickListener { finish() }

        btnAttachDocumentExFootPrint.setOnClickListener {

            //            performFileSearch() Clase para enviar documento adjunto
        }

        txtIdentificationBack.setOnClickListener {

            documentSide = false
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            } else {
                showOkAlertDialog("", getString(R.string.elderly_error_camera))
            }
        }

        txtIdentificationFront.setOnClickListener {

            documentSide = true
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            } else {
                showOkAlertDialog("", getString(R.string.elderly_error_camera))
            }
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

        validateInputsTextDocumentNumber()

        initListenersOnClick2()
        initListenersFingerPrintEnrollment()

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
    }

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

        val datePicker = DatePickerDialog(
            this,
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

    private fun submitCollection() {

        if (validateSearch()) {
            getElderly()
        }
    }

    private fun getElderly() {

        val model = ElderlyThirdModel().apply {
            this.document = txtDocumentNumber.text.toString()
            this.documentType = spinnerDocumentType.text.toString()
        }
        showDialogProgress(getString(R.string.text_load_data))
        viewModel.elderlyQueryId(model)
    }

    private fun cleanForm() {

        txtDocumentNumber.setText("")
        txtUserName.setText("")
        disableCreateThird()
        disableDataPay()
        disablePictures()
        containerUserInfo.visibility = View.VISIBLE
        nextButtom.visibility = View.GONE
        dataPictureFront = null
        dataPictureBack = null

    }

    private fun enrollUser() {

        if (elderlyTypeOperation == ElderlyTypeOperation.USER_CREATE) {

            if (!validateFields()) {

                showOkAlertDialog("", getString(R.string.elderly_error_data))
                return

            } else {
                val third = createThirdDTO()
                createThird(third)

            }

        } else if (elderlyTypeOperation == ElderlyTypeOperation.USER_ENROLLER) {

            sendFingerprint()

        } else {
//            sendDocument() Para enviar documento adjunto
            sendBytePicture()

        }
    }

    private fun createThirdDTO(): ThirdDTO {

        val codePerson =
            typePerson.filter { it.contains(spinnerTypePerson.text.toString()) }.last().split("-")[1]

        val codeEconomicActivity =
            economyActivity.filter { it.contains(spinnerEconomyActivity.text.toString()) }.last().split(
                "-"
            )[1]

        return ThirdDTO().apply {
            this.idtype = spinnerTypeIdentification.text.toString()
            this.id = txtDocumentNumberForm.text.toString()
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
            this.countryExpeditionCode = countryExpeditionCode
            this.daneCityExpedition = cityExpeditionCode
            this.dateEntryCountry = txtDateEntryCountry.text.toString().replace("/", "")
            this.dateExpirePassport = txtDateExpirePassport.text.toString().replace("/", "")
        }
    }

    @SuppressLint("InflateParams")
    private fun createThird(third: ThirdDTO) {

        third.apply {
            this.enrolled = false
        }

        showDialogProgress(getString(R.string.elderly_create_user))
        viewModel.createThird(
            third
        )
    }

    private fun sendFingerprint() {

        initListenersFingerPrintEnrollment()

    }

    private fun initListenersFingerPrintEnrollment() {

        btnFingerEnrollment.setOnClickListener {
            initListenersFingerPrintSalePointEnrollment()
        }
    }

    private fun initListenersFingerPrintSalePointEnrollment() {

        val call = object : com.datacenter.fingerprintlibrary.IResultListener {
            override fun onResultData(huella: Array<out Array<String>>?) {

                if ((huella?.get(0)?.get(1) ?: "").isNotEmpty() && (huella?.get(1)?.get(1) ?: "").isNotEmpty()) {

                    val fingerPrint = ElderlyPointedFingerModel().apply {
                        this.indexFingerR = huella?.get(0)?.get(1) ?: ""
                        this.middleFingerR = huella?.get(0)?.get(1) ?: ""
                        this.ringFingerR = huella?.get(0)?.get(1) ?: ""
                        this.littleFingerR = huella?.get(0)?.get(1) ?: ""
                        this.indexFingerL = huella?.get(1)?.get(1) ?: ""
                        this.middleFingerL = huella?.get(1)?.get(1) ?: ""
                        this.ringFingerL = huella?.get(1)?.get(1) ?: ""
                        this.littleFingerL = huella?.get(1)?.get(1) ?: ""
                        btnFingerEnrollment.setImageResource(R.drawable.ic_fingerprint_correct)
                        btnFingerEnrollment.isEnabled = false
                    }

                    val thirdSecConsult = createThirdDTO()

                    val sendId = ElderlyThirdModel().apply {

                        this.documentType = thirdSecConsult.idtype
                        this.document = thirdSecConsult.id

                    }

                    viewModel.elderlySaveFingerPrint(sendId, fingerPrint)


                } else {
                    showErrors(getString(R.string.error_biometry))
                }
            }
        }

        bioFacade.setListener(call)

        bioFacade.procesar(

            arrayOf(

                com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_INDICE,
                com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_CORAZON,
                com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_ANULAR,
                com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_MENIQUE,
                com.datacenter.fingerprintlibrary.HuellaEnum.IZQUIERDA_INDICE,
                com.datacenter.fingerprintlibrary.HuellaEnum.IZQUIERDA_CORAZON,
                com.datacenter.fingerprintlibrary.HuellaEnum.IZQUIERDA_ANULAR,
                com.datacenter.fingerprintlibrary.HuellaEnum.IZQUIERDA_MENIQUE

            ),
            false
        )
    }

    private fun showErrors(error: String) {

        hideDialogProgress()
        showOkAlertDialog("", error)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val takePicture = data.extras?.get("data") as Bitmap
                    showConfirmPicture(takePicture)
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmPicture(bitmap: Bitmap) {

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_elderly_view_picture, null)
        dialogView.findViewById<ImageView>(R.id.photoimageView).setImageBitmap(bitmap)
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Captura de foto")
            setView(dialogView)
            setCancelable(false)
            setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.text_btn_accept)) { dialog, which ->

                dialog.dismiss()
                isForeground = false
                savePicture(bitmap)

            }
        }.show()

        dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.secondaryText
            )
        )
    }

    private fun savePicture(bitmap: Bitmap) {

        if (documentSide == true) {
            dataPictureFront = bitmap

        } else {
            dataPictureBack = bitmap
        }

        if (dataPictureBack != null && dataPictureFront != null) {
            showOkAlertDialog("", "Fotos adjuntas")
            createPdf()
        } else {
            showOkAlertDialog("", "Tome la foto del otro lado del documento")
        }
    }

    private fun createPdf(){

        val mDoc = Document()
        //pdf file name
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        //pdf file path
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName +".pdf"

        try {
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))

            //open the document for writing
            mDoc.open()

            val streamFront = ByteArrayOutputStream()
            dataPictureFront?.compress(Bitmap.CompressFormat.PNG, 100, streamFront)

            val streamBack = ByteArrayOutputStream()
            dataPictureBack?.compress(Bitmap.CompressFormat.PNG, 100, streamBack)

            val imageFrontId = Image.getInstance(streamFront.toByteArray())
            val imageBackId = Image.getInstance(streamBack.toByteArray())
            val space = ""

            imageFrontId.scaleAbsolute(450F,350F)
            imageBackId.scaleAbsolute(450F,350F)

            imageFrontId.alignment = 5
            imageBackId.alignment = 5

            mDoc.add(imageFrontId)
            mDoc.add(Paragraph(space))
            mDoc.add(imageBackId)

            //close document
            mDoc.close()

            //show file saved message with file name and path
            Toast.makeText(this, "$mFileName.pdf\nis saved to\n$mFilePath", Toast.LENGTH_SHORT).show()

            readPdf = File(mFilePath).inputStream().readBytes()

        }

        catch (e: Exception){
            //if anything goes wrong causing exception, get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendBytePicture() {

        val thirdConsult = createThirdDTO()

        val sendCedula = ElderlyThirdModel().apply {

            this.documentUser = readPdf
            this.documentType = thirdConsult.idtype
            this.document = thirdConsult.id

        }

        viewModel.elderlySaveId(sendCedula)
    }

    private fun createEditUser() {

        containerUserInfo.visibility = View.GONE
        containerEnrollment.visibility = View.VISIBLE
        nextButtom.visibility = View.VISIBLE

    }

    private fun disableCreateThird() {

        containerEnrollment.visibility = View.GONE

    }

    private fun enableFingerprint() {

        btnFingerEnrollment.visibility = View.VISIBLE

    }

    private fun disableFingerprint() {

        btnFingerEnrollment.visibility = View.GONE

    }

    private fun enableDataPay() {

        dataPay.visibility = View.VISIBLE
        divisionLine.visibility = View.VISIBLE

    }

    private fun disableDataPay() {

        dataPay.visibility = View.INVISIBLE
        divisionLine.visibility = View.INVISIBLE

    }

    private fun enablePictures() {

        capturePictureFront.visibility = View.VISIBLE
        capturePictureBack.visibility = View.VISIBLE

    }

    private fun disablePictures() {

        capturePictureBack.visibility = View.INVISIBLE
        capturePictureFront.visibility = View.INVISIBLE

    }

    private fun enablePdf() {

        pdfButtom.visibility = View.VISIBLE

    }

    private fun validateSearch(): Boolean {

        var isValid = true

        if (txtDocumentNumber.text.isEmpty()) {
            labelErrorDocumentNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDocumentNumber.visibility = View.GONE
        }

        return isValid;

    }

    private fun validateFields(): Boolean {

        var isValid = true

        if (txtDocumentNumberForm.text.isEmpty()) {
            labelErrorGiroDocumentNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorGiroDocumentNumber.visibility = View.GONE
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

        if (txtDateBirth.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroDateBirth.visibility = View.VISIBLE
        } else {
            labelErrorGiroDateBirth.visibility = View.GONE
        }

        if (!validateEmail(txtEmail.text.toString()) && txtEmail.text.toString().length > 0) {
            isValid = false
            labelErrorGiroEmailInvalid.visibility = View.VISIBLE
        } else {
            labelErrorGiroEmailInvalid.visibility = View.GONE
        }

        if (!validateFieldsLocale()) {
            isValid = false
        }

        return isValid;
    }

    private fun validateFieldsLocale(): Boolean {

        var isValid = true
        if (txtDateExpedition.text.toString().isEmpty()) {
            isValid = false
            labelErrorDateExpedition.visibility = View.VISIBLE
        } else {
            labelErrorDateExpedition.visibility = View.GONE
        }

        if (spinnerSocialStratum.text.toString() == (getString(R.string.giro_users_placeholder_social_stratum))) {
            isValid = false
            labelErrorGiroSocialStratum.visibility = View.VISIBLE
        } else {
            labelErrorGiroSocialStratum.visibility = View.GONE
        }

        if (txtCountryExpedition.text.toString().isEmpty()) {
            isValid = false
            labelErrorCountryExpedition.visibility = View.VISIBLE
        } else if (countryExpeditionCode.isEmpty()) {
            isValid = false
//            labelErrorCountryExpeditionInvalid.visibility = View.VISIBLE
        } else {
            labelErrorCountryExpedition.visibility = View.GONE
//            labelErrorCountryExpeditionInvalid.visibility = View.GONE
        }

        if (txtCityExpedition.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroCityExpedition.visibility = View.VISIBLE
        } else if (cityExpeditionCode.isEmpty()) {
            isValid = false
//            labelErrorGiroCityExpeditionInvalid.visibility = View.VISIBLE
        } else {
            labelErrorGiroCityExpedition.visibility = View.GONE
//            labelErrorGiroCityExpeditionInvalid.visibility = View.GONE
        }

        if (txtCity.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroCity.visibility = View.VISIBLE
        } else if (cityResidenceCode.isEmpty()) {
            isValid = false
//            labelErrorGiroCityInvalid.visibility = View.VISIBLE
        } else {
            labelErrorGiroCity.visibility = View.GONE
//            labelErrorGiroCityInvalid.visibility = View.GONE
        }

        if (txtAddress.text.toString().isEmpty()) {
            isValid = false
            labelErrorGiroAddress.visibility = View.VISIBLE
        } else {
            labelErrorGiroAddress.visibility = View.GONE
        }

        return isValid;

    }

    private fun validateInputsTextDocumentNumber() {

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
            }
        })

    }

    private fun initListenersTextWatcher() {

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

        spinnerSocialStratum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (spinnerSocialStratum.text.toString().isEmpty())
                    labelErrorGiroSocialStratum.visibility = View.VISIBLE
                else
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

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtAddress.text.toString().isEmpty())
                    labelErrorGiroAddress.visibility = View.VISIBLE
                else
                    labelErrorGiroAddress.visibility = View.GONE
            }
        })

        initListenersThirdInfoTextWatcher()
    }

    private fun initListenersThirdInfoTextWatcher() {

        txtDocumentNumberForm.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDocumentNumberForm.text.toString().isEmpty())
                    labelErrorGiroDocumentNumber.visibility = View.VISIBLE
                else
                    labelErrorGiroDocumentNumber.visibility = View.GONE
            }
        })

        txtNames.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtNames.text.toString().isEmpty())
                    labelErrorGiroNames.visibility = View.VISIBLE
                else
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

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtFirstSurname.text.toString().isEmpty())
                    labelErrorGiroFirstSurname.visibility = View.VISIBLE
                else
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

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtNumber.text.toString().isEmpty())
                    labelErrorGiroNumber.visibility = View.VISIBLE
                else
                    labelErrorGiroNumber.visibility = View.GONE
            }
        })

        initListenersThirdDateTextWatcher()

    }

    private fun initListenersThirdDateTextWatcher() {

        txtDateBirth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDateBirth.text.toString().isEmpty())
                    labelErrorGiroDateBirth.visibility = View.VISIBLE
                else
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

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtDateExpedition.text.toString().isEmpty())
                    labelErrorDateExpedition.visibility = View.VISIBLE
                else
                    labelErrorDateExpedition.visibility = View.GONE
            }
        })

        initListenersThirdLocaleTextWatcher()

    }

    private fun initListenersThirdLocaleTextWatcher() {

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
                    viewModel.getCities(txtCity.text.toString().toUpperCase())
                    city = true
                }
            }
        })

        txtCountryExpedition.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtCountryExpedition.text.toString().isEmpty())
                    labelErrorCountryExpedition.visibility = View.VISIBLE
                else
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
                    viewModel.getCities(txtCityExpedition.text.toString().toUpperCase())
                    cityExpedition = true
                }
            }
        })

    }

    private fun validateEmail(email: String): Boolean {

        return email.contains("@")

    }
}

//Método para cargar documento adjunto
//    fun performFileSearch() {
//
//        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
//        // browser.
//        try {
//
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                // Filter to only show results that can be "opened", such as a
//                // file (as opposed to a list of contacts or timezones)
//                addCategory(Intent.CATEGORY_OPENABLE)
//
//                // Filter to show only images, using the image MIME data type.
//                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
//                // To search for all documents available via installed storage providers,
//                // it would be "*/*".
//                type = "application/pdf"
//
//            }
//            startActivityForResult(intent, READ_REQUEST_CODE)
//
//        } catch (ex: Exception) {
//            showOkAlertDialog("", getString(R.string.giro_authorization_attach_error))
//        }
//
//
//    }

//    @Throws(IOException::class)
//    private fun readBytes(context: Context, uri: Uri): ByteArray? =
//        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
//
//
//        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//
//            resultData?.data?.also { uri ->
//
//                loadDocument(uri)
//
//            }
//        }
//
//    }

//    private fun loadDocument(uri: Uri) {
//
//        val bytes = readBytes(this, uri)
//
//        readPdf = bytes
//
//    }
//
//    private fun sendDocument() {
//
//        if (readPdf != null) {
//
//            showDialogProgress(getString(R.string.text_load_data))
//
//            val thirdConsult = createThirdDTO()
//
//            val sendCedula = ElderlyThirdModel().apply {
//
//                this.documentUser = readPdf
//                this.documentType = thirdConsult.idtype
//                this.document = thirdConsult.id
//
//            }
//
//            viewModel.elderlySaveId(sendCedula)
//
//        } else {
//            showOkAlertDialog("", getString(R.string.elderly_error_document))
//        }
//
//    }
//FIN


//    private fun saveInternalStorage() {
//        btnConsultId.setOnClickListener {
//            //we need to handle runtime permission for devices with marshmallow and above
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                //system OS >= Marshmallow(6.0), check permission is enabled or not
//                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_DENIED
//                ) {
//                    //permission was not granted, request it
//                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    requestPermissions(permissions, STORAGE_CODE)
//                } else {
//                    //permission already granted, call savePdf() method
//                    createPdf()
//                }
//            } else {
//                //system OS < marshmallow, call savePdf() method
//                createPdf()
//            }
//        }
//    }
