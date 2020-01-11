package co.com.pagatodo.core.views.raffle.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.com.pagatodo.core.R
import co.com.pagatodo.core.views.adapters.RaffleRangeAdapter
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_range_raffle.*


class RangeRaffleFragment: BaseFragment(), RaffleRangeAdapter.OnActionListener {

    interface OnFragmentInteractionListener {
        fun getNumbers(numberInit: String, numberFinal: String)
        fun selectNumber(numberSelect: String)
    }

    var selectNumber = ""
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_range_raffle, container, false)
    }

    fun clearForm(){
        if(etNumberInitRaffle != null || etNumberFinalRaffle != null){
            etNumberInitRaffle.setText("")
            etNumberFinalRaffle.setText("")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    fun setOnFragmentListener(listener: OnFragmentInteractionListener) {
        this.listener = listener
    }

    fun setupView(){
        btConsultRange.setOnClickListener { searchRange() }

        etNumberFinalRaffle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                errorInputFinalRaffle.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etNumberInitRaffle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                errorInputInitRaffle.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }


    fun searchRange(){

        if(isValidForm()){
            setNUmbers()
            listener!!.getNumbers(etNumberInitRaffle.text.toString(), etNumberFinalRaffle.text.toString())
        }
    }

    fun setNUmbers(){
        if(etNumberInitRaffle.text.toString().length < 4){
            var text = etNumberInitRaffle.text.toString()
            for(i in 1..(4 - etNumberInitRaffle.text.toString().length)){
                text = "0$text"
            }
            etNumberInitRaffle.setText(text)
        }

        if(etNumberFinalRaffle.text.toString().length < 4){
            var text = etNumberFinalRaffle.text.toString()
            for(i in 1..(4 - etNumberFinalRaffle.text.toString().length)){
                text = "0$text"
            }
            etNumberFinalRaffle.setText(text)
        }
    }

    private fun isValidForm(): Boolean {

        var isValid = true


        if(etNumberInitRaffle.text.toString().isEmpty() ){
            errorInputInitRaffle.visibility = View.VISIBLE
            isValid = false
        }

        if(etNumberFinalRaffle.text.toString().isEmpty() ){
            errorInputFinalRaffle.visibility = View.VISIBLE
            isValid = false
        }

        if( etNumberInitRaffle.text.toString().isNotEmpty() && etNumberFinalRaffle.text.toString().isNotEmpty()){

            val numStart =etNumberInitRaffle.text.toString().toInt()
            val numEnd = etNumberFinalRaffle.text.toString().toInt()

            if(numStart>=numEnd){
                isValid = false
            }


            if(numEnd>9999){
                isValid = false
            }

            if(!isValid){
                showOkAlertDialog("",getString(R.string.range_invalid))
            }

        }

        return isValid
    }

    fun setEnableEditText(status: Boolean){
        etNumberInitRaffle.isEnabled = status
        etNumberFinalRaffle.isEnabled = status
    }

    fun updateRaffleList(data: List<String>){
        val adapter = RaffleRangeAdapter(data)
        adapter.setListener(this)
        rvRangeAvailable.adapter = adapter
    }

    override fun onClickMenu(currentRaffle: String?) {
        selectNumber = currentRaffle?:""
    }
}