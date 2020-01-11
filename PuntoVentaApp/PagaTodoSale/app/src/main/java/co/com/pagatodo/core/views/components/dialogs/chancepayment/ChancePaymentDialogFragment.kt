package co.com.pagatodo.core.views.components.dialogs.chancepayment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.fragment_chance_payment_dialog.*
import kotlinx.android.synthetic.main.fragment_chance_payment_dialog.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ChancePaymentDialogFragment : DialogFragment() {

    private var mainView: View? = null
    private var listener: OnChancePaymentDialogListener? = null
    private var valueWithoutIva = 0
    private var suggestedValue: Double = 0.0
    private var suggestedValueString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ModalDialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_chance_payment_dialog, container, false)
        dialog.setTitle(RUtil.R_string(R.string.text_chance_ticket))
        setupView(mainView)
        return mainView
    }

    fun setOnChancePaymentDialogListener(listener: OnChancePaymentDialogListener) {
        this.listener = listener
    }

    private fun setupView(view: View?) {
        isCancelable = false
        mainView?.btnCancel?.setOnClickListener {
            dialog.dismiss()
        }

        mainView?.btnPay?.setOnClickListener { payChance() }
        initListenerTextChanged( mainView?.etSuggested!!)
        arguments?.let { arguments ->
            val raffleDate = arguments.getString(R_string(R.string.bundle_value_raffle_date))
            val lotteriesNumber =
                arguments.getString(R_string(R.string.bundle_value_lotteries_number))
            val numbers = arguments.getString(R_string(R.string.bundle_value_numbers))
            val stub = arguments.getString(R_string(R.string.bundle_value_stubs))
            val minValue = arguments.getString(R_string(R.string.bundle_value_min_amount))

            valueWithoutIva = arguments.getInt(R_string(R.string.bundle_value_without_Iva))
            val totalIva = (valueWithoutIva * CurrencyUtils.getIvaPercentage()).toInt()
            val totalPaid = valueWithoutIva + totalIva
            mainView?.tvRaffleDate?.text = raffleDate
            mainView?.tvLotteriesCount?.text = lotteriesNumber

            mainView?.tvNumbers?.text = numbers!!.replace("-", " - ")
            mainView?.tvStub?.text = stub
            mainView?.tvBet?.text = getString(
                R.string.text_label_parameter_coin,
                formatValue("$valueWithoutIva".toDouble())
            )
            mainView?.tvIva?.text =
                getString(R.string.text_label_parameter_coin, formatValue("$totalIva".toDouble()))
            mainView?.tvPaid?.text =
                getString(R.string.text_label_parameter_coin, formatValue("$totalPaid".toDouble()))

            suggestedValueString = "${CurrencyUtils.calculateSuggestedValue(totalPaid.toDouble())}"

            if (suggestedValueString.toInt() < minValue!!.toInt()) {
                suggestedValueString = minValue.toString()
            }


            suggestedValue = suggestedValueString.toDouble()
            mainView?.etSuggested?.text = SpannableStringBuilder("$${formatValue(suggestedValue)}")
            mainView?.etSuggested?.setSelection(mainView?.etSuggested?.text?.length ?: 0)


        }

        mainView?.etSuggested?.addTextChangedListener(
            AddCurrencySymbolTextWatcher(
                mainView?.etSuggested,
                object : AddCurrencySymbolTextWatcherListener {
                    override fun onTextChange(number: String) {
// not implemented
                    }
                })
        )
    }

    private fun initListenerTextChanged(ed: EditText) {

        var isTextChanged = true
        ed.addTextChangedListener(object : TextWatcher {

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

                if (isTextChanged) {

                    isTextChanged = false

                    if (ed.text.toString().isNotEmpty()) {

                        try {
                            var originalString = ed.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()

                            ed.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    co.com.pagatodo.core.util.formatValue(value)
                                )
                            )
                            ed.setSelection(ed.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    }


                    isTextChanged = true

                }

            }
        })
    }

    private fun formatValue(value: Double): String {
        val decimalFormat = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale.ITALIAN))
        return decimalFormat.format(value)
    }

    private fun payChance() {

        if (etSuggested.text.isNotEmpty()) {
            val suggestedValue = getIntOrNull(etSuggested.text.toString())
            if (suggestedValue < valueWithoutIva) {
                showDialogMessage(
                    R_string(R.string.message_error_title_chance_suggest),
                    R_string(R.string.message_error_chance_suggest)
                )
            } else {
                if (isItAFifty(suggestedValue)) {
                    listener?.onPayChance(valueWithoutIva, suggestedValue)
                } else {
                    showDialogMessage(
                        R_string(R.string.message_error_title_chance_suggest),
                        R_string(R.string.message_error_multiple_100)
                    )
                }
            }
        } else {
            showDialogMessage(
                R_string(R.string.message_error_title_chance_suggest),
                R_string(R.string.message_error_chance_suggest)
            )
        }
    }

    private fun showDialogMessage(title: String, message: String) {
        val alertDialog = activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(title)
                setMessage(message)
                setCancelable(false)
                setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                    dialog.dismiss()
                }
            }
        }

        alertDialog?.show()
    }

    private fun isItAFifty(value: Int): Boolean {
        var response = false
        if (value % 50 == 0) {
            response = true
        }
        return response
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnChancePaymentDialogListener {
        // TODO: Update argument type and name
        fun onPayChance(value: Int, suggestedValue: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChancePaymentDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle?) =
            ChancePaymentDialogFragment().apply {
                arguments = bundle
            }
    }
}
