package co.com.pagatodo.core.views.paymillionaire

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_query_pay_millonaire.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class QueryPayMillonaireActivity : BaseActivity() {

    private lateinit var payMillonaireViewModel: PayMillionaireViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_pay_millonaire)

        setupView()
        setupViewModel()
    }

    private fun setupViewModel(){
        payMillonaireViewModel = ViewModelProviders.of(this).get(PayMillionaireViewModel::class.java)

        payMillonaireViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is PayMillionaireViewModel.ViewEvent.ShowMode -> {
                    tvAccumulatedModality3.text = "$ ${formatValue(it.modes.filter { it.numberOfDigits.equals("3") }.first().accumulated?.toDouble() ?: 0.0)}"
                    tvAccumulatedModality4.text = "$ ${formatValue(it.modes.filter { it.numberOfDigits.equals("4") }.first().accumulated?.toDouble() ?: 0.0)}"
                }
            }
        })

        payMillonaireViewModel.dispatchMode()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(R_string(R.string.pay_millionaire_title))
        btnNext.visibility = View.GONE

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}
