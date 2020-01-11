package co.com.pagatodo.core.views.resetpassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class ResetPasswordActivity : BaseActivity() {

    private lateinit var resetPasswordViewModel: ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.reset_password))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        resetPasswordViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel::class.java)
        initSubscriptions()
    }

    private fun initListenersViews() {

        txtCurrentPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorCurrentPassword.visibility = View.GONE
                labelErrorCurrentPasswordFormat.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        txtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorNewPassword.visibility = View.GONE
                labelErrorNewPasswordFormat.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        txtConfirmNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorConfirmNewPassword.visibility = View.GONE
                labelErrorNewPasswordNotWorking.visibility = View.GONE
                labelErrorConfirmNewPasswordFormat.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { changePassword() }

        imgShowCurrentPassword.setOnClickListener {
            showPasswordInEdittext(txtCurrentPassword, imgShowCurrentPassword)
        }
        imgShowNewPassword.setOnClickListener {
            showPasswordInEdittext(txtNewPassword, imgShowNewPassword)
        }
        imgShowNewConfirmPassword.setOnClickListener {
            showPasswordInEdittext(txtConfirmNewPassword, imgShowNewConfirmPassword)
        }
    }

    private fun initSubscriptions() {
        resetPasswordViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is ResetPasswordViewModel.ViewEvent.ResponseSuccess -> {
                    progressDialog.dismiss()
                    it.successMessage?.let { it1 -> showOkAlertDialog(R_string(R.string.title_success_change_password), R_string(R.string.message_success_change_password)) { navigateToMenu(this) } }
                }
                is ResetPasswordViewModel.ViewEvent.ResponseError -> {
                    progressDialog.dismiss()
                    it.errorMessage?.let { it1 -> showOkAlertDialog("", it1) }
                }
            }
        })
    }

    private fun changePassword() {
        if(isValidFields()) {
            showDialogProgress(getString(R.string.message_dialog_request))
            val currentPassword = txtCurrentPassword.text.toString()
            val newPassword = txtNewPassword.text.toString()
            resetPasswordViewModel.changePassword(currentPassword, newPassword)
        }
    }

    private fun isValidFields(): Boolean {
        hideLabelsError()
        var isValid = true
        if(txtCurrentPassword.text.isEmpty()) {
            labelErrorCurrentPassword.visibility = View.VISIBLE
            isValid = false
        } else if(txtCurrentPassword.text.isNotEmpty()) {
            if(txtCurrentPassword.text.length < 5) {
                labelErrorCurrentPasswordFormat.visibility = View.VISIBLE
                isValid = false
            }
        }

        if(txtNewPassword.text.isEmpty()) {
            labelErrorNewPassword.visibility = View.VISIBLE
            isValid = false
        } else if(txtNewPassword.text.isNotEmpty()) {
            if(txtNewPassword.text.length < 5) {
                labelErrorNewPasswordFormat.visibility = View.VISIBLE
                isValid = false
            }
        }

        if(txtConfirmNewPassword.text.isEmpty()) {
            labelErrorConfirmNewPassword.visibility = View.VISIBLE
            isValid = false
        } else if(txtConfirmNewPassword.text.isNotEmpty()) {
            if(txtConfirmNewPassword.text.length < 5) {
                labelErrorConfirmNewPasswordFormat.visibility = View.VISIBLE
                isValid = false
            }else if(txtNewPassword.text.isNotEmpty() && txtConfirmNewPassword.text.isNotEmpty()) {
                if(txtNewPassword.text.toString() != txtConfirmNewPassword.text.toString()) {
                    labelErrorNewPasswordNotWorking.visibility = View.VISIBLE
                    isValid = false
                }
            }
        }

        return isValid
    }

    private fun showPasswordInEdittext(edittext: EditText, view: ImageButton) {
        if(edittext.transformationMethod == null) {
            edittext.transformationMethod = PasswordTransformationMethod()
            view.setImageResource(R.drawable.ic_show_pass)
        } else {
            edittext.transformationMethod = null
            view.setImageResource(R.drawable.ic_hide_pass)
        }
    }

    private fun hideLabelsError(){
        labelErrorCurrentPassword.visibility = View.GONE
        labelErrorCurrentPasswordFormat.visibility = View.GONE
        labelErrorNewPassword.visibility = View.GONE
        labelErrorNewPasswordFormat.visibility = View.GONE
        labelErrorConfirmNewPassword.visibility = View.GONE
        labelErrorConfirmNewPasswordFormat.visibility = View.GONE
        labelErrorNewPasswordNotWorking.visibility = View.GONE
    }
}
