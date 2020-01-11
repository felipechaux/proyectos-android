package co.com.pagatodo.core.views.registraduria

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseRegistraduriaGetCollectionDTO
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_registraduria_reprint_collection.*

class RegistraduriaReprintCollectionActivity : BaseActivity() {

    private lateinit var viewModel: RegistraduriaCollectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registraduria_reprint_collection)
        setupView()
    }

    private  fun setupView(){
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.home_menu_title_registraduria_collection))
        setupViewModel()
        initListeners()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(RegistraduriaCollectionViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it){
                is RegistraduriaCollectionViewModel.ViewEvent.Errors -> {
                    showOkAlertDialog("",it.errorMessage ?: "")
                }
            }
        })
    }

    private fun initListeners(){

        btnRegistraduriaReprintReprint.setOnClickListener{
            submitReprint()
        }

        txtSalePin.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (count == 0 && s.isEmpty())
                    labelErrorSalePin.visibility = View.VISIBLE
                else
                    labelErrorSalePin.visibility = View.GONE
            }
        })

    }

    private fun submitReprint() {
        if (validateFields()) {
            val alertDialog = this.let {
                androidx.appcompat.app.AlertDialog.Builder(it).apply {
                    setTitle(RUtil.R_string(R.string.registraduria_modal_reprint_collection_title))
                    setMessage(RUtil.R_string(R.string.registraduria_modal_reprint_collection_message))
                    setCancelable(false)
                    setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { _, _ ->
                        reprintCollection()
                    }
                }.show()
            }

            alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.secondaryText
                )
            )
        }
    }

    private fun reprintCollection() {
        var pinNumber = txtSalePin.text.toString()
        var isReprint = true
        viewModel.queryRegistraduriaGetCollection(pinNumber, isReprint)
    }

    private fun validateFields(): Boolean {

        if (txtSalePin.text.isEmpty()) {
            labelErrorSalePin.visibility = View.VISIBLE
            return false
        } else {
            labelErrorSalePin.visibility = View.GONE
        }

        return true

    }

}
