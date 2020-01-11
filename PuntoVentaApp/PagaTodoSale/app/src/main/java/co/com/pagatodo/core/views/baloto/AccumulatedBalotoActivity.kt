package co.com.pagatodo.core.views.baloto

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.BalotoDrawDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DateUtil.Companion.convertMillisecondsToDate
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.activity_accumulated_baloto.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class AccumulatedBalotoActivity : BaseActivity() {

    lateinit var viewModel: BalotoViewModel

    private lateinit var draw: BalotoDrawDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accumulated_baloto)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.accumulated_baloto_title))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BalotoViewModel::class.java)
        initSubscriptions()
        showDialogProgress(getString(R.string.message_dialog_request))
        isForeground = false
        viewModel.getAccumulated()
    }

    private fun initSubscriptions() {
        viewModel.singleLiveEvent.observe(this, Observer { itl ->
            when(itl) {
                is BalotoViewModel.ViewEvent.GetDraws -> {
                    hideDialogProgress()
                    draw = itl.draws.last()
                    setInformationAccumulated()
                }
                is BalotoViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", itl.errorMessage.toString()) {finish()}
                }
            }
        })
    }

    private fun setInformationAccumulated() {
        val dateDraw = convertMillisecondsToDate(draw.closeTime.toString().toLong(), DateUtil.StringFormat.EEE_DD_MM_YY_FULL.rawValue).capitalize()
        lblDateDraw.text = getString(R.string.text_label_parameter_baloto_date_draw, dateDraw)
        lblValueDraw.text = getString(R.string.text_label_parameter_baloto_value,
            draw.jackpots?.first()?.amount?.toDouble()?.let { formatValue(it) }
        )

        lblDateDrawRevenge.text = getString(R.string.text_label_parameter_baloto_date_draw, dateDraw)
        lblValueDrawRevenge.text = getString(R.string.text_label_parameter_baloto_value,
            draw.jackpots?.last()?.amount?.toDouble()?.let { formatValue(it) }
        )
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.visibility = View.INVISIBLE
    }
}
