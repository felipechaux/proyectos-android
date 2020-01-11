package co.com.pagatodo.core.views.raffle.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.RaffleModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.fragment_pay_raffle.*

class PayRaffleFragment : Fragment() {

    interface OnFragmentInteractionListener {
        fun getCurrentRaffle(): RaffleModel?
        fun validateNumberRaffle(number: String)
        fun generateRandomNumber()
        fun showRange()
        fun setupViewModelNumber(number: String)
        fun showNextButton()
        fun showMessage(message: String)
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pay_raffle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btGenerateRandom.setOnClickListener { listener?.generateRandomNumber() }
        btFetchAvailable.setOnClickListener { fetchAvailable() }
        setupView()
    }

    fun setOnFragmentListener(listener: OnFragmentInteractionListener) {
        this.listener = listener
    }

    private fun setupView(){
        listener?.showNextButton()
        btGenerateRandom.setOnClickListener { listener?.generateRandomNumber() }
        imgRandomRaffle.setOnClickListener { listener?.generateRandomNumber() }
        btFetchAvailable.setOnClickListener { fetchAvailable() }

        if (listener?.getCurrentRaffle()?.countNumberFigures == 3){
            imgRandomRaffle.setImageDrawable(activity?.getDrawable(R.drawable.ic_random_3))
        }else{
            imgRandomRaffle.setImageDrawable(activity?.getDrawable(R.drawable.ic_random_4))
        }

        if (DeviceUtil.isSalePoint()){
            setEnableEditText(false)
            btFetchAvailable.isEnabled = true
        }

        etNumberRaffle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                messageEmptyError.visibility = View.GONE
                raffle_number_for_sale.text = etNumberRaffle.text.toString()
                listener?.getCurrentRaffle()?.raffleNumber = etNumberRaffle.text.toString()
                listener?.setupViewModelNumber(etNumberRaffle.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    private fun showSummary(){
        val time =  DateUtil.convertMilitaryHourToNormal(DateUtil.StringFormat.HHMMA,listener?.getCurrentRaffle()?.drawTime ?: "")
        var dateRaffle = DateUtil.convertStringToDate(DateUtil.StringFormat.EEE_DD_MM_YY_SPLIT, listener?.getCurrentRaffle()?.dateDraw ?: "").toLowerCase().capitalize()
        if (DeviceUtil.isSalePoint()){
            dateRaffle = DateUtil.convertStringToDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, listener?.getCurrentRaffle()?.dateDraw ?: "")
        }

        raffle_name_number_for_sale.text = listener?.getCurrentRaffle()?.raffleName
        raffle_number_for_sale.text = listener?.getCurrentRaffle()?.raffleNumber
        raffle_prize_number_for_sale.text = "               ${listener?.getCurrentRaffle()?.raffleDescription}"
        raffle_lottery_number_for_sale.text = listener?.getCurrentRaffle()?.lotteryName
        raffle_value_number_for_sale.text = "$ ${formatValue(listener?.getCurrentRaffle()?.price?.toDouble() ?: 0.0)}"
        raffle_date_draw_number_for_sale.text = "${dateRaffle} ${if (!DeviceUtil.isSalePoint()) time else ""}"
        container_raffle_number_summary.visibility = View.VISIBLE
    }

    fun hideSummary(){

        raffle_name_number_for_sale.text = ""
        raffle_number_for_sale.text = ""
        raffle_prize_number_for_sale.text = ""
        raffle_lottery_number_for_sale.text = ""
        raffle_value_number_for_sale.text = ""
        raffle_date_draw_number_for_sale.text = ""
        container_raffle_number_summary.visibility = View.GONE
    }

    fun fetchAvailable(){

        listener?.getCurrentRaffle().let {
            if(validateSelectList()){
                if (etNumberRaffle.text.toString().isEmpty()){
                    listener?.showRange()
                }else{
                    listener?.validateNumberRaffle(listener?.getCurrentRaffle()?.raffleNumber ?: "0")
                    this.context?.let { it1 -> DeviceUtil.hideKeyboard(etNumberRaffle, it1) }
                }
            }else{
                listener?.showMessage(RUtil.R_string(R.string.text_raffles_validation_id))
            }
        }?: run {
            listener?.validateNumberRaffle(etNumberRaffle.text.toString())
        }
    }

    fun validateSelectList() :Boolean{
        var isSelect = true
        if (listener?.getCurrentRaffle() == null){
            isSelect = false
        }
        return isSelect
    }

    fun setEnableEditText(status: Boolean){
        etNumberRaffle.isEnabled = status
        btFetchAvailable.isEnabled = status
    }

    fun generateRandomNumber(number: String){
        if (listener?.getCurrentRaffle() != null) {
            listener?.getCurrentRaffle()?.raffleNumber = number
            etNumberRaffle.setText(number)
        }else{
            listener?.showMessage(RUtil.R_string(R.string.text_raffles_validation_id))
        }
    }

    fun numberAvailable(){
        showSummary()
    }

    fun validateData(): Boolean {
        if ((listener?.getCurrentRaffle()?.raffleNumber?.isNotEmpty() ?: false) && container_raffle_number_summary.visibility == View.VISIBLE){
            return true
        }
        return false
    }

    fun setNumberRaffleValue(values:String){
        etNumberRaffle.setText(values)
    }
}
