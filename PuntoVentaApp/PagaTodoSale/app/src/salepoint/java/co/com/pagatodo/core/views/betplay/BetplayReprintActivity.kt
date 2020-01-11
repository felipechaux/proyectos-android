package co.com.pagatodo.core.views.betplay

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_betplay_reprint.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class BetplayReprintActivity : BaseActivity() {

    private lateinit var viewModel: BetplayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_betplay_reprint)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.betplay_reprint_title))
        initListenersViews()
        btnNext.setText(R.string.text_btn_reprint_arrow)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BetplayViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer { itl ->
            hideDialogProgress()
            when (itl) {
                is BetplayViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    itl.successMessage?.let { it1 ->
                        showOkAlertDialog("", it1) {
                            navigateToMenu(
                                this
                            )
                        }
                    }
                }
                is BetplayViewModel.ViewEvent.ResponseError -> {
                    if (itl.errorMessage?.take(3).equals(
                            R_string(R.string.code_error_betplay_document_not_found),
                            ignoreCase = true
                        )
                    ) {
                        itl.errorMessage?.let { it1 -> showOkAlertDialog("", it1) { } }
                    } else if (itl.errorMessage?.take(3).equals(
                            R_string(R.string.code_error_betplay_provider_not_response),
                            ignoreCase = true
                        )
                    ) {
                        itl.errorMessage?.let { it1 -> showOkAlertDialog("", it1) { } }
                    } else {
                        showOkAlertDialog("", R_string(R.string.message_error_transaction))
                    }
                }
                is BetplayViewModel.ViewEvent.ResponseReprintValues -> {
                    if (itl.document.isNotEmpty() && itl.value.isNotEmpty()) {
                        txtNumberId.setText(itl.document)
                        txtValueRecharge.setText("$${formatValue(itl.value.toDouble())}")
                        btnNext.setText(R.string.text_btn_next)
                        dispatchReprintRecharge()
                    } else {
                        showOkAlertDialog("", "No existen datos")
                    }
                }
            }
        })

        viewModel.loadProductParameters()
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { dispatchReprintRecharge() }

        showAlertDialog("", getString(R.string.betplay_reprint_message), {
            dispatchReprintRecharge()
        }, {
            finish()
        })

    }

    private fun dispatchReprintRecharge() {
        if (isValidFields()) {
            viewModel.reprint()
        } else {
            showDialogProgress(getString(R.string.message_dialog_request))
            viewModel.dispatchBetplay()
        }
    }

    private fun isValidFields(): Boolean {
        var isValid = true

        if (txtNumberId.text.isEmpty()) {
            isValid = false
        }

        if (txtValueRecharge.text.isEmpty()) {
            isValid = false
        }

        return isValid
    }
}
