package co.com.pagatodo.core.views.stub

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.text.InputFilter
import androidx.lifecycle.Observer
import co.com.pagatodo.core.R
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_stub.*
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.data.database.DatabaseViewModel
import co.com.pagatodo.core.data.model.StubModel
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.login.LoginActivity
import co.com.pagatodo.core.views.adapters.StubAdapter
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.views.homemenu.MainMenuActivity

class StubActivity : BaseActivity() {

    private lateinit var viewModel: StubViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stub)
        
        setupModel()
        setupView()
    }

    @SuppressLint("CheckResult")
    private fun setupModel() {
        viewModel = ViewModelProviders.of( this).get(StubViewModel::class.java)
        viewModel.stubLiveData.observe(this, Observer <List<StubModel>> {
            validateBluetoothConnection()
            updateStubsList(it)
        })

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it) {
                is StubViewModel.ViewEvent.UpdateSuccess -> {
                    navigateToOption(MainMenuActivity::class.java)
                }
                is StubViewModel.ViewEvent.UpdateError -> {
                    showOkAlertDialog(R_string(R.string.title_dialog_error), it.errorMessage)
                }
                is StubViewModel.ViewEvent.ResponseSuccess -> {

                    navigateToOption(LoginActivity::class.java,true)
                }
                is StubViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("", it.errorMessage.toString())
                }
                is StubViewModel.ViewEvent.SaveMenus -> {
                    navigateToOption(MainMenuActivity::class.java)
                }
            }
        })

        viewModel.loadAllStubs()
    }

    private fun setupView() {
        title = getString(R.string.stub_title)
        etSerie1.filters = arrayOf<InputFilter>(InputFilter.AllCaps(), InputFilter.LengthFilter(5))
        btnNext.setOnClickListener(::actionBtnNext)
        btnBack.setOnClickListener {
            showAlertDialog(
                "",
                R_string(R.string.text_message_close_activity),
                { logout() },
                {}
            )
        }

        rvStubs.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun updateStubsList(data: List<StubModel>) {
        rvStubs.adapter = StubAdapter(data)
    }

    private fun validateSeriesTextfield(serie1: String, serie2: String, callback: () -> Unit) {
        if (serie1.trim() != "" && serie2.trim() != "") {
            callback()
        } else {
            showOkAlertDialog(getString(R.string.title_dialog_error), getString(R.string.message_error_input_stub))
        }
    }

    private fun actionBtnNext(view: View) {
        val serie1 = etSerie1.text.toString().toUpperCase()
        val serie2 = etSerie2.text.toString().toUpperCase()
        validateSeriesTextfield(serie1, serie2) {
            val response = viewModel.saveCurrentStubSeries(serie1, serie2)
            if (response) {
                executeUpdateStub()
            } else {
                showOkAlertDialog(getString(R.string.title_dialog_error), getString(R.string.message_error_save_current_stub))
            }
        }
    }

    override fun onBackPressed() {
        showAlertDialog(
            RUtil.R_string(R.string.text_title_close_activity),
            R_string(R.string.text_message_close_activity),
            { logout() },
            {}
        )
    }

    private fun logout() {
        showDialogProgress(getString(R.string.message_dialog_request))
        viewModel.logout()
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>,isLogin:Boolean = false) {
        val intent = Intent(this, classType)

        if(isLogin){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }

    private fun executeUpdateStub() {
        val stub = getStubs()
        val request = RequestUtilModel().apply {
            this.serie1 = stub.serie1
            this.serie2 = stub.serie2
            this.sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
        }
        showDialogProgress(R_string(R.string.message_dialog_load_data))
        viewModel.updateStubInServer(request)
    }
}
