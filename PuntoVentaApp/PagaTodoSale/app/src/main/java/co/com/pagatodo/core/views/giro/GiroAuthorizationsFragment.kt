package co.com.pagatodo.core.views.giro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.response.GiroTypeRequestsMessagesDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryGiroDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryThirdDTO
import co.com.pagatodo.core.data.dto.response.ResponseThirdDTO
import co.com.pagatodo.core.data.model.GiroExFingerPrintModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.GiroTypeRequest
import co.com.pagatodo.core.util.print.BytesUtil
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_giro_authorizations.*
import java.io.File
import java.io.IOException
import java.lang.Exception

class GiroAuthorizationsFragment : BaseFragment() {

    private val READ_REQUEST_CODE: Int = 42
    private lateinit var giroViewModel: GiroViewModel
    private var typeRequest: GiroTypeRequestsDTO? = null
    private lateinit var documentType: MutableList<String>
    private lateinit var requestsMessages: MutableList<GiroTypeRequestsMessagesDTO>
    private var listDocumentExtFingerPrintModel: MutableList<DocumentDTO> = mutableListOf()
    private var mThird: ResponseThirdDTO? = null
    private var mQueryGiro: ResponseQueryGiroDTO? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giro_authorizations, container, false)
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
                is GiroViewModel.ViewEvent.ParameterSuccess -> loadParameters(
                    it.typeRequest,
                    it.listDocumentType!!
                )
                is GiroViewModel.ViewEvent.Errors -> errors(it.errorMessage)
                is GiroViewModel.ViewEvent.ThirdSuccess -> loadThird(it.third)
                is GiroViewModel.ViewEvent.ThirdNotFound -> errors(getString(R.string.giro_authorization_third_error))
                is GiroViewModel.ViewEvent.Success -> showSuccessMessage(it.message)
                is GiroViewModel.ViewEvent.QueryGiroSuccess -> loadGiro(it.queryGiro)

            }


        })

        giroViewModel.getParametersGiros()
        showDialogProgress(getString(R.string.message_dialog_load_data))


    }

    private fun showSuccessMessage(message: String) {

        hideDialogProgress()
        showOkAlertDialog("", message) {
            if (DeviceUtil.isSalePoint())
                onBackPressed(GiroAuthorizationsFragment())
            else
                onBackPressed()
        }

    }

    private fun loadThird(respose: ResponseQueryThirdDTO?) {

        mThird = respose?.third
        val third = respose?.third
        when (typeRequest?.code) {

            GiroTypeRequest.EXONERATE_FINGERPRINT.raw -> {

                if (third?.isExoneratedFootprint ?: true) {

                    showOkAlertDialog("", getString(R.string.third_exonerate_true)) {
                        if (DeviceUtil.isSalePoint())
                            onBackPressed(GiroAuthorizationsFragment())
                        else
                            onBackPressed()
                    }

                } else {

                    val nameThird =
                        "${third?.firstName ?: ""} ${third?.lastName ?: ""} ${third?.secondLastName
                            ?: ""}"
                    txtUserNamesExFootPrint.setText(nameThird)
                }


            }
            GiroTypeRequest.CHANGE_BENEFICIARY.raw -> {
                val nameThird =
                    "${third?.firstName ?: ""} ${third?.lastName ?: ""} ${third?.secondLastName
                        ?: ""}"
                txtUserNamesBenChange.setText(nameThird)
            }
        }

        hideDialogProgress()

    }

    private fun errors(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun loadParameters(
        typeRequests: List<GiroTypeRequestsDTO>?,
        listDocumentType: List<String>
    ) {


        val listItems = typeRequests?.map { it.description!! }!! as MutableList<String>

        listItems.add(0, getString(R.string.giro_authorization_placeholder_type_request))
        spinnerTypeRequest.setItems(listItems)
        spinnerTypeRequest.setOnItemSelectedListener { view, position, id, item ->

            if (typeRequests.filter { it.description == item }.count() > 0) {

                typeRequest = typeRequests.filter { it.description == item }.last()

                loadRequestsMessages()

                validateContainerAuthorizationType()

            } else {

                typeRequest = null
                showContainer(lyContainerInit as View)

            }


        }

        documentType = listDocumentType as MutableList<String>

        val dataDocumentType = documentType.map {
            it.split("-")[0]
        }

        spinnerDocumentTypeExFootPrint.setItems(dataDocumentType)
        spinnerDocumentTypeExFootPrint.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        spinnerDocumentTypeBenChange.setItems(dataDocumentType)
        spinnerDocumentTypeBenChange.selectedIndex =
            dataDocumentType.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))

        hideDialogProgress()
    }

    private fun loadRequestsMessages() {

        requestsMessages = typeRequest?.messages as MutableList<GiroTypeRequestsMessagesDTO>
        val listItems = requestsMessages.map { it.description!! } as MutableList<String>


        when (typeRequest?.code) {
            GiroTypeRequest.EXONERATE_FINGERPRINT.raw -> {
                listItems.add(0, getString(R.string.giro_authorization_placeholder_type_request))
                spinnerReasonRequestExFootPrint.setItems(listItems)
            }
            GiroTypeRequest.CANCEL.raw,
            GiroTypeRequest.AUTHORIZE_GIRO.raw,
            GiroTypeRequest.LIFTING_EXPIRED.raw,
            GiroTypeRequest.RISING_RESTRICTION.raw -> {
                listItems.add(0, getString(R.string.giro_authorization_category_placeholder))
                spinnerCategoryCGLRLV.setItems(listItems)
            }
            GiroTypeRequest.CHANGE_BENEFICIARY.raw -> {
                listItems.add(0, getString(R.string.giro_authorization_category_placeholder))
                spinnerCategoryBenChange.setItems(listItems)
            }

        }

    }

    private fun setupFragmentView() {
        initListenersViews()
        initListenersExFingerPrintViews()
        initListenersCGLRLVViews()
        initListenersBenChangeViews()
    }

    private fun loadGiro(queryGiro: ResponseQueryGiroDTO) {

        this.mQueryGiro = queryGiro

        when (typeRequest?.code) {
            GiroTypeRequest.CANCEL.raw,
            GiroTypeRequest.LIFTING_EXPIRED.raw,
            GiroTypeRequest.AUTHORIZE_GIRO.raw,
            GiroTypeRequest.RISING_RESTRICTION.raw -> {
                txtStatusGiroCGLRLV.setText(getString(R.string.giro_default_state))
            }
            GiroTypeRequest.CHANGE_BENEFICIARY.raw -> {
                txtStatusGiroBenChange.setText(getString(R.string.giro_default_state))
            }

        }

        hideDialogProgress()

    }

    private fun initListenersViews() {

        initListenersOnClick()

    }

    private fun initListenersOnClick() {

        btnBackInit.setOnClickListener {
            onBackPressed()
        }

    }

    private fun validateContainerAuthorizationType() {

        when (typeRequest?.code) {
            GiroTypeRequest.EXONERATE_FINGERPRINT.raw -> showContainer(lyContainerExFootPrint)
            GiroTypeRequest.CANCEL.raw,
            GiroTypeRequest.LIFTING_EXPIRED.raw,
            GiroTypeRequest.AUTHORIZE_GIRO.raw,
            GiroTypeRequest.RISING_RESTRICTION.raw -> showContainer(lyContainerCGLRLV)
            GiroTypeRequest.CHANGE_BENEFICIARY.raw -> showContainer(lyContainerBenChange)

        }

    }

    private fun clearContainer() {

        lyContainerInit.visibility = View.GONE
        lyContainerExFootPrint.visibility = View.GONE
        lyContainerBenChange.visibility = View.GONE
        lyContainerCGLRLV.visibility = View.GONE

        mThird = null
        mQueryGiro = null
        listDocumentExtFingerPrintModel = mutableListOf()
        clearBenChange()
        clearCGLRLV()
    }

    private fun showContainer(container: View) {
        clearContainer()
        container.visibility = View.VISIBLE

    }

    //region  EXONERAR HUELL

    private fun initListenersExFingerPrintViews() {

        btnSearchUserExFootPrintSP.setOnClickListener {

            if (validateQueryThirtyExFingerPrint()) {

                showDialogProgress(getString(R.string.text_load_data))

                giroViewModel.getThird(
                    txtDocumentNumberExFootPrint.text.toString(),
                    spinnerDocumentTypeExFootPrint.text.toString(),
                    false
                )
            }

        }

        btnSearchUserExFootPrint.setOnClickListener {

            if (validateQueryThirtyExFingerPrint()) {

                showDialogProgress(getString(R.string.text_load_data))

                giroViewModel.getThird(
                    txtDocumentNumberExFootPrint.text.toString(),
                    spinnerDocumentTypeExFootPrint.text.toString(),
                    false
                )
            }

        }

        btnAttachDocumentExFootPrint.setOnClickListener {

            if (validateQueryThirtyExFingerPrint() && validateAttachDocumen()) {
                performFileSearch()
            }

        }

        btnSaveExFootPrint.setOnClickListener {

            if (validateAllExFingerPrint()) {

                exFingerPrintDespatch()
            }

        }

        btnBackExFootPrint.setOnClickListener {

            onBackPressed()

        }

        initListenersExFingerPrintTextWatcher()

    }

    private fun initListenersExFingerPrintTextWatcher() {

        spinnerReasonRequestExFootPrint.setOnItemSelectedListener { view, position, id, item ->

            if (spinnerReasonRequestExFootPrint.text.toString() != getString(R.string.giro_authorization_placeholder_type_request)) {

                labelErrorReasonRequestExFootPrint.visibility = View.GONE
            } else {
                labelErrorReasonRequestExFootPrint.visibility = View.VISIBLE
            }

        }


        txtDocumentNumberExFootPrint.addTextChangedListener(object : TextWatcher {

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

                labelErrorDocumentNumberExFootPrint.visibility = View.GONE
            }
        })


        txtObservationsExFootPrint.addTextChangedListener(object : TextWatcher {

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

                labelErrorObservationsExFootPrint.visibility = View.GONE
            }
        })


    }

    private fun validateAllExFingerPrint(): Boolean {


        var isValid = true

        if (txtObservationsExFootPrint.text.toString().isEmpty()) {
            isValid = false
            labelErrorObservationsExFootPrint.visibility = View.VISIBLE
        } else {
            labelErrorObservationsExFootPrint.visibility = View.GONE
        }

        if (!validateQueryThirtyExFingerPrint())
            isValid = false

        if (!validateAttachDocumen())
            isValid = false

        return isValid

    }

    private fun exFingerPrintDespatch() {

        if (validateDocument()) {

            val thirdDTO = ThirdDTO().apply {
                this.idtype = mThird?.typeId
                this.id = mThird?.id
                this.firstName = mThird?.firstName
                this.lastName = mThird?.lastName
                this.secondLastName = mThird?.secondLastName
                this.address = mThird?.address
                this.email = mThird?.email
                this.dateExpeditionDocument = mThird?.dateExpeditionDocument
                this.birthDate = mThird?.birthDate
                this.telephone = mThird?.telephone
                this.mobile = mThird?.mobile

            }

            val messages =
                typeRequest?.messages?.filter { it.description == spinnerReasonRequestExFootPrint.text.toString() }
                    ?.last()

            val giroTypeRequestsMessagesDTO = GiroTypeRequestsMessagesDTO().apply {
                this.description = messages?.description
                this.code = messages?.code
            }

            val exFingerPrintModel = GiroExFingerPrintModel().apply {
                this.third = thirdDTO
                this.notes = txtObservationsExFootPrint.text.toString()
                this.listDocument = listDocumentExtFingerPrintModel
                this.requestsMessages = giroTypeRequestsMessagesDTO
            }

            showDialogProgress(getString(R.string.text_load_data))
            giroViewModel.exonerateFingerPrint(exFingerPrintModel)
        }
    }

    private fun validateQueryThirtyExFingerPrint(): Boolean {
        var isValid = true

        if (txtDocumentNumberExFootPrint.text.toString().isEmpty()) {
            isValid = false
            labelErrorDocumentNumberExFootPrint.visibility = View.VISIBLE
        } else {
            labelErrorDocumentNumberExFootPrint.visibility = View.GONE
        }

        return isValid
    }

    private fun validateAttachDocumen(): Boolean {
        var isValid = true

        if (spinnerReasonRequestExFootPrint.text.toString() == getString(R.string.giro_authorization_placeholder_type_request)) {
            isValid = false
            labelErrorReasonRequestExFootPrint.visibility = View.VISIBLE
        } else {
            labelErrorDocumentNumberExFootPrint.visibility = View.GONE
        }

        return isValid
    }

    private fun validateDocument(): Boolean {
        var isValid = true

        if (listDocumentExtFingerPrintModel.size == 0) {
            isValid = false
            showOkAlertDialog("", getString(R.string.giro_authorization_msm_document))
        }

        return isValid
    }

    private fun loadDocument(uri: Uri) {

        val file = File(uri.path)

        val bytes = readBytes(activity!!, uri)

        val hexa = BytesUtil.getHexStringFromBytes(bytes)

        bytes.toString()

        val data = DocumentDTO().apply {
            this.document = hexa
            this.name = file.name
        }

        listDocumentExtFingerPrintModel.add(data)

        showDocumentsExtFingerPrint()

    }

    private fun showDocumentsExtFingerPrint() {

        if (listDocumentExtFingerPrintModel.size > 0) {
            lyContainerDocumentsExFootPrint.removeAllViews()

            listDocumentExtFingerPrintModel.forEach { document ->

                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_giro_authorization_attach_document, null, false)
                val lblNameDoc = view.findViewById<TextView>(R.id.lblNameDocument)
                val lblImg = view.findViewById<TextView>(R.id.lblbtnRemoveDocument)
                val imgDelete = view.findViewById<ImageView>(R.id.btnRemoveDocument)

                lblNameDoc.setText(document.name)
                imgDelete.visibility = View.VISIBLE
                lblImg.visibility = View.GONE

                imgDelete.setOnClickListener {
                    removeDocumentsExtFingerPrint(document.name!!)
                }

                lyContainerDocumentsExFootPrint.addView(view)
            }


        } else {

            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_giro_authorization_attach_document, null, false)
            lyContainerDocumentsExFootPrint.removeAllViews()
            lyContainerDocumentsExFootPrint.addView(view)
        }

    }

    private fun removeDocumentsExtFingerPrint(nameDoc: String) {

        listDocumentExtFingerPrintModel =
            listDocumentExtFingerPrintModel.filter { it.name != nameDoc } as MutableList<DocumentDTO>

        showDocumentsExtFingerPrint()
    }

    /* DOCUEMNTOS*/

    fun performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        try {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                addCategory(Intent.CATEGORY_OPENABLE)

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                type = "application/pdf"
            }
            startActivityForResult(intent, READ_REQUEST_CODE)

        } catch (ex: Exception) {
            showOkAlertDialog("", getString(R.string.giro_authorization_attach_error))
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {


        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            resultData?.data?.also { uri ->

                loadDocument(uri)

            }
        }
    }

    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }

    //endregion

    //region  Autorizar giro ,Anular giro , levantar restriccion , levantar vencimiento

    private fun initListenersCGLRLVViews() {

        btnSearchReferenceCGLRLVSP.setOnClickListener {

            if (validateQueryGiroCGLRLV()) {

                showDialogProgress(getString(R.string.text_load_data))
                giroViewModel.queryGiro(txtReferenceNumberCGLRLV.text.toString())

            }

        }

        btnSearchReferenceCGLRLV.setOnClickListener {
            if (validateQueryGiroCGLRLV()) {

                showDialogProgress(getString(R.string.text_load_data))
                giroViewModel.queryGiro(txtReferenceNumberCGLRLV.text.toString())

            }
        }

        btnCleanCGLRLV.setOnClickListener {

            clearCGLRLV()

        }

        btnBackCGLRLV.setOnClickListener {
            onBackPressed()
        }

        btnSaveCGLRLV.setOnClickListener {

            if (validateAllCGLRLV()) {
                despatchCGLRLV()
            }

        }

        initListenersCGLRLVTextWatcher()

    }

    private fun initListenersCGLRLVTextWatcher() {

        spinnerCategoryCGLRLV.setOnItemSelectedListener { view, position, id, item ->

            if (spinnerCategoryCGLRLV.text.toString() != getString(R.string.giro_authorization_category_placeholder)) {

                labelErrorCategoryCGLRLV.visibility = View.GONE
            } else {
                labelErrorCategoryCGLRLV.visibility = View.VISIBLE
            }

        }

        txtReferenceNumberCGLRLV.addTextChangedListener(object : TextWatcher {

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

                labelErrorReferenceCGLRLV.visibility = View.GONE
            }
        })

        txtNotesAdviserCGLRLV.addTextChangedListener(object : TextWatcher {

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

                labelErrorNotesAdviserCGLRLV.visibility = View.GONE
            }
        })

    }

    private fun validateAllCGLRLV(): Boolean {

        var isValid = true

        if (txtNotesAdviserCGLRLV.text.isEmpty()) {
            isValid = false
            labelErrorNotesAdviserCGLRLV.visibility = View.VISIBLE
        } else {
            labelErrorNotesAdviserCGLRLV.visibility = View.GONE
        }

        if (!validateQueryGiroCGLRLV())
            isValid = false


        if (mQueryGiro == null)
            isValid = false

        return isValid
    }

    private fun clearCGLRLV() {
        mQueryGiro = null
        spinnerCategoryCGLRLV.selectedIndex = 0
        txtReferenceNumberCGLRLV.setText("")
        txtStatusGiroCGLRLV.setText(getString(R.string.giro_not_found))
        txtNotesAdviserCGLRLV.setText("")
    }

    private fun despatchCGLRLV() {


        val messages =
            typeRequest?.messages?.filter { it.description == spinnerCategoryCGLRLV.text.toString() }
                ?.last()

        val giroTypeRequestsMessagesDTO = GiroTypeRequestsMessagesDTO().apply {
            this.description = messages?.description
            this.code = messages?.code

        }
        val listMessages = arrayListOf(giroTypeRequestsMessagesDTO)

        val typeRequestsTem2 = GiroTypeRequestsDTO().apply {
            this.code = typeRequest?.code
            this.description = typeRequest?.description
            this.messages = listMessages
        }

        val authorizationModel = AuthorizationModel().apply {
            this.notes = txtNotesAdviserCGLRLV.text.toString()
            this.reference = txtReferenceNumberCGLRLV.text.toString()
            this.typeRequests = typeRequestsTem2
        }

        showDialogProgress(getString(R.string.text_load_data))
        giroViewModel.giroAuthorization(authorizationModel)

    }

    private fun validateQueryGiroCGLRLV(): Boolean {

        var isValid = true

        if (spinnerCategoryCGLRLV.text.toString() == getString(R.string.giro_authorization_category_placeholder)) {
            isValid = false
            labelErrorCategoryCGLRLV.visibility = View.VISIBLE
        } else {
            labelErrorCategoryCGLRLV.visibility = View.GONE
        }

        if (txtReferenceNumberCGLRLV.text.toString().isEmpty()) {
            isValid = false
            labelErrorReferenceCGLRLV.visibility = View.VISIBLE
        } else {
            labelErrorReferenceCGLRLV.visibility = View.GONE
        }

        return isValid

    }
    //endregion

    //region CAMBIAR BENEFICIARIO

    private fun initListenersBenChangeViews() {

        btnSearchReferenceBenChangeSP.setOnClickListener {

            if (validateQueryGiroBenChange()) {
                showDialogProgress(getString(R.string.text_load_data))
                giroViewModel.queryGiro(txtReferenceNumberBenChange.text.toString())
            }

        }

        btnSearchReferenceBenChange.setOnClickListener {

            if (validateQueryGiroBenChange()) {
                showDialogProgress(getString(R.string.text_load_data))
                giroViewModel.queryGiro(txtReferenceNumberBenChange.text.toString())
            }

        }

        btnCleanBenChange.setOnClickListener {
            clearBenChange()
        }

        btnSearchUserBenChange.setOnClickListener {

            if (validateQueryThirtyBenChange()) {

                showDialogProgress(getString(R.string.text_load_data))

                giroViewModel.getThird(
                    txtDocumentNumberBenChange.text.toString(),
                    spinnerDocumentTypeBenChange.text.toString(),
                    false
                )
            }

        }

        btnBackBenChange.setOnClickListener {
            onBackPressed()
        }

        btnSaveBenChange.setOnClickListener {

            if (validateAllBenChange()) {
                despatchBenChange()
            }
        }

        initListenersBenChangeTextWatcher()
    }

    private fun initListenersBenChangeTextWatcher() {

        spinnerCategoryBenChange.setOnItemSelectedListener { view, position, id, item ->

            if (spinnerCategoryBenChange.text.toString() != getString(R.string.giro_authorization_category_placeholder)) {

                labelErrorCategoryBenChange.visibility = View.GONE
            } else {
                labelErrorCategoryBenChange.visibility = View.VISIBLE
            }

        }

        txtReferenceNumberBenChange.addTextChangedListener(object : TextWatcher {

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

                labelErrorReferenceBenChange.visibility = View.GONE
            }
        })

        txtNotesAdviserBenChange.addTextChangedListener(object : TextWatcher {

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

                labelErrorNotesAdviserBenChange.visibility = View.GONE
            }
        })

        txtDocumentNumberBenChange.addTextChangedListener(object : TextWatcher {

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
                labelErrorDocumentNumberBenChange.visibility = View.GONE
            }
        })
    }


    private fun despatchBenChange() {

        val thirdDTO = ThirdDTO().apply {
            this.idtype = mThird?.typeId
            this.id = mThird?.id
            this.firstName = mThird?.firstName
            this.lastName = mThird?.lastName
            this.secondLastName = mThird?.secondLastName
            this.address = mThird?.address
            this.email = mThird?.email
            this.dateExpeditionDocument = mThird?.dateExpeditionDocument
            this.birthDate = mThird?.birthDate
            this.telephone = mThird?.telephone
            this.mobile = mThird?.mobile

        }

        val messages =
            typeRequest?.messages?.filter { it.description == spinnerCategoryBenChange.text.toString() }
                ?.last()

        val giroTypeRequestsMessagesDTO = GiroTypeRequestsMessagesDTO().apply {
            this.description = messages?.description
            this.code = messages?.code

        }
        val listMessages = arrayListOf(giroTypeRequestsMessagesDTO)

        val typeRequestsTem2 = GiroTypeRequestsDTO().apply {
            this.code = typeRequest?.code
            this.description = typeRequest?.description
            this.messages = listMessages
        }

        val authorizationModel = AuthorizationModel().apply {
            this.notes = txtNotesAdviserBenChange.text.toString()
            this.reference = txtReferenceNumberBenChange.text.toString()
            this.typeRequests = typeRequestsTem2
            this.third = thirdDTO
        }

        showDialogProgress(getString(R.string.text_load_data))
        giroViewModel.giroAuthorization(authorizationModel)

    }

    private fun validateQueryGiroBenChange(): Boolean {

        var isValid = true

        if (spinnerCategoryBenChange.text.toString() == getString(R.string.giro_authorization_category_placeholder)) {
            isValid = false
            labelErrorCategoryBenChange.visibility = View.VISIBLE
        } else {
            labelErrorCategoryBenChange.visibility = View.GONE
        }

        if (txtReferenceNumberBenChange.text.toString().isEmpty()) {
            isValid = false
            labelErrorReferenceBenChange.visibility = View.VISIBLE
        } else {
            labelErrorReferenceBenChange.visibility = View.GONE
        }

        return isValid
    }

    private fun validateQueryThirtyBenChange(): Boolean {
        var isValid = true

        if (txtDocumentNumberBenChange.text.toString().isEmpty()) {
            isValid = false
            labelErrorDocumentNumberBenChange.visibility = View.VISIBLE
        } else {
            labelErrorDocumentNumberBenChange.visibility = View.GONE
        }

        return isValid
    }

    private fun clearBenChange() {
        mQueryGiro = null
        spinnerCategoryBenChange.selectedIndex = 0
        txtReferenceNumberBenChange.setText("")
        txtStatusGiroBenChange.setText(getString(R.string.giro_not_found))
        txtNotesAdviserBenChange.setText("")
        txtDocumentNumberBenChange.setText("")
        txtUserNamesBenChange.setText("")
    }

    private fun validateAllBenChange(): Boolean {

        var isValid = true

        if (txtNotesAdviserBenChange.text.isEmpty()) {
            isValid = false
            labelErrorNotesAdviserBenChange.visibility = View.VISIBLE
        } else {
            labelErrorNotesAdviserBenChange.visibility = View.GONE
        }

        if (!validateQueryGiroBenChange())
            isValid = false

        if (!validateQueryThirtyBenChange())
            isValid = false

        if (mThird == null)
            isValid = false

        if (mQueryGiro == null)
            isValid = false

        return isValid
    }

    //endregion

}


