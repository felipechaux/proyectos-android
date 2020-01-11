package co.com.pagatodo.core.views.award

import android.os.Bundle
import co.com.pagatodo.core.R
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_award.*

class AwardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_award)
        setupView()
    }

    private fun setupView() {
        title = "CONSULTAR PREMIO"
        butCancel.setOnClickListener {
            finish()
        }
    }
}
