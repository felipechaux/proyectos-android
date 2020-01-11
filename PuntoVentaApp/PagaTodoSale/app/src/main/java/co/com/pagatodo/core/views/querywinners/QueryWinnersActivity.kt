package co.com.pagatodo.core.views.querywinners

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.OkInformationConfirmDialog
import kotlinx.android.synthetic.main.activity_query_winners.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class QueryWinnersActivity : BaseActivity() {

    private lateinit var viewModel: QueryWinnersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_winners)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.query_winners_title))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(QueryWinnersViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {
                is QueryWinnersViewModel.ViewEvent.ResponseSuccess -> {
                    when (it.codeMessage) {
                        R_string(R.string.code_expired_prize) -> {
                            showPrizeExpired()
                        }
                        R_string(R.string.code_paid_prize) -> {
                            showPrizePayedDialog()
                        }
                        R_string(R.string.code_award_restriction) -> {
                            showPrizeRestrictionDialog()
                        }
                        R_string(R.string.code_user_winner) -> {
                            showWinnerDialog()
                        }
                        R_string(R.string.code_terminal_not_exist) -> {
                            showOkAlertDialog("", it.successMessage ?: "")
                        }
                        R_string(R.string.code_error_seller_not_exist) -> {
                            showOkAlertDialog("", it.successMessage ?: "")
                        }
                        R_string(R.string.code_transaction_error) -> {
                            showStubNotWinnerDialog()
                        }
                    }
                }
                is QueryWinnersViewModel.ViewEvent.ResponseError -> {
                    if (it.codeMessage?.equals(R.string.code_transaction_error) == true) {
                        showStubNotWinnerDialog()
                    } else {
                        showOkAlertDialog("", it.errorMessage ?: "")
                    }
                }
            }
        })
    }

    private fun initListenersViews() {

        txtSerieOne.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        txtSerieTwo.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener {
            if(validateFields()) {
                showDialogProgress(R_string(R.string.message_dialog_request))
                viewModel.dispatchQueryWinners(
                    if(txtSerieOne.text.toString().isEmpty()) null else txtSerieOne.text.toString() ,
                    if(txtSerieTwo.text.toString().isEmpty()) null else txtSerieTwo.text.toString() ,
                    if(txtUniqueSerie.text.toString().isEmpty()) null else txtUniqueSerie.text.toString()
                )
            }
        }

        txtUniqueSerie.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorUniqueSerial.visibility = View.GONE
                labelErrorSerieOne.visibility = View.GONE
                labelErrorSerieTwo.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //not implemented
            }
        })

        txtSerieOne.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorSerieOne.visibility = View.GONE
                labelErrorUniqueSerial.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //not implemented
            }
        })

        txtSerieTwo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorSerieTwo.visibility = View.GONE
                labelErrorUniqueSerial.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //not implemented
            }
        })
    }

    private fun showWinnerDialog() {
        showOkAlertDialog(
            R_string(R.string.title_winner_client),
            R_string(R.string.text_winner_client)
        )
    }

    private fun showPrizePayedDialog() {
        showOkAlertDialog(R_string(R.string.title_prize_payed), R_string(R.string.text_prize_payed))
    }

    private fun showPrizeRestrictionDialog() {
        showOkAlertDialog(
            R_string(R.string.title_prize_restriction),
            R_string(R.string.text_prize_restriction)
        )
    }

    private fun showPrizeExpired() {
        showOkAlertDialog(
            R_string(R.string.title_prize_expired),
            R_string(R.string.text_prize_expired)
        )
    }

    private fun showStubNotWinnerDialog() {
        showOkAlertDialog(
            R_string(R.string.title_stub_not_winner),
            R_string(R.string.text_stub_not_winner)
        )
    }

    private fun validateFields(): Boolean {

        var isValid = true

        if (txtUniqueSerie.text.isEmpty()) {

            if (txtSerieOne.text.isEmpty()) {
                isValid = false
                labelErrorSerieOne.visibility = View.VISIBLE
            }

            if (txtSerieTwo.text.isEmpty()) {
                isValid = false
                labelErrorSerieTwo.visibility = View.VISIBLE
            }
        }

        return isValid
    }
}