package co.com.pagatodo.core.views.betplay

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.salepoint.activity_betplay_collect.*

class BetplayCollectActivity : BaseActivity() {


    private lateinit var viewModel: BetplayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_betplay_collect)
        setupViewModel()
        setupView()
    }

    private fun setupViewModel() {

        viewModel = ViewModelProviders.of(this).get(BetplayViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            when (it) {

                is BetplayViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    if (it.errorMessage?.take(3).equals(
                            RUtil.R_string(R.string.code_error_betplay_document_not_found),
                            ignoreCase = true
                        )
                    ) {
                        it.errorMessage?.let { it1 -> showOkAlertDialog("", it1) { } }
                    } else if (it.errorMessage?.take(3).equals(
                            RUtil.R_string(R.string.code_error_betplay_provider_not_response),
                            ignoreCase = true
                        )
                    ) {
                        it.errorMessage?.let { it1 -> showOkAlertDialog("", it1) { } }
                    } else {
                        showOkAlertDialog("", RUtil.R_string(R.string.message_error_transaction))
                    }
                }


            }

        })

    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.betplay_collect_title))
        initListenersViews()
    }

    private fun initListenersViews() {
        initListenersTextWatcher()
        initListenersOnClick()
    }

    private fun initListenersOnClick() {

        btnNext.setOnClickListener {

            if (validateControls()) {
                chargeBetplay()
            }

        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun chargeBetplay() {

        showDialogProgress(getString(R.string.text_load_data))
        viewModel.dispatchCheckOutBetplay(
            txtNumberId.text.toString(),
            txtCollectValue.text.toString().resetValueFormat().toInt(),
            txtCollectPin.text.toString().toInt()
        )


    }

    private fun initListenersTextWatcher() {

        initListenersTextWatcherValue()
        initListenersTextWatcherValueConfirm()

        initListenersTextWatcherId()

    }

    @SuppressLint("CheckResult")
    private fun initListenersTextWatcherValue() {

        var isWatcher = true

        txtCollectValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                if (isWatcher) {

                    isWatcher = false
                    if (txtCollectValue.text.toString().isNotEmpty()) {

                        labelErrorCollectValue.visibility = View.GONE
                        try {
                            var originalString = txtCollectValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            txtCollectValue.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            txtCollectValue.setSelection(txtCollectValue.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        labelErrorCollectValue.visibility = View.VISIBLE
                        labelErrorCollectValue.setText(RUtil.R_string(R.string.giro_send_error_amount_send))
                    }

                    isWatcher = true

                }
            }
        })

    }

    @SuppressLint("CheckResult")
    private fun initListenersTextWatcherValueConfirm() {



        txtCollectPin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {

                labelErrorCollectPin.visibility = View.GONE

            }
        })

    }

    private fun initListenersTextWatcherId() {

        txtNumberId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumberId.visibility = View.GONE
            }
        })

        txtNumberIdConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumberIdConfirm.visibility = View.GONE
                labelErrorNumberIdNotEqual.visibility = View.GONE
            }
        })


    }

    private fun validateControls(): Boolean {
        var isValid = true

        if (txtNumberId.text.isEmpty()) {
            labelErrorNumberId.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorNumberId.visibility = View.GONE
        }

        labelErrorNumberIdNotEqual.visibility = View.GONE
        labelErrorNumberIdConfirm.visibility = View.GONE

        if (txtNumberIdConfirm.text.isEmpty()) {
            labelErrorNumberIdConfirm.visibility = View.VISIBLE
            isValid = false
        } else if (!(txtNumberId.text.isEmpty() && txtNumberIdConfirm.text.isEmpty())) {
            if (txtNumberId.text.toString() != txtNumberIdConfirm.text.toString()) {
                labelErrorNumberIdNotEqual.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorNumberIdNotEqual.visibility = View.GONE
            }
        } else {
            labelErrorNumberIdNotEqual.visibility = View.GONE
            labelErrorNumberIdConfirm.visibility = View.GONE
        }

        if (txtCollectValue.text.toString().isEmpty() || txtCollectValue.text.toString() == "$") {
            isValid = false
            labelErrorCollectValue.visibility = View.VISIBLE
        } else {
            labelErrorCollectValue.visibility = View.GONE
        }


        if (txtCollectPin.text.toString().isEmpty() ) {
            isValid = false
            labelErrorCollectPin.visibility = View.VISIBLE
        } else {
            labelErrorCollectPin.visibility = View.GONE
        }

        return isValid
    }

}
