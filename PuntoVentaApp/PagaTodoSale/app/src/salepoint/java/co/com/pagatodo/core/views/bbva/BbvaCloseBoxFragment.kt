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

class BbvaCloseBoxFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_bbva_close_box, container, false)
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

        launchModal()

    }

    private fun launchModal() {
        val alertDialog = activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_close_box_modal_title))
                setMessage(RUtil.R_string(R.string.bbva_close_box_modal_message))
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(RUtil.R_string(R.string.text_btn_close_box)) { dialog, _ ->
                    closeBox()
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

    private fun closeBox() {

        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_close_box_modal_title))
                setMessage(RUtil.R_string(R.string.bbva_close_box_status_modal_message))
                setCancelable(false)
                setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }

    }

}
