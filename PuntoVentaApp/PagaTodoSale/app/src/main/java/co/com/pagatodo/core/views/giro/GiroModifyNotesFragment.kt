package co.com.pagatodo.core.views.giro


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseQueryGiroDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_giro_modify_notes.*

class GiroModifyNotesFragment : BaseFragment() {

    private lateinit var giroViewModel: GiroViewModel
    private var responseQueryGiroDTO: ResponseQueryGiroDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giro_modify_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()
    }

    private fun setupFragmentView() {

        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, androidx.lifecycle.Observer {

            when (it) {
                is GiroViewModel.ViewEvent.QueryGiroSuccess -> loadGiro(it.queryGiro)
                is GiroViewModel.ViewEvent.Errors -> errors(it.errorMessage)
                is GiroViewModel.ViewEvent.Success -> showSuccessMessage(it.message)

            }

        })
    }

    private fun showSuccessMessage(message: String) {

        hideDialogProgress()
        showOkAlertDialog(getString(R.string.giro_modify_notes_success_title), message) {
            if (DeviceUtil.isSalePoint())
                onBackPressed(GiroModifyNotesFragment())
            else
                onBackPressed()
        }

    }

    private fun errors(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun loadGiro(queryGiro: ResponseQueryGiroDTO) {


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

        lblAgency.setText(queryGiro.agencyOrigen?.name ?: R_string(R.string.giro_not_found))

        lblValue.setText(
            getString(
                R.string.text_label_parameter_coin,
                formatValue(queryGiro.giroValue!!.toDouble())
            )
        )


        val date =
            DateUtil.convertStringToDateFormat(DateUtil.StringFormat.DDMMYY, queryGiro.dateGiro!!)
        lblDate.setText(
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                date
            )
        )
        lblHour.setText(queryGiro.hourGiro ?: getText(R.string.giro_not_found))

        if (!DeviceUtil.isSalePoint()) {

            lyContainerResult.visibility = View.VISIBLE
            lyContainerResultTem.visibility = View.GONE

        }

        lyContainerNotes.removeAllViews()

        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_giro_modify_notes_previous, null, false)
        val lblNotes = view.findViewById<TextView>(R.id.lblNote)

        lblNotes.setText(queryGiro.notes ?: getText(R.string.giro_not_found))
        lyContainerNotes.addView(view)

        hideDialogProgress()
    }

    private fun setupViewModel() {
        initListenersViews()
        initListenersTextWatcherViews()

    }

    private fun initListenersTextWatcherViews() {

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

                labelErrorReferenceNumber.visibility = View.GONE
            }
        })


        txtNotes.addTextChangedListener(object : TextWatcher {

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

                labelErrorNotes.visibility = View.GONE
            }
        })


    }

    private fun initListenersViews() {

        btnConsult.setOnClickListener {
            if (validateQuery()) {
                hideKeyboard()
                showDialogProgress(getString(R.string.message_dialog_load_data))
                giroViewModel.queryGiro(txtReferenceNumber.text.toString())

            }
        }

        btnConsultSP.setOnClickListener {
            if (validateQuery()) {

                showDialogProgress(getString(R.string.message_dialog_load_data))
                giroViewModel.queryGiro(txtReferenceNumber.text.toString())

            }
        }

        btnClean.setOnClickListener {
            clearForm()
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnSave.setOnClickListener {

            if (validateSendNotes()) {

                showDialogProgress(getString(R.string.message_dialog_load_data))
                giroViewModel.modifyNotes(
                    txtReferenceNumber.text.toString(),
                    txtNotes.text.toString()
                )

            }

        }


    }

    private fun validateQuery(): Boolean {

        var isValid = true

        if (txtReferenceNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorReferenceNumber.visibility = View.VISIBLE
        } else {
            labelErrorReferenceNumber.visibility = View.GONE
        }

        return isValid

    }

    private fun validateSendNotes(): Boolean {

        var isValid = true

        if (txtNotes.text.toString().isEmpty()) {
            isValid = false
            labelErrorNotes.visibility = View.VISIBLE
        } else {
            labelErrorNotes.visibility = View.GONE
        }

        if (responseQueryGiroDTO == null) {
            isValid = false
        }

        return isValid

    }

    private fun clearForm() {


        lyContainerNotes.removeAllViews()

        lblDocumentNumberOrigin.setText(R_string(R.string.giro_not_found))
        lblNameUserOrigen.setText(R_string(R.string.giro_not_found))
        lblNameUserDestination.setText(R_string(R.string.giro_not_found))
        lblDocumentNumberDestination.setText(R_string(R.string.giro_not_found))
        lblDepartment.setText(R_string(R.string.giro_not_found))
        lblCity.setText(R_string(R.string.giro_not_found))
        lblAgency.setText(R_string(R.string.giro_not_found))
        lblDate.setText(R_string(R.string.giro_not_found))
        lblHour.setText(R_string(R.string.giro_not_found))
        lblValue.setText(R_string(R.string.giro_not_found))

        responseQueryGiroDTO = null

    }


}
