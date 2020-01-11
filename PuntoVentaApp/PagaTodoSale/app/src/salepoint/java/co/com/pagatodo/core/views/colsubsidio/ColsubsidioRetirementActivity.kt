package co.com.pagatodo.core.views.colsubsidio

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.salepoint.activity_colsubsidio_retirement.*
import kotlinx.android.synthetic.salepoint.dialog_colsubsidio_retirement.view.*
import tw.com.prolific.driver.pl2303.PL2303Driver
import android.widget.Toast
import android.hardware.usb.UsbManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.datacenter.clienteredebanlibrary.core.UtilMsg
import com.datacenter.clienteredebanlibrary.colsubsidio.dto.RespuestaDTO
import com.datacenter.clienteredebanlibrary.core.IEventResponse
import com.datacenter.clienteredebanlibrary.colsubsidio.TestPago
import com.datacenter.clienteredebanlibrary.colsubsidio.dto.RetiroDTO
import kotlinx.android.synthetic.salepoint.dialog_colsubsidio_retirement.*
import kotlinx.android.synthetic.salepoint.layout_buttons_back_next.*
import kotlin.concurrent.thread


class ColsubsidioRetirementActivity: BaseActivity() {

    private var SHOW_DEBUG = true
    private var ACTION_USB_PERMISSION = "co.com.pagatodo.core.views.colsubsidio.USB_PERMISSION"
    private var TAG = "LogTest"
    private var mSerial: PL2303Driver? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colsubsidio_retirement)
        setupView()
        setupViewModel()

        // implementacion componente redeban
        // get service
        mSerial = PL2303Driver(
            getSystemService(Context.USB_SERVICE) as UsbManager,
            this, ACTION_USB_PERMISSION
        )
        // check USB host function.
        if (!mSerial!!.PL2303USBFeatureSupported()) {
            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT).show()
            Log.d("log->", "No Support USB host API")
            mSerial = null
        }


    }

    override fun onStop() {
        Log.d("", "Enter onStop")
        super.onStop()
        Log.d("", "Leave onStop")
    }

    override fun onDestroy() {
        Log.d("", "Enter onDestroy")

        if (mSerial != null) {
            mSerial!!.end()
            mSerial = null
        }
        super.onDestroy()
        Log.d("", "Leave onDestroy")
    }

    public override fun onResume() {
        super.onResume()
        val action = intent.action
        //Log.d("resumen", "onResume:" + action!!)
        //if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
        if (!mSerial!!.isConnected()) {

            if (!mSerial!!.enumerate()) {

                //Toast.makeText(this, "No haz conectado el datafono", //Toast.LENGTH_SHORT).show();
                return
            } else {
                Log.d("resumenenumarate", "onResume:enumerate succeeded!")
            }
        }//if isConnected
        Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {

    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.colsubsidio_ret_title))
        initListenersViews()
    }

    private fun initListenersViews() {
        initListenersTextWatcher()
        initListenersOnClick()
    }

    private fun initListenersTextWatcher() {

        txtValueRetirement.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                labelErrorValueRetirement.visibility = View.GONE
                labelErrorColsupsidioValorNoCaseNotWorking.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        txtValueRetirementConfirm.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                labelErrorColsupsidioValorNoCase.visibility = View.GONE
                labelErrorColsupsidioValorNoCaseNotWorking.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun initListenersOnClick() {
        btnNext.setOnClickListener { sendRetirementColsubsidio() }
        btnBack.setOnClickListener { finish() }
    }

    private fun sendRetirementColsubsidio() {

        if(validateFields())
            showConfirmationDialog()
    }

    private fun showConfirmationDialog() {

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_colsubsidio_retirement, null)
        dialogView.findViewById<TextView>(R.id.textRetiroDocumentNumber)
            .text=txtDocumentNumber.text.toString()

        dialogView.findViewById<TextView>(R.id.textRetiroRetirementValue)
            .text=txtValueRetirement.text.toString()


        val dialog = AlertDialog.Builder(this!!).apply {
            setTitle(RUtil.R_string(R.string.title_ticket_pay_colsubsidio_retirement))
            setView(dialogView)
            setCancelable(false)
            setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.text_btn_retirement)) { dialog, which ->

                showRedebanDialog()

                thread {
                    sendRetirementRedeban()
                    println("findialog")
                }

            }
        }.show()
        dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.secondaryText
            )
        )
    }

    private fun sendRetirementRedeban(){

        val testPago = TestPago(this, mSerial)

        val pagoDTO =RetiroDTO()
        pagoDTO.numeroCaja = "111111"
        pagoDTO.valorTotalCompra = txtValueRetirement.text.toString()
        pagoDTO.valorIva = "0"
        pagoDTO.valorBaseDevolicionIva = "0"
        pagoDTO.valorImpuestoConsumo = "0"
        pagoDTO.numeroTransaccion = "1234"
        pagoDTO.valorDescuento = "0"

        testPago.run(pagoDTO, object : IEventResponse<RespuestaDTO>() {
            override fun initProcess() {
                UtilMsg.print("Inicio el proceso con datafono")
            }

            override fun callBack(dto: RespuestaDTO) {
                UtilMsg.print("Termino el proceso con datafono")
                println(dto)

            }
        })
    }

    private fun showRedebanDialog() {

            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_colsubsidio_retirement_redeban, null)
            val dialog = AlertDialog.Builder(this!!).apply {
                setTitle(RUtil.R_string(R.string.title_ticket_colsubsidio_retirement_redeban))
                setView(dialogView)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }

            }.show()

    }

    private fun validateFields(): Boolean{
        var isValid = true

        if (txtDocumentNumber.text.toString().isEmpty()) {
            isValid = false
            labelErrorDocumentNumber.visibility = View.VISIBLE
        } else {
            labelErrorDocumentNumber.visibility = View.GONE
        }
        if (txtValueRetirement.text.toString().isEmpty()) {
            isValid = false
            labelErrorValueRetirement.visibility = View.VISIBLE
        } else {
            labelErrorValueRetirement.visibility = View.GONE
        }
        if(txtValueRetirementConfirm.text.isEmpty()) {
            labelErrorColsupsidioValorNoCase.visibility = View.VISIBLE
            isValid = false
        } else {
            if (txtValueRetirement.text.toString() != txtValueRetirementConfirm.text.toString()) {
                labelErrorColsupsidioValorNoCaseNotWorking.visibility = View.VISIBLE
                isValid = false
            }
        }



        return isValid
    }
}
