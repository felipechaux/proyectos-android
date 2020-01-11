package co.com.pagatodo.core.views.snr

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.soat.SnrViewModel
import co.com.pagatodo.core.views.soat.VirtualSoatViewModel
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class SnrActivity : BaseActivity() {

    private lateinit var viewModel: SnrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snr)
        setupView()
    }

    private  fun setupView(){
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.home_menu_title_snr))
        initListeners()
    }

    private fun initListeners(){
        // Not implemented
    }
}
