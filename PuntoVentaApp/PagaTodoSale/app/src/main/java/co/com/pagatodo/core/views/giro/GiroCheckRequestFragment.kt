package co.com.pagatodo.core.views.giro


import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GiroTypeRequestsDTO
import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.data.dto.response.ResponseGiroCheckRequestDTO
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.adapters.GiroCheckRequestAdapter
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_giro_check_request.*


class GiroCheckRequestFragment : BaseFragment() {


    private lateinit var giroViewModel: GiroViewModel
    private var mtypeRequest: List<GiroTypeRequestsDTO>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giro_check_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()

    }

    private fun setupFragmentView() {

        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, Observer {

            when (it) {
                is GiroViewModel.ViewEvent.Errors -> errors(it.errorMessage)
                is GiroViewModel.ViewEvent.GiroCheckRequestSuccess -> loadCheckRequest(it.checkRequest)
                is GiroViewModel.ViewEvent.ParameterSuccess -> loadParameters(it.typeRequest)

            }
        })

        showDialogProgress(getString(R.string.message_dialog_load_data))
        giroViewModel.getParametersGiros()


    }

    private fun loadParameters(typeRequest: List<GiroTypeRequestsDTO>?) {
        mtypeRequest = typeRequest
        giroViewModel.giroCheckRequest()

    }

    private fun loadCheckRequest(checkRequest: ResponseGiroCheckRequestDTO) {

        initListCheckRequest(checkRequest.requests )
        hideDialogProgress()

    }


    private fun initListCheckRequest(listCheckRequests: List<GirorCheckRequestsDTO>?) {

        listCheckRequests?.forEach { giroTypeRequestsDTO ->
           val typeRequest = mtypeRequest?.filter { it.code ==  giroTypeRequestsDTO.requestType }
           giroTypeRequestsDTO.isPrinter =  typeRequest?.last()?.printer == "S"
        }


        val giroCheckRequestAdapter = GiroCheckRequestAdapter(listCheckRequests!!)

        val call = object : GiroCheckRequestAdapter.OnActionListener {
            override fun onClickItemPrint(girorCheckRequestsDTO: GirorCheckRequestsDTO) {

                giroViewModel.printCheckRequest(girorCheckRequestsDTO )

            }

            override fun onClickItemDetail(girorCheckRequestsDTO: GirorCheckRequestsDTO) {

                showDetailDalog(girorCheckRequestsDTO)

            }

        }
        giroCheckRequestAdapter.setListener(call)


        recyclerView.adapter = giroCheckRequestAdapter

    }

    private fun showDetailDalog(girorCheckRequestsDTO: GirorCheckRequestsDTO) {


        activity?.runOnUiThread {

            val inflater = this.layoutInflater

            val dialogView = inflater.inflate(R.layout.dialog_giro_check_result_detail, null)

            dialogView.findViewById<TextView>(R.id.lblId)
                .setText(girorCheckRequestsDTO.beneficiaryId)

            dialogView.findViewById<TextView>(R.id.lblReference)
                .setText(girorCheckRequestsDTO.reference)
            dialogView.findViewById<TextView>(R.id.lblTypeRequest)
                .setText(girorCheckRequestsDTO.requestName)
            dialogView.findViewById<TextView>(R.id.lblCatalogue)
                .setText(girorCheckRequestsDTO.messageTypeName)
            dialogView.findViewById<TextView>(R.id.lblStatus).setText(girorCheckRequestsDTO.status)
            dialogView.findViewById<TextView>(R.id.lblObservation)
                .setText(girorCheckRequestsDTO.observation)
            dialogView.findViewById<TextView>(R.id.lblThird)
                .setText(girorCheckRequestsDTO.beneficiaryName)

            val dialog = AlertDialog.Builder(context!!).apply {
                setTitle(RUtil.R_string(R.string.dialog_giro_check_request_title))
                setView(dialogView)
                setCancelable(false)
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, which ->

                    dialog.dismiss()

                }
            }.show()


        }


    }

    private fun errors(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun setupViewModel() {
        initListenersView()
    }

    private fun initListenersView() {

        btnUpdate.setOnClickListener {
            showDialogProgress(getString(R.string.message_dialog_load_data))
            giroViewModel.giroCheckRequest()
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

    }


}
