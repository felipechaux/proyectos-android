package co.com.pagatodo.core.views.giro

import android.os.Bundle
import androidx.fragment.app.Fragment
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.TypeCreateUser
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum


class GiroActivity : BaseActivity() {

    var giroUsersFragment = GiroUsersFragment()
    var giroSendFragment = GiroSendFragment()
    var giroPaymentFragment = GiroPaymentFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giro)
        setupView()
        setupFragmentGiro()
    }


    private fun setupView() {
        setupBaseView()
    }

    private fun setupFragmentGiro() {

        intent.extras.let {

            when (it?.get(RUtil.R_string(R.string.EXT_MENU_GIRO_ITEM)) as MainMenuEnum) {
                MainMenuEnum.P_GIRO_SEND -> {
                    giroSendFragment = GiroSendFragment()
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                    setupFragment(giroSendFragment)
                }
                MainMenuEnum.P_GIRO_PAYMENT -> {
                    giroPaymentFragment = GiroPaymentFragment()
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_payment))
                    setupFragment(giroPaymentFragment)
                }
                MainMenuEnum.P_GIRO_CONSULT -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_payment))
                    setupFragment(GiroConsultFragment())
                }
                MainMenuEnum.P_GIRO_USERS -> {
                    giroUsersFragment = GiroUsersFragment()
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_users))
                    setupFragment(giroUsersFragment)
                }
                MainMenuEnum.P_GIRO_REPRINT -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_reprint))
                    setupFragment(GiroReprintFragment())
                }
                MainMenuEnum.P_GIRO_MOVEMENT -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_movement))
                    setupFragment(GiroMovementFragment())
                }
                MainMenuEnum.P_GIRO_AUTHORIZATIONS -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_authorizations))
                    setupFragment(GiroAuthorizationsFragment())
                }
                MainMenuEnum.P_GIRO_NOTES -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_modify_notes))
                    setupFragment(GiroModifyNotesFragment())
                }
                MainMenuEnum.P_GIRO_REQUEST -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_request))
                    setupFragment(GiroCheckRequestFragment())
                }

                MainMenuEnum.P_GIRO_CLOSE_BOX -> {
                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_close_box))
                    setupFragment(GiroCloseBoxFragment())
                }
            }

        }

    }

    private fun setupFragment(view: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.giroContainer, view).commit()
    }

    private fun setupToolBar(item: String) {
        updateTitle(item)
    }

    fun navigateToOptionCreateUser(
        typeDocument: String,
        document: String,
        operation: TypeCreateUser) {

        setupToolBar(RUtil.R_string(R.string.item_menu_giro_users))
        val sendData = Bundle()
        sendData.putString(getString(R.string.giro_ext_type_document), typeDocument)
        sendData.putString(getString(R.string.giro_ext_document), document)
        sendData.putInt(getString(R.string.giro_ext_type_operation), operation.raw)

        giroUsersFragment = GiroUsersFragment()


        supportFragmentManager.beginTransaction()
            .hide(giroSendFragment)
            .hide(giroPaymentFragment)
            .commitNow()


        if (giroUsersFragment.isAdded) {

            setupToolBar(RUtil.R_string(R.string.item_menu_giro_users))
            supportFragmentManager.beginTransaction()
                .show(giroUsersFragment.apply { arguments = sendData })
                .commitNow()


        } else {

            supportFragmentManager.beginTransaction()
                .add(R.id.giroContainer, giroUsersFragment.apply { arguments = sendData })
                .commit()
        }


    }

    fun navigateToOptionReturn(typeOperation: TypeCreateUser) {

        when (typeOperation) {

            TypeCreateUser.USER_CREATE_DEFAULT -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_create_third_success_title),
                    RUtil.R_string(R.string.giro_create_third_success_message)
                ) {
                    onBackPressed()
                }

            }
            TypeCreateUser.USER_UPDATE_DEFAULT -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_update_third_success_title),
                    RUtil.R_string(R.string.giro_update_third_success_message)
                ) {
                    onBackPressed()
                }

            }
            TypeCreateUser.USER_SEND_UPDATE -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_update_third_success_title),
                    RUtil.R_string(R.string.giro_update_third_success_message)
                ) {

                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                    supportFragmentManager.beginTransaction()
                        .hide(giroUsersFragment)
                        .commitNow()

                    supportFragmentManager.beginTransaction()
                        .show(giroSendFragment)
                        .commitNow()

                }

            }
            TypeCreateUser.USER_SEND_CREATE_ORIGEN -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_create_third_success_title),
                    RUtil.R_string(R.string.giro_create_third_success_message)
                ) {

                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                    supportFragmentManager.beginTransaction()
                        .hide(giroUsersFragment)
                        .commitNow()

                    supportFragmentManager.beginTransaction()
                        .show(giroSendFragment.apply { searchUserOrigin() })
                        .commitNow()

                }

            }
            TypeCreateUser.USER_SEND_CREATE_DESTINATION -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_create_third_success_title),
                    RUtil.R_string(R.string.giro_create_third_success_message)
                ) {

                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                    supportFragmentManager.beginTransaction()
                        .hide(giroUsersFragment)
                        .commitNow()

                    supportFragmentManager.beginTransaction()
                        .show(giroSendFragment.apply { searchUserDestination() })
                        .commitNow()

                }

            }
            TypeCreateUser.USER_PAY_ENROLL -> {

                showOkAlertDialog(
                    "",
                    RUtil.R_string(R.string.giro_enroler_third_success_message)
                ) {

                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                    supportFragmentManager.beginTransaction()
                        .hide(giroUsersFragment)
                        .commitNow()

                    supportFragmentManager.beginTransaction()
                        .show(giroPaymentFragment.apply {queryGiro() })
                        .commitNow()

                }

            }
            TypeCreateUser.USER_SEND_ENROLL-> {

                showOkAlertDialog(
                    "",
                    RUtil.R_string(R.string.giro_enroler_third_success_message)
                ) {

                    setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                    supportFragmentManager.beginTransaction()
                        .hide(giroUsersFragment)
                        .commitNow()

                    supportFragmentManager.beginTransaction()
                        .show(giroSendFragment.apply { searchUserOrigin()})
                        .commitNow()

                }

            }

        }

    }

    fun navigateToOptionPayGiro(reference: String) {

        setupToolBar(RUtil.R_string(R.string.item_menu_giro_payment))
        val sendData = Bundle()
        sendData.putString(getString(R.string.giro_ext_reference), reference)

        supportFragmentManager.beginTransaction()
            .replace(R.id.giroContainer, giroPaymentFragment.apply { arguments = sendData })
            .commit()


    }

    override fun onBackPressed() {

        if(!giroUsersFragment.isHidden){
            navigateToBack(giroUsersFragment.typeOperation)

        }else{

            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {

        if(!giroUsersFragment.isHidden){
            navigateToBack(giroUsersFragment.typeOperation)

        }else{

            finish()
        }

        return  false
    }

    fun navigateToBack(typeOperation: TypeCreateUser){

        when(typeOperation){
            TypeCreateUser.USER_SEND_UPDATE,
            TypeCreateUser.USER_SEND_CREATE_ORIGEN,
            TypeCreateUser.USER_SEND_ENROLL,
            TypeCreateUser.USER_SEND_CREATE_DESTINATION->{

                setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                supportFragmentManager.beginTransaction()
                    .hide(giroUsersFragment)
                    .commitNow()

                supportFragmentManager.beginTransaction()
                    .show(giroSendFragment)
                    .commitNow()
            }
            TypeCreateUser.USER_PAY_ENROLL->{

                setupToolBar(RUtil.R_string(R.string.item_menu_giro_send))
                supportFragmentManager.beginTransaction()
                    .hide(giroUsersFragment)
                    .commitNow()

                supportFragmentManager.beginTransaction()
                    .show(giroPaymentFragment)
                    .commitNow()
            }
            else -> {
                finish()
            }

        }

    }



}
