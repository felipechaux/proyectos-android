package co.com.pagatodo.core.views.base

import androidx.fragment.app.Fragment
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.AuthMode
import co.com.pagatodo.core.views.giro.GiroActivity
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity

open class BaseFragment : Fragment() {

    fun hideDialogProgress() {
        if (validateMenuActivity())
            baseMenuActivity().hideDialogProgress()
        else
            baseActivity().hideDialogProgress()
    }

    fun showDialogProgress(message: String) {
        if (validateMenuActivity())
            baseMenuActivity().showDialogProgress(message)
        else
            baseActivity().showDialogProgress(message)
    }

    fun showOkAlertDialog(title: String, message: String) {
        if (validateMenuActivity())
            baseMenuActivity().showOkAlertDialog(title, message)
        else
            baseActivity().showOkAlertDialog(title, message)
    }

    fun showOkAlertDialog(title: String, message: String, closure: () -> Unit?) {
        if (validateMenuActivity())
            baseMenuActivity().showOkAlertDialog(title, message, closure)
        else
            baseActivity().showOkAlertDialog(title, message, closure)

    }

    fun showOkAlertDialog(title: String, message: String, closure: () -> Unit?,closure2: () -> Unit?) {
        if (validateMenuActivity())
            baseMenuActivity().showAlertDialog(title, message, closure,closure2)
        else
            baseActivity().showAlertDialog(title, message, closure,closure2)
    }

    fun showAuthUserBiometria(closure: (data: String,dataImg:String) -> Unit?) {
        if (validateMenuActivity())
            baseMenuActivity().showAuthUserBiometria(closure)
        else
            baseActivity().showAuthUserBiometria(closure)
    }

    fun validateAuthSeller(key:String,closure: (type: AuthMode, data: String,dataImg:String) -> Unit?) {
        if (validateMenuActivity())
            baseMenuActivity().validateAuthSeller(key,closure)
        else
            baseActivity().validateAuthSeller(key,closure)

    }

    fun onBackPressed(fragment: BaseFragment){
        reloadFragment(fragment)
    }

    private fun reloadFragment (fragment: BaseFragment){
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.containerMainMenu,fragment).commit()
    }

    fun onBackPressed() {

        activity?.onBackPressed()

    }

    fun hideKeyboard() {
        if (validateMenuActivity())
            baseMenuActivity().hideKeyboard()
        else
            baseActivity().hideKeyboard()
    }

    private fun validateMenuActivity(): Boolean {
        return activity is MenuBaseActivity
    }

    fun baseMenuActivity(): MenuBaseActivity {

        return activity as MenuBaseActivity
    }

    fun baseActivity(): BaseActivity {

        return activity as BaseActivity
    }

    fun giroActivity(): GiroActivity {

        return activity as GiroActivity
    }

    fun giroMenuActivity(): GiroMenuActivity {

        return activity as GiroMenuActivity
    }

}