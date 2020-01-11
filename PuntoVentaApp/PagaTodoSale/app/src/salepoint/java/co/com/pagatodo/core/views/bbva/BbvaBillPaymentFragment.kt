package co.com.pagatodo.core.views.bbva

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.AgreementsBbvaDTO
import co.com.pagatodo.core.data.dto.BillRequestDTO
import co.com.pagatodo.core.data.dto.CategoriesDTO
import co.com.pagatodo.core.data.dto.CitiesDTO
import co.com.pagatodo.core.data.dto.response.ResponseBbvaBillPaymentDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.base.BaseFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.salepoint.fragment_bbva_bill_payment.*
import java.util.concurrent.TimeUnit


class BbvaBillPaymentFragment : BaseFragment() {

    private lateinit var viewModel: BbvaViewModel
    private lateinit var categories: MutableList<CategoriesDTO>
    private lateinit var cities: MutableList<CitiesDTO>
    private var lagreementsMap: MutableMap<String, MutableList<AgreementsBbvaDTO>> = HashMap()
    private var selectCity: Int = 0
    private var descriptions = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            co.com.pagatodo.core.R.layout.fragment_bbva_bill_payment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupFragmentView()
    }

    private fun setupViewModel() {

        viewModel = ViewModelProviders.of(this).get(BbvaViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is BbvaViewModel.ViewEvent.Errors -> loadErrors(it.errorMessage)
                is BbvaViewModel.ViewEvent.Success -> success(it.message)
                is BbvaViewModel.ViewEvent.QueryCitiesSuccess -> loadCities(it.cities)
                is BbvaViewModel.ViewEvent.QueryaParametersSuccess -> saveParameters(
                    it.categories,
                    it.agreements
                )
                is BbvaViewModel.ViewEvent.ValidateBill -> showResponseData(it.billRequestDTO.bill)
                is BbvaViewModel.ViewEvent.PaymentBill -> paymentSuccess(it.message)

            }
        })
        showDialogProgress(getString(R.string.message_dialog_request))
        viewModel.getParameters()

    }

    private fun saveParameters(categories: List<CategoriesDTO>?, agreements: List<String>?) {
        hideDialogProgress()
        loadParameter(categories, agreements)
    }

    private fun loadParameter(
        categories: List<CategoriesDTO>?,
        agreements: List<String>?
    ) {
        listBillType(categories)
        listBillAgreements(agreements)
    }

    private fun listBillType(categories: List<CategoriesDTO>?) {

        this.categories = categories as MutableList<CategoriesDTO>

        val items = mutableListOf<String>()
        items.add(0, getString(R.string.bbva_obligations_bill_type_option))
        categories.forEachIndexed { index, item ->
            items.add(item.nameCategorie.toString())
        }
        spinnerBillType.setItems(items)
    }

    private fun listBillAgreements(agreements: List<String>?) {

        for (ItemAgreement in agreements!!) {
            var campos = ItemAgreement.split(",");
            var agreementDTO = AgreementsBbvaDTO()
            agreementDTO.agreementId = campos[0]
            agreementDTO.description = campos[1]
            agreementDTO.daneCode = campos[2]
            agreementDTO.validateValue = campos[3]

            var lagreements: MutableList<AgreementsBbvaDTO>? =
                lagreementsMap[agreementDTO.daneCode!!]
            if (lagreements == null)
                lagreements = ArrayList()
            lagreements.add(agreementDTO)
            lagreementsMap.put(agreementDTO.daneCode!!, lagreements)
        }

    }

    private fun loadCities(cities: List<CitiesDTO>?) {

        this.cities = cities as MutableList<CitiesDTO>

        hideDialogProgress()
        val adapter = ArrayAdapter<String>(
            this.activity!!,
            android.R.layout.simple_dropdown_item_1line,
            cities?.map { "${it.cityName} - ${it.departamentName}" })
        txtCities.setAdapter(adapter)
        txtCities.showDropDown()
        txtCities.setOnItemClickListener { _, _, pos, _ ->
            hideKeyboard()
            selectCity = pos
        }

    }

    private fun success(message: String) {
        hideDialogProgress()
        showOkAlertDialog("", message) {
            onBackPressed(this)
        }
    }

    private fun loadErrors(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun setupFragmentView() {

        initListenersViews()

    }

    private fun initListenersViews() {

        initListenersOnClick()
        initListenersRWatcherCities()
    }

    private fun initListenersRWatcherCities() {

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

                labelErrorBillPaymentCity.visibility = View.GONE
                if (spinnerBillType.selectedIndex != 0)
                    labelErrorBillPaymentType.visibility = View.GONE

                if (txtCities.length() == 3) {
                    showDialogProgress(getString(R.string.message_dialog_load_data))
                    viewModel.getCities(txtCities.text.toString().toUpperCase())

                }

            }
        })

    }

    @SuppressLint("CheckResult")
    private fun formatInputCurrency(txtInput: EditText, lblError: TextView) {

        RxTextView.textChanges(txtInput)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (txtInput.text.toString().isNotEmpty()) {
                    lblError.visibility = View.GONE
                    try {
                        var originalString = txtInput.text.toString()
                        originalString = originalString.replace(".", "").replace("$", "")
                        val value = originalString.toDouble()
                        txtInput.setText(
                            getString(
                                R.string.text_label_parameter_coin,
                                formatValue(value)
                            )
                        )
                        txtInput.setSelection(txtInput.text.length)
                        if (resetValueFormat(txtInput.text.toString()).toInt() < RUtil.R_string(R.string.GIRO_MIN_VALUE).toInt()) {
                            lblError.visibility = View.VISIBLE
                        } else {
                            lblError.visibility = View.GONE
                        }

                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }
                } else {
                    lblError.visibility = View.VISIBLE
                }
            }, {})

    }

    private fun initListenersOnClick() {

        btnBack.setOnClickListener { activity?.onBackPressed() }
        btnPay.setOnClickListener { calculateChangeBill() }
        btnFilter.setOnClickListener { sendFilter() }
        btnQuery.setOnClickListener { queryBill() }
        formatInputCurrency(txtBillValue, labelErrorBillValue)

        txtCities.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (count == 0 && s.isEmpty())
                    labelErrorBillPaymentCity.visibility = View.VISIBLE
                else
                    labelErrorBillPaymentCity.visibility = View.GONE
            }
        })

        txtReferenceNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (count == 0 && s.isEmpty())
                    labelErrorReferenceNumber.visibility = View.VISIBLE
                else
                    labelErrorReferenceNumber.visibility = View.GONE
            }
        })

        spinnerBillType.setOnItemSelectedListener { view, position, id, item ->
            if (position > 0)
                labelErrorBillPaymentType.visibility = View.GONE
            else labelErrorBillPaymentType.visibility = View.VISIBLE
        }

        spinnerAgreement.setOnItemSelectedListener { view, position, id, item ->
            if (position > 0)
                labelErrorBillAgreement.visibility = View.GONE
            else labelErrorBillAgreement.visibility = View.VISIBLE
        }


    }

    private fun queryBill() {
        hideKeyboard()
        if (validateBillData()) {
            showDialogProgress(getString(R.string.message_dialog_request))
            viewModel.validateBill(getRequest(false))
        }

    }

    private fun getRequest(isBilPay: Boolean): BillRequestDTO {
        val indexBilltype = spinnerBillType.selectedIndex - 1
        val indexAgreements = spinnerAgreement.selectedIndex - 1

        return BillRequestDTO().apply {
            refNumber = txtReferenceNumber.text.toString()
            codeCategorie = categories.get(indexBilltype).codeCategorie
            nameCategorie = categories.get(indexBilltype).nameCategorie
            agreement = AgreementsBbvaDTO().apply {
                agreementCod =
                    lagreementsMap[cities.get(selectCity).daneCodigo]?.get(indexAgreements)
                        ?.agreementId
                agreementName =
                    lagreementsMap[cities.get(selectCity).daneCodigo]?.get(indexAgreements)
                        ?.description
            }
            value = Integer.valueOf(txtBillValue.text.toString().resetValueFormat())
            if (isBilPay) {
                validationCode = textBilledService.text.toString()
                description = descriptions
                expirationDate = textBillExpDate.text.toString().replace("/", "")
                maximumPaymentDate = textBillPayMostPayDate.text.toString().replace("/", "")
            }
        }
    }

    private fun showResponseData(responseData: BillRequestDTO?) {
        hideDialogProgress()
        textBilledService.text = responseData?.validationCode
        descriptions = responseData!!.description.toString()
        textBillValuePay.text = getString(
            R.string.text_label_parameter_coin_with_space,
            formatValue(responseData?.value?.toDouble()!!)
        )
        textBillExpDate.text = DateUtil.addBackslashToStringDate(responseData.expirationDate ?: "")
        textBillPayMostPayDate.text =
            DateUtil.addBackslashToStringDate(responseData.expirationDate ?: "")
    }

    private fun sendFilter() {

        hideKeyboard()
        if (validateBillPayment()) {
            billDataForm.visibility = View.VISIBLE
            listAgreement()
        }
    }

    private fun validateBillPayment(): Boolean {
        var isValid = true

        if (spinnerBillType.selectedIndex == 0) {
            isValid = false
            labelErrorBillPaymentType.visibility = View.VISIBLE
        } else if (txtCities.text.isEmpty() || txtCities.text.toString().trim().length < 4) {
            isValid = false
            labelErrorBillPaymentCity.visibility = View.VISIBLE
        }

        return isValid
    }

    private fun validateBillData(): Boolean {
        var isValid = true

        if (!validateBillPayment())
            isValid = false
        if (spinnerAgreement.selectedIndex == 0) {
            isValid = false
            labelErrorBillAgreement.visibility = View.VISIBLE
        } else if (txtReferenceNumber.text.isEmpty()) {
            isValid = false
            labelErrorReferenceNumber.visibility = View.VISIBLE
        } else if (txtBillValue.text.isEmpty()) {
            isValid = false
            labelErrorBillValue.visibility = View.VISIBLE
        }

        return isValid
    }

    private fun listAgreement() {

        if (lagreementsMap[cities.get(selectCity).daneCodigo] == null) {
            billDataForm.visibility = View.GONE
            showOkAlertDialog("", "No hay convenios disponibles para esta ciudad")
        } else {
            val items = mutableListOf<String>()
            items.add(0, getString(R.string.bbva_bill_payment_agreement_placeholder))
            lagreementsMap[cities.get(selectCity).daneCodigo]!!.forEachIndexed { index, item ->
                "${item.agreementId} - ${item.description}".let { items.add(it) }
            }
            spinnerAgreement.setItems(items)
            if (spinnerAgreement.selectedIndex == 0)
                labelErrorBillAgreement.visibility = View.VISIBLE

        }

    }

    @SuppressLint("CheckResult")
    private fun calculateChangeBill() {

        if (validateBillData()) {

            val inflater = this.layoutInflater
            val mDialogView = inflater.inflate(R.layout.dialog_bbva_calculate_change, null)
            mDialogView.findViewById<TextView>(R.id.lblValue).text = txtBillValue.text
            mDialogView.findViewById<EditText>(R.id.txtAmountReceived).text = txtBillValue.text
            mDialogView.findViewById<EditText>(R.id.txtAmountReceived)
                .setSelection(txtBillValue.text.count())
            val alertDialog = activity?.let {
                androidx.appcompat.app.AlertDialog.Builder(it).apply {
                    setTitle(RUtil.R_string(R.string.bbva_title_bill_calculate_change))
                    setView(mDialogView)
                    setCancelable(false)
                    setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton(RUtil.R_string(R.string.text_btn_pay)) { dialog, _ ->
                        confirmBillPayment()
                    }
                }.show()
            }

            alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.secondaryText
                )
            )

            val amountReceived = mDialogView.findViewById<EditText>(R.id.txtAmountReceived)
            val amountReceivedError = mDialogView.findViewById<TextView>(R.id.labelErrorBbvaAmount)

            formatInputCurrency(amountReceived, amountReceivedError)
            setupDialogEvent(mDialogView, amountReceived)

        }

    }

    private fun setupDialogEvent(mDialogView: View, amountReceived: EditText) {

        amountReceived.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (count == 0 && s.isEmpty()) {
                    mDialogView.findViewById<TextView>(R.id.labelErrorBbvaAmount).visibility =
                        View.VISIBLE
                } else {
                    if (s.toString() == "$") {
                        mDialogView.findViewById<TextView>(R.id.labelErrorBbvaAmount).visibility =
                            View.VISIBLE
                    } else {
                        val mount1: String = resetValueFormat(s.toString())
                        val mount2: String =
                            resetValueFormat(mDialogView.findViewById<TextView>(R.id.lblValue).text.toString())
                        if (mount1.toLong() < mount2.toLong()) {
                            mDialogView.findViewById<TextView>(R.id.labelErrorBbvaAmount)
                                .visibility = View.VISIBLE
                        } else {
                            mDialogView.findViewById<TextView>(R.id.labelErrorBbvaAmount)
                                .visibility = View.GONE
                            mDialogView.findViewById<TextView>(R.id.lblAmountBack).text =
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue((mount1.toLong() - mount2.toLong()).toDouble())
                                )
                        }
                    }
                }

            }
        })
    }

    private fun confirmBillPayment() {
        val inflater = this.layoutInflater
        val mDialogView = inflater.inflate(R.layout.layout_bbva_confirm_payment_bill, null)
        mDialogView.findViewById<TextView>(R.id.lblObligationsBillType).text =
            spinnerBillType.text.toString()
        mDialogView.findViewById<TextView>(R.id.lblAgreement).text =
            spinnerAgreement.text.toString()
        mDialogView.findViewById<TextView>(R.id.lblReferenceNumber).text =
            txtReferenceNumber.text.toString()
        mDialogView.findViewById<TextView>(R.id.lblTotalBillValue).text =
            txtBillValue.text.toString()
        val alertDialog = activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_pay_modal_title))
                setView(mDialogView)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(RUtil.R_string(R.string.text_btn_pay)) { dialog, _ ->
                    payBbvaBill()
                }
            }.show()
        }

        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.secondaryText
            )
        )

    }

    private fun payBbvaBill() {
        showDialogProgress(getString(R.string.message_dialog_request))
        viewModel.billPayment(getRequest(true))
    }

    private fun paymentSuccess(message: ResponseBbvaBillPaymentDTO) {
        hideDialogProgress()
        showOkAlertDialog(RUtil.R_string(R.string.bbva_payment_success_title), RUtil.R_string(R.string.bbva_bill_payment_success) )
    }

    private fun resetValueFormat(amount: String) = amount.replace("$", "").replace(".", "")

}
