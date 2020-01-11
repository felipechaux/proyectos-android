package co.com.pagatodo.core.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class JumpToNextEditTextWatcher(numberCharactes: Int, editText: EditText): TextWatcher {
    var editText = editText
    val numberCharactes = numberCharactes

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().length == numberCharactes){
            editText.requestFocus()
            editText.setSelection(editText.text.length)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}