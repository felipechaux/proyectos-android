package co.com.pagatodo.core.views.components.dialogs

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string

typealias closureConfirmDialog = () -> Unit
typealias closureConfirmDialogWithParam = (dialog: View) -> Unit

class PaymentConfirmationDialog {

    private var resourceId: Int = 0
    private var context: Context? = null
    lateinit var builder: AlertDialog.Builder

    fun show(context: Context?, layoutResource: Int,
             title: String,
             message: String? = null,
             closureAfterLoad: closureConfirmDialogWithParam? = null,
             positiveClosure: closureConfirmDialog? = null,
             negativeClosure: closureConfirmDialog? = null,
             textPositiveButton: String = R_string(R.string.text_btn_pay)
    ) {

        this.context = context
        this.resourceId = resourceId
        builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)

        layoutResource.let {
            val inflater = LayoutInflater.from(context)
            val newFileView = inflater.inflate(layoutResource, null)
            builder.setView(newFileView)
            closureAfterLoad?.invoke(newFileView)
        }

        builder.setPositiveButton(textPositiveButton) { dialog, _ ->
            positiveClosure?.invoke()
            dialog.dismiss()
        }
        builder.setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, _ ->
            negativeClosure?.invoke()
            dialog.dismiss()
        }

        build()
    }

    private fun build(): AlertDialog {
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        context?.let {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(it, R.color.colorGrayDark1))
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
        }
        return dialog
    }
}