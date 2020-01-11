package co.com.pagatodo.core.views.bbva

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import kotlinx.android.synthetic.salepoint.fragment_bbva_reprint_transaction.*

class BbvaReprintTransactionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_bbva_reprint_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragmentView()
    }

    private fun setupFragmentView() {

        initListenersViews()

    }

    private fun initListenersViews() {

        initListenersOnClick()

    }

    private fun initListenersOnClick(){

        btnReprintBack.setOnClickListener{ activity?.kPressed() }
        btnReprint.setOnClickListener { reprintLastTransaction() }
        setExamplesValues()

    }

    private fun setExamplesValues() {

        lblReprintTransactionDate.text = "xxxxxxxxxxxxx"
        lblReprintTransactionType.text = "xxxxxxxxxxxxx"
        lblReprintTransactionValue.text = "xxxxxxxxxxxxx"
        lblReprintVoucherNumber.text = "xxxxxxxxxxxxx"
        lblReprintBox.text = "xxxxxxxxxxxxx"
        lblReprintUser.text = "xxxxxxxxxxxxx"
        lblReprintTerminal.text = "xxxxxxxxxxxxx"
        lblReprintTransactionId.text = "xxxxxxxxxxxxx"

    }

    private fun reprintLastTransaction() {
        val alertDialog = activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_reprint_last_transaction_modal_title))
                setMessage(RUtil.R_string(R.string.bbva_reprint_last_transaction_modal_message))
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(RUtil.R_string(R.string.text_btn_reprint)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }

        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.secondaryText
            )
        )
    }

}
