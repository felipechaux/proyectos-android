package co.com.pagatodo.core.views.giro


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.TypeBillGiro
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_giro_reprint.*

class GiroReprintFragment : BaseFragment() {

    private lateinit var giroViewModel: GiroViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giro_reprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupViewModel()
        setupFragmentView()

    }

    private fun setupViewModel() {


        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, Observer {


            when (it) {
                is GiroViewModel.ViewEvent.Errors -> showErrors(it.errorMessage)
            }
        })

    }

    private fun showErrors(erros: String) {

        hideDialogProgress()
        showOkAlertDialog("", erros)

    }

    private fun setupFragmentView() {

        initView()
        initListenersViews()

    }

    private fun initView() {

        val itemsUnusualOperations = mutableListOf<String>()

        itemsUnusualOperations.add(0, getString(R.string.giro_reprint_placeholder_type_bill))
        itemsUnusualOperations.add(1, TypeBillGiro.CAPTURE.raw)
        itemsUnusualOperations.add(2, TypeBillGiro.PAY.raw)

        spinnerTypeBill.setItems(itemsUnusualOperations)

    }


    private fun initListenersViews() {

        initListenersTextWatcher()
        initListenersOnClick()

    }

    private fun initListenersOnClick() {

        btnBack.setOnClickListener {

            onBackPressed()

        }
        btnSave.setOnClickListener {
            hideKeyboard()
            rePrint()

        }

        spinnerTypeBill.setOnNothingSelectedListener {
            labelErrorTypeBill.visibility = View.GONE
        }

    }

    private fun rePrint() {

        if (validateRePrint()) {

            showOkAlertDialog(RUtil.R_string(R.string.title_confirm_reprint),RUtil.R_string(R.string.text_confirm_reprint)) {

                showDialogProgress(getString(R.string.message_dialog_load_data))
                giroViewModel.giroRePrint(
                    txtReferenceNumber.text.toString(),
                    spinnerTypeBill.text.toString()
                )
            }

        }

    }

    private fun validateRePrint(): Boolean {

        var isValid = true

        if (spinnerTypeBill.text.isEmpty() || spinnerTypeBill.text.toString() == getString(R.string.giro_reprint_placeholder_type_bill)) {
            labelErrorTypeBill.visibility = View.VISIBLE
            isValid = false
        } else {

            labelErrorTypeBill.visibility = View.GONE

        }

        if (txtReferenceNumber.text.isEmpty()) {
            labelErrorReferenceNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorReferenceNumber.visibility = View.GONE
        }

        return isValid
    }

    private fun initListenersTextWatcher() {

        spinnerTypeBill.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorTypeBill.visibility = View.GONE
            }
        })

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

                if (txtReferenceNumber.text.isEmpty()) {
                    labelErrorReferenceNumber.visibility = View.VISIBLE

                } else {
                    labelErrorReferenceNumber.visibility = View.GONE
                }

            }

        })


    }


}
