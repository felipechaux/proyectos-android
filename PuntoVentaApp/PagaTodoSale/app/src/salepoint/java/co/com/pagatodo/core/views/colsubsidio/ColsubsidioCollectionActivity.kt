package co.com.pagatodo.core.views.colsubsidio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseColsubsidioGetProductsDTO
import co.com.pagatodo.core.data.model.ColsubsidioObligationModel
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.salepoint.activity_colsubsidio_collection.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.PayColsubsidioCollectionAdapter
import kotlinx.android.synthetic.salepoint.dialog_pay_colsubsidio_collection.view.*
import kotlinx.android.synthetic.salepoint.layout_buttons_back_next.*

class ColsubsidioCollectionActivity : BaseActivity() {

    private lateinit var viewModel: ColsubsidioCollectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colsubsidio_collection)
        setupView()
        setupViewModel()
    }



    private fun setupView(){
        setupBaseView()
        updateTitle("Recaudos Colsubsidio")
        payColsubsidioCollectionList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ColsubsidioCollectionViewModel::class.java)


        viewModel.singleLiveEvent.observe(this@ColsubsidioCollectionActivity, Observer {
            when (it) {

                is ColsubsidioCollectionViewModel.ViewEvent.ResponseSuccessColsubsidioGetProducts->loadProducts(it.responseDTO)
                is ColsubsidioCollectionViewModel.ViewEvent.ResponseError -> showErrors(it.errorMessage!!)
            }
        })
        showDialogProgress(getString(R.string.message_dialog_load_data))

        viewModel.queryColsubsidioGetProducts()


        viewModel.obligation.observe(this, Observer {
            hideDialogProgress()

            setupCollectionAdapter(it)
        })
        setupCollectionAdapter(ColsubsidioObligationModel() )

    }

    private fun loadProducts(products: ResponseColsubsidioGetProductsDTO?) {

        println("loadProductos!!!!!!!")
    }

    private fun initListenersViews(){
        initListenersOnClick()
    }

    private fun listDocumentType(){

        val list = ArrayList<String>()
        list.add("NIT")
        list.add("CC")
        list.add("CE")
        list.add("Pasaporte")

        spinnerDocumentType.setItems(list)
        spinnerDocumentType.selectedIndex = list.indexOf("CC")
    }

    private fun initListenersOnClick(){

        btnClean.setOnClickListener {
            Log.e("col","colclean")
        }
        btnSearch.setOnClickListener { getCollection() }
        btnNext.setOnClickListener { sendCollectionColsubsidio() }
        btnBack.setOnClickListener { finish() }
        listDocumentType()

        txtDocumentNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                labelErrorDocumentNumber.visibility = View.GONE

            }
        })

        txtReferenceNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                labelErrorReferenceNumber.visibility = View.GONE

            }
        })

    }

    private fun getCollection() {

        if(validateFields()){
            showDialogProgress(getString(R.string.text_load_data))
        }
    }

    private fun sendCollectionColsubsidio() {
       if(validateFields())
           showConfirmationDialog()

    }

    private fun showConfirmationDialog() {
      //  val description = viewModel.getAgreementDescription(spinner_agreements.selectedIndex)

       // val bill = viewModel.getBill()

      /*  if (bill != null && bill.valueToPay?.isNotEmpty() == true){*/
            PaymentConfirmationDialog().show(this, R.layout.dialog_pay_colsubsidio_collection,
                R_string(R.string.title_ticket_pay_colsubsidio_collection), null, { view ->
                    view.textRecaudoDescription.text = ""
                    view.textRecaudoUserName.text = ""
                    view.textRecaudoBillNumber.text = ""
                    view.textRecaudoBillValue.text = ""
                    view.textRecaudoIva.text = ""
                    view.textRecaudoTotalPaid.text = ""
                },{
                    //showDialogProgress(getString(R.string.message_dialog_request))
                    //viewModel.dispatchPayBill(description?.idCompanyService ?: "", txtNumberRequested.text.toString())
                })
       /* }else{
            showOkAlertDialog("", "Error, consulte una factura")
        }*/
    }

    private fun validateFields(): Boolean{
        var isValid = true

        if (txtDocumentNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorDocumentNumber.visibility = View.VISIBLE
            labelErrorDocumentNumber.text = "Digita el número de documento"
        } else {
            labelErrorDocumentNumber.visibility = View.GONE
        }

        if (txtReferenceNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorReferenceNumber.visibility = View.VISIBLE
            labelErrorReferenceNumber.text = "Digita el número de referencia"
        } else {
            labelErrorReferenceNumber.visibility = View.GONE
        }
        return isValid
    }


    private fun setupCollectionAdapter(obligationModel: ColsubsidioObligationModel){

        val obligationList = mutableListOf<ColsubsidioObligationModel>()

        obligationList.add(obligationModel)
        obligationList.add(obligationModel)
        obligationList.add(obligationModel)

        val adapter = PayColsubsidioCollectionAdapter(obligationList,viewModel)
        payColsubsidioCollectionList.adapter = adapter
    }

    private fun showErrors(error: String) {

        hideDialogProgress()
        showOkAlertDialog("", error)

    }




}
