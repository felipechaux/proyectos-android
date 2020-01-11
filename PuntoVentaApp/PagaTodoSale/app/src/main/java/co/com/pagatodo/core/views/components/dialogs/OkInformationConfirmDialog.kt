package co.com.pagatodo.core.views.components.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil

typealias closureInfoConfirmDialog = () -> Unit
typealias closureInfoConfirmDialogWithParam = (dialog: View) -> Unit

class OkInformationConfirmDialog {

    private var resourceId: Int = 0
    private var context: Context? = null
    lateinit var builder: AlertDialog.Builder

    fun show(context: Context?, layoutResource: Int,
             title: String, message: String? = null,
             closureAfterLoad: closureInfoConfirmDialogWithParam? = null,
             positiveClosure: closureInfoConfirmDialog? = null
    ) {

        this.context = context
        this.resourceId = resourceId
        builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        layoutResource.let {
            val inflater = LayoutInflater.from(context)
            val newFileView = inflater.inflate(layoutResource, null)
            builder.setView(newFileView)
            closureAfterLoad?.invoke(newFileView)
        }
        builder.setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
            positiveClosure?.invoke()
            dialog.dismiss()
        }
        build()
    }

    private fun build(): AlertDialog {
        val dialog = builder.create()
        dialog.show()
        context?.let {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
        return dialog
    }
}