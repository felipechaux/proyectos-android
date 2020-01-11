package co.com.pagatodo.core.views.elderly

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ElderlyThirdModel
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_elderly_reprint.*

class ElderlyRePrintActivity : BaseActivity() {

    private lateinit var viewModel: ElderlyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elderly_reprint)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.home_menu_title_elderly))
        initListenersViews()
        setupViewModel()
    }

    private fun initListenersViews() {

        btnNext.setOnClickListener { showConfirm() }

        btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun showConfirm() {

        if (validateForm()) {

            showOkAlertDialog(getString(R.string.home_menu_title_elderly), getString(R.string.elderly_reprint_message)) {

                showDialogProgress(getString(R.string.text_load_data))

                viewModel.elderlyQueryGetReprint(ElderlyThirdModel().apply {
                    this.document = txtDocumentNumber.text.toString()
                    this.documentType = getString(R.string.DOCUMENT_TYPE_DEFAULT)
                })

            }
        }

    }

    private fun validateForm(): Boolean {

        var isValid = true


        if (txtDocumentNumber.text.toString().isEmpty()) {
            labelErrorDocumentNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDocumentNumber.visibility = View.GONE
        }

        return isValid
    }

    private fun setupViewModel() {

        viewModel = ViewModelProviders.of(this).get(ElderlyViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {

            when(it){
                is ElderlyViewModel.ViewEvent.Errors -> showErrors(it.errorMessage)
            }
        })

    }

    private fun showErrors(errorMessage:String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage)

    }
}
