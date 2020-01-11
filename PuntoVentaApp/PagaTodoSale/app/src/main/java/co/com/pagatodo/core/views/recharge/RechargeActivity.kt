package co.com.pagatodo.core.views.recharge

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_recharge.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.OperatorModel
import co.com.pagatodo.core.data.model.PackageModel
import co.com.pagatodo.core.data.model.RequestRechargeModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recharge.labelErrorConfirmNumberFormat
import kotlinx.android.synthetic.main.activity_recharge.labelErrorConfirmNumberLimit
import kotlinx.android.synthetic.main.activity_recharge.labelErrorNumber
import kotlinx.android.synthetic.main.activity_recharge.labelErrorNumberConfirm
import kotlinx.android.synthetic.main.activity_recharge.labelErrorNumberConfirmNotEqual
import kotlinx.android.synthetic.main.activity_recharge.labelErrorNumberFormat
import kotlinx.android.synthetic.main.activity_recharge.labelErrorNumberLimit
import kotlinx.android.synthetic.main.activity_recharge.labelErrorOperator
import kotlinx.android.synthetic.main.activity_recharge.labelErrorSpinnerValue
import kotlinx.android.synthetic.main.activity_recharge.labelErrorValue
import kotlinx.android.synthetic.main.activity_recharge.labelPackages
import kotlinx.android.synthetic.main.activity_recharge.layoutSpinnerPackages
import kotlinx.android.synthetic.main.activity_recharge.layoutSpinnerValue
import kotlinx.android.synthetic.main.activity_recharge.spinner_operators
import kotlinx.android.synthetic.main.activity_recharge.spinner_package
import kotlinx.android.synthetic.main.activity_recharge.spinner_value
import kotlinx.android.synthetic.main.activity_recharge.tvRechargeValue
import kotlinx.android.synthetic.main.activity_recharge.txtNumberRecharge
import kotlinx.android.synthetic.main.activity_recharge.txtNumberRechargeConfirm
import kotlinx.android.synthetic.main.activity_recharge.txtRechargeValue
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import java.util.concurrent.TimeUnit


class RechargeActivity : BaseActivity() {

    private lateinit var operators: MutableList<OperatorModel>
    private lateinit var packages: MutableList<PackageModel>
    private lateinit var rechargeViewModel: RechargeViewModel
    private var rechargeValue = ""
    private var rechargeValueConfirm = ""
    private var minValue = 0
    private var maxValue = 0
    private var multiple = 100
    private var minDigits = 0
    private var maxDigits = 0
    private var isOtherValue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.recharge_title))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        rechargeViewModel = ViewModelProviders.of(this).get(RechargeViewModel::class.java)
        initSubscriptions()
        rechargeViewModel.loadOperators()
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { lauchRecharge() }
        initListenersEditText(txtRechargeValue)
        initListenersTextWatcher()
        initListenersFocusChange()
    }

    @SuppressLint("CheckResult")
    private fun initListenersEditText(txtValue: EditText) {
        RxTextView.textChanges(txtValue)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (txtValue.text.toString().isNotEmpty()) {
                    try {
                        var originalString = txtValue.text.toString()
                        originalString = originalString.replace(".", "").replace("$", "")
                        val value = originalString.toDouble()
                        if (txtRechargeValue.hasFocus()) {
                            rechargeValue = originalString
                        }
                        txtValue.setText(
                            getString(
                                R.string.text_label_parameter_coin,
                                formatValue(value)
                            )
                        )
                        txtValue.setSelection(txtValue.text.length)
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }
                }
            }
    }

    private fun initListenersTextWatcher() {
        txtNumberRecharge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumber.visibility = View.GONE
                labelErrorNumberLimit.visibility = View.GONE
                labelErrorNumberFormat.visibility = View.GONE
            }
        })

        txtNumberRechargeConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumberConfirmNotEqual.visibility = View.GONE
                labelErrorNumberConfirm.visibility = View.GONE
                labelErrorConfirmNumberFormat.visibility = View.GONE
                labelErrorConfirmNumberLimit.visibility = View.GONE
            }
        })
    }

    private fun initListenersFocusChange() {
        txtRechargeValue.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                labelErrorValue.visibility = View.GONE
            }
        }

    }

    private fun initSubscriptions() {
        rechargeViewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is RechargeViewModel.ViewEvent.ResponseOperators -> {
                    operators = it.operators as MutableList<OperatorModel>
                    operators.add(0, OperatorModel())
                    rechargeViewModel.getProductParameters()
                    createItemsForSpinnerOperators()
                }
                is RechargeViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    it.successMessage?.let { it1 ->
                        showOkAlertDialog(
                            "",
                            it1
                        ) { navigateToMenu(this) }
                    }
                }
                is RechargeViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    it.errorMessage?.let { it1 -> showOkAlertDialog("", it1) }
                }
                is RechargeViewModel.ViewEvent.RechargeSuccess -> showConfirmPrint(it.responseRechargeModel)
                is RechargeViewModel.ViewEvent.ResponseProductParameters -> loadParameters(it.productParameters)
            }
        })
    }

    private fun loadParameters(productParameters: List<KeyValueModel>) {

        val listValues = (productParameters
            .filter { it.key == RUtil.R_string(R.string.recharge_parameter_values) }
            .map { it.value }
            .last()
            ?.split(",")
            ?.map { "$${formatValue(it.toDouble())}" }) as MutableList<String>

        val items = mutableListOf<String>()

        items.add(getString(R.string.placeholder_recharge_value))
        items.add(getString(R.string.recharge_placeholder_value_other))
        items.addAll(listValues)

        spinner_value.setItems(items)

        spinner_value.setOnItemSelectedListener { view, position, id, item ->

            labelErrorSpinnerValue.visibility = View.GONE

            if (item == getString(R.string.recharge_placeholder_value_other)) {

                txtRechargeValue.visibility = View.VISIBLE
                txtRechargeValue.setText("")
                txtRechargeValue.setHint(getString(R.string.placeholder_value_recharge))
                tvRechargeValue.visibility = View.VISIBLE
                tvRechargeValue.setText(getString(R.string.text_recharge_value_other))
                isOtherValue = true

            } else {

                isOtherValue = false
                tvRechargeValue.visibility = View.GONE
                txtRechargeValue.setText("")
                txtRechargeValue.visibility = View.GONE
            }


        }


    }

    private fun createItemsForSpinnerOperators() {
        val items = mutableListOf<String>()
        items.add(0, getString(R.string.placeholder_operator))
        operators.forEachIndexed { index, item ->
            if (index != 0) {
                items.add(item.operatorName.toString())
            }
        }
        setItemsInSpinnerOperators(items)
    }

    private fun setItemsInSpinnerOperators(items: List<String>) {
        spinner_operators.setItems(items)
        spinner_operators.setOnItemSelectedListener { _, position, _, _ ->
            labelErrorOperator.visibility = View.GONE
            if (position != 0) {

                val operator = operators[position]
                minValue = operator.minValue ?: 0
                maxValue = operator.maxValue ?: 0
                multiple = operator.multipleDigits ?: 0
                minDigits = operator.minDigits ?: 0
                maxDigits = operator.maxDigist ?: 0
                spinner_package.selectedIndex = 0
                operator.packages?.let {
                    val packages = operator.packages
                    createItemsForSpinnerPackages(packages)
                }

                txtNumberRecharge.filters = arrayOf(InputFilter.LengthFilter(maxDigits))
                txtNumberRechargeConfirm.filters = arrayOf(InputFilter.LengthFilter(maxDigits))

            }

            txtRechargeValue.setText("")

            txtRechargeValue.isEnabled = true
            txtRechargeValue.visibility = View.GONE
            tvRechargeValue.visibility = View.GONE

            tvRechargeValue.setText(getText(R.string.placeholder_value_recharge))

            spinner_value.visibility = View.VISIBLE
            txtNumberRechargeConfirm.filters = arrayOf(InputFilter.LengthFilter(maxDigits))
            layoutSpinnerValue.visibility = View.VISIBLE
            labelErrorSpinnerValue.visibility = View.GONE
            labelErrorValue.visibility = View.GONE
            spinner_value.selectedIndex = 0


        }
    }

    private fun createItemsForSpinnerPackages(packages: List<PackageModel>?) {
        val items = mutableListOf<String>()
        this.packages = mutableListOf()
        items.add(0, getString(R.string.placeholder_package))
        packages?.forEach {
            items.add(it.packageName.toString())
            this.packages.add(it)
        }
        if (items.size == 1) {
            labelPackages.visibility = View.GONE
            layoutSpinnerPackages.visibility = View.GONE
        } else {
            labelPackages.visibility = View.VISIBLE
            layoutSpinnerPackages.visibility = View.VISIBLE
            spinner_package.setItems(items)
            spinner_package.setOnItemSelectedListener { _, position, id, _ ->
                if (position != 0) {
                    val value = this.packages[position - 1].packageValue
                    txtRechargeValue.isEnabled = false

                    if (value != null) {
                        txtRechargeValue.setText("$${formatValue(value.toDouble())}")
                        rechargeValue = value.toString()
                        rechargeValueConfirm = value.toString()
                        labelErrorValue.visibility = View.GONE

                        spinner_value.visibility = View.GONE
                        layoutSpinnerValue.visibility = View.GONE
                        txtRechargeValue.visibility = View.VISIBLE
                        txtRechargeValue.isEnabled = false

                        if(DeviceUtil.isSalePoint()) {
                            layoutRechargueValue.visibility = View.GONE
                            tvRechargeValue.text = (getString(R.string.text_recharge_value).toString())
                            tvRechargeValue.visibility = View.VISIBLE
                            layoutRechargueEmpty.visibility = View.VISIBLE
                        }

                    }

                } else {
                    txtRechargeValue.isEnabled = true

                    txtRechargeValue.setText("")
                    txtRechargeValue.visibility = View.GONE
                    spinner_value.visibility = View.VISIBLE
                    layoutSpinnerValue.visibility = View.VISIBLE
                    spinner_value.selectedIndex = 0

                    rechargeValue = ""
                    rechargeValueConfirm = ""

                    if(DeviceUtil.isSalePoint()) {
                        tvRechargue.text = (getString(R.string.placeholder_value_recharge).toString())
                        layoutRechargueValue.visibility = View.VISIBLE
                        tvRechargeValue.visibility = View.GONE
                        layoutRechargueEmpty.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun lauchRecharge() {
        txtRechargeValue.clearFocus()

        if (isValidForm()) {

            if (validateValue())
                showConfirmationDialog()
            else {

                val max = "$${formatValue(maxValue.toDouble())}"
                val min = "$${formatValue(minValue.toDouble())}"

                showOkAlertDialog(
                    "",
                    getString(R.string.recharge_message_validate_values, min, max)
                )
            }

        }
    }

    private fun createRechargeModel(): RequestRechargeModel {

        var mCodePackage:String? = null
        val pakage = packages.filter{ it.packageName == spinner_package.text.toString()}

        if(pakage.size > 0){
            mCodePackage = pakage.first().packageCode
        }

        return RequestRechargeModel().apply {
            number = txtNumberRecharge.text.toString()
            operatorCode = operators[spinner_operators.selectedIndex].operatorCode.toString()
            rechargeAmount = if (isOtherValue || spinner_package.selectedIndex > 0) {
                txtRechargeValue.text.toString().resetValueFormat()
            } else {
                spinner_value.text.toString().resetValueFormat()
            }
            descroptionPackage = if(spinner_package.selectedIndex > 0) spinner_package.text.toString() else ""
            transactionTime = DateUtil.getCurrentDateInUnix()
            sequenceTransaction = StorageUtil.getSequenceTransaction()
            operatorName = spinner_operators.text.toString()
            codePackage = mCodePackage
        }
    }


    private fun validateValue(): Boolean {

        var isValid = true

        var value = ""
        if (spinner_package.selectedIndex == 0) {

            if (isOtherValue) {
                value = txtRechargeValue.text.toString()
            } else {
                value = spinner_value.text.toString()
            }

        } else {

            value = txtRechargeValue.text.toString()
        }

        value = value.resetValueFormat()

        if (value.toInt() < minValue)
            isValid = false


        if (value.toInt() > maxValue)
            isValid = false

        return isValid

    }

    @SuppressLint("StringFormatMatches")
    private fun isValidForm(): Boolean {
        hideLabelErrors()

        var isValid = true
        if (spinner_operators.text == getString(R.string.placeholder_operator)) {
            labelErrorOperator.visibility = View.VISIBLE
            isValid = false
        }

        if (txtNumberRecharge.text.isEmpty()) {
            labelErrorNumber.visibility = View.VISIBLE
            isValid = false
        } else if (txtNumberRecharge.text.isNotEmpty()) {

            if (txtNumberRecharge.text.length < maxDigits) {
                labelErrorNumberLimit.setText(getString(R.string.message_error_number_bad_limit_parameter,"$maxDigits"))
                labelErrorNumberLimit.visibility = View.VISIBLE
                isValid = false
            }

        }

        if (txtNumberRechargeConfirm.text.isEmpty()) {
            labelErrorNumberConfirm.visibility = View.VISIBLE
            isValid = false
        } else if (txtNumberRechargeConfirm.text.isNotEmpty()) {

            if (txtNumberRechargeConfirm.text.length < maxDigits) {
                labelErrorConfirmNumberLimit.setText(getString(R.string.message_error_number_bad_limit_parameter,"$maxDigits"))
                labelErrorConfirmNumberLimit.visibility = View.VISIBLE
                isValid = false
            } else if (!(txtNumberRecharge.text.isEmpty() && txtNumberRechargeConfirm.text.isEmpty())) {
                if (txtNumberRecharge.text.toString() != txtNumberRechargeConfirm.text.toString()) {
                    labelErrorNumberConfirmNotEqual.visibility = View.VISIBLE
                    isValid = false
                }
            }
        }

        if (spinner_package.selectedIndex == 0) {

            if (isOtherValue) {

                if (txtRechargeValue.text.toString().isEmpty()) {
                    labelErrorValue.visibility = View.VISIBLE
                    isValid = false
                } else {
                    labelErrorValue.visibility = View.GONE
                }

            } else {


                if (spinner_value.text == getString(R.string.placeholder_recharge_value_spinner)) {
                    labelErrorSpinnerValue.visibility = View.VISIBLE
                    isValid = false
                } else {
                    labelErrorSpinnerValue.visibility = View.GONE
                }

            }


        }

        return isValid
    }

    private fun showConfirmationDialog() {
        hideKeyboard()
        PaymentConfirmationDialog().show(this, R.layout.layout_payment_confirm_recharges,
            R_string(R.string.text_title_recharge_dialog), null, { view ->

                view.findViewById<TextView>(R.id.labelOperator).text =
                    spinner_operators.text.toString()

                if (spinner_package.selectedIndex > 0) {
                    view.findViewById<TextView>(R.id.labelPackage).text =
                        spinner_package.text.toString()
                } else {
                    view.findViewById<TextView>(R.id.labelPackage).text = ""
                }
                view.findViewById<TextView>(R.id.labelNumberTitle).text =
                    R_string(R.string.text_label_number)
                view.findViewById<TextView>(R.id.labelNumber).text = txtNumberRecharge.text

                if (spinner_package.selectedIndex > 0) {
                    view.findViewById<TextView>(R.id.labelValue).text = txtRechargeValue.text
                } else {

                    if (!isOtherValue) {

                        view.findViewById<TextView>(R.id.labelValue).text = spinner_value.text

                    } else {

                        view.findViewById<TextView>(R.id.labelValue).text = txtRechargeValue.text
                    }
                }


            }, {
                showDialogProgress(getString(R.string.message_dialog_request))
                isForeground = false
                rechargeViewModel.dispatchRecharge(createRechargeModel())
            })
    }

    private fun showConfirmPrint(responseRechargeModel: ResponseRechargeModel) {

        runOnUiThread {
            val alertDialogo = AlertDialog.Builder(this@RechargeActivity).apply {
                setTitle(getString(R.string.message_success_recharge))
                setMessage(getString(R.string.message_success_recharge_print))
                setCancelable(false)
                setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                    dialog.dismiss()

                    rechargeViewModel.printRecharge(
                        responseRechargeModel,
                        createRechargeModel().rechargeAmount ?: ""
                    )

                }
                setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                    onBackPressed()
                }
            }.show()
            alertDialogo?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    this@RechargeActivity,
                    R.color.secondaryText
                )
            )
        }

    }


    private fun hideLabelErrors() {
        labelErrorOperator.visibility = View.GONE
        labelErrorNumberConfirm.visibility = View.GONE
        labelErrorNumber.visibility = View.GONE
        labelErrorNumberFormat.visibility = View.GONE
        labelErrorNumberLimit.visibility = View.GONE
        labelErrorConfirmNumberFormat.visibility = View.GONE
        labelErrorConfirmNumberLimit.visibility = View.GONE
        labelErrorNumberConfirmNotEqual.visibility = View.GONE
        labelErrorValue.visibility = View.GONE

    }
}
