package co.com.pagatodo.core.views.giro


import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GiroMovementDTO
import co.com.pagatodo.core.data.dto.response.ResponseGiroMovementDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.GiroMovementAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_giro_movement.*
import kotlinx.android.synthetic.main.item_movement_summary.*
import java.text.SimpleDateFormat
import java.util.*


class GiroMovementFragment : BaseFragment() {

    private val calendar = Calendar.getInstance()
    private lateinit var giroViewModel: GiroViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_giro_movement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()
    }

    private fun setupViewModel() {

        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, androidx.lifecycle.Observer {

            when (it) {
                is GiroViewModel.ViewEvent.GiroConsultMovementSuccess -> loadMovement(it.movement)
                is GiroViewModel.ViewEvent.Errors -> showErros(it.errorMessage)

            }

        })


    }

    private fun showErros(errorMessage: String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun loadMovement(
        response: ResponseGiroMovementDTO? = null,
        isDefaultRow: Boolean = false
    ) {

        val listData =  if( response != null)  response.movements!! else listOf(GiroMovementDTO())

        tvEarnings.setText(
            getString(
                R.string.text_label_parameter_coin, formatValue(
                    response?.incomer?.toDouble()
                        ?: 0.0
                )
            )
        )
        tvExpenses.setText(
            getString(
                R.string.text_label_parameter_coin, formatValue(
                    response?.egress?.toDouble()
                        ?: 0.0
                )
            )
        )

        tvBeginningBalance.setText(
            getString(
                R.string.text_label_parameter_coin, formatValue(
                    response?.valueInit?.toDouble()
                        ?: 0.0
                )
            )
        )

        tvFinalBalance.setText(
            getString(
                R.string.text_label_parameter_coin, formatValue(
                    response?.valueFinish?.toDouble()
                        ?: 0.0
                )
            )
        )

        val giroMovementAdapter = GiroMovementAdapter( listData, isDefaultRow)
        rvMovementSummaryDetails.adapter = giroMovementAdapter

        hideDialogProgress()

        calculateHeightRc(listData)

    }

    private fun calculateHeightRc(listMovement: List<GiroMovementDTO>) {

        if (listMovement.size > 2) {
            val params = rvMovementSummaryDetails.getLayoutParams()

            if (DeviceUtil.isSalePoint())
                params.height = 60 * listMovement.size
            else
                params.height = 220                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  * listMovement.size

            rvMovementSummaryDetails.setLayoutParams(params)
        }

    }

    private fun setupFragmentView() {
        initListenersViews()
        initListDefault()
    }

    private fun initListDefault() {

        if (DeviceUtil.isSalePoint())
            loadMovement(isDefaultRow = true)

    }

    private fun initListenersViews() {
        initListenersClick()
    }

    private fun initListenersClick() {

        btnBack.setOnClickListener { activity?.onBackPressed() }

        txtMovementDateQuery.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                activity!!,
                R.style.DateDialogTheme,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    txtMovementDateQuery.text = SpannableStringBuilder(
                        convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time)
                    )

                    lblMovementTitle.text = txtMovementDateQuery.text.toString()

                    showDialogProgress(RUtil.R_string(R.string.message_dialog_load_data))
                    giroViewModel.giroConsultMovement(txtMovementDateQuery.text.toString())

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }

    }

    fun convertDateToStringFormat(format: DateUtil.StringFormat, date: Date): String {
        val simpleDateFormat = SimpleDateFormat(format.rawValue, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}
