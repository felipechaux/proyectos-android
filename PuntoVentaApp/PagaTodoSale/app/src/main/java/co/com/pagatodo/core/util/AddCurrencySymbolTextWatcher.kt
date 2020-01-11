package co.com.pagatodo.core.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.Exception

interface AddCurrencySymbolTextWatcherListener {
    fun onTextChange(number: String)
}

class AddCurrencySymbolTextWatcher(var editText: EditText?, var listener: AddCurrencySymbolTextWatcherListener): TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        try {
            editText?.removeTextChangedListener(this)
            var originalString = s.toString()
            originalString = originalString
                .replace(".", "")
                .replace("$", "")

            if (originalString.isNotEmpty()){
                val value = originalString.toDouble()
                val newString = "$${formatValue(value)}"
                editText?.setText(newString)
                editText?.setSelection(newString.length)
                listener.onTextChange(originalString)
            }
            editText?.addTextChangedListener(this)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}