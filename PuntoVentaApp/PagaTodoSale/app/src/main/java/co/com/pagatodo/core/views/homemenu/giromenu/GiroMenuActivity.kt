package co.com.pagatodo.core.views.homemenu.giromenu

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseGiroLoginDTO
import co.com.pagatodo.core.data.dto.response.ResponseGiroMovementDTO
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.views.adapters.SalePointMenuAdapter
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.giro.*
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import kotlinx.android.synthetic.main.activity_giro_menu.*
import kotlinx.android.synthetic.main.activity_giro_menu.lblBannerAdviser
import kotlinx.android.synthetic.main.activity_giro_menu.lblBannerAgency
import kotlinx.android.synthetic.main.activity_giro_menu.rvMenu
import java.util.*

class GiroMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewMenuModel: MenuViewModel
    private lateinit var giroViewModel: GiroViewModel
    private var mainMenuAdapter: SalePointMenuAdapter? = null

    var giroUsersFragment = GiroUsersFragment()
    var giroSendFragment = GiroSendFragment()
    var giroPaymentFragment = GiroPaymentFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giro_menu)

        setupModel()
        setupView()
        setupGirosViewModel()

    }

    private fun setupModel() {
        viewMenuModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewMenuModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })

    }

    private fun setupGirosViewModel() {

        giroViewModel = ViewModelProviders.of(this).get(GiroViewModel::class.java)

        giroViewModel.singleLiveEvent.observe(this, Observer {

            when (it) {
                is GiroViewModel.ViewEvent.GiroLoginSuccess -> loadSession(it.responseGiroLoginDTO)
                is GiroViewModel.ViewEvent.Errors -> errors(it.errorMessage)
                is GiroViewModel.ViewEvent.GiroConsultMovementSuccess -> loadMovement(it.movement)
            }

        })

        giroViewModel.loadTimeOutGiros()
        
        giroViewModel.girosLogin()

    }

    private fun errors(errorMessage: String) {
        hideDialogProgress()
        finishMenu(errorMessage)

    }

    private fun finishMenu(message: String) {

        showOkAlertDialog("",message){
            navigateToMenu(this@GiroMenuActivity)
        }

    }

    private fun loadSession(responseGiroLoginDTO: ResponseGiroLoginDTO?) {

        val agencyFull = "${responseGiroLoginDTO?.user?.agency
            ?: ""} | ${responseGiroLoginDTO?.user?.agencyName ?: ""} "
        lblBannerAdviser.setText(responseGiroLoginDTO?.user?.userName ?: "")
        lblBannerAgency.setText(agencyFull)

        if (DeviceUtil.isSalePoint()) {

            loadDefauldFragment()
        }

        btnClosebox.setOnClickListener {

            val date =
                convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, Date())
            showDialogProgress(getString(R.string.text_load_data))
            giroViewModel.giroConsultMovement(date)

        }

        viewMenuModel.loadGiroMenu()


    }

    private fun loadMovement(response: ResponseGiroMovementDTO) {

        calculateTotal(response)
        hideDialogProgress()

    }

    private fun calculateTotal(response: ResponseGiroMovementDTO) {


        showDialogCloseBox(
            response.valueInit ?: 0,
            response.incomer ?: 0,
            response.egress ?: 0,
            response.valueFinish ?: 0
        )


    }

    private fun showDialogCloseBox(valueInit: Int, entry: Int, egress: Int, balance: Int) {

            val inflater = this.layoutInflater

            val dialogView = inflater.inflate(R.layout.dialog_giro_close_box, null)

            dialogView.findViewById<TextView>(R.id.lblCloseBoxInit).setText(
                getString(
                    R.string.text_label_parameter_coin, formatValue(
                        valueInit.toDouble()
                    )
                )
            )
            dialogView.findViewById<TextView>(R.id.blCloseBoxIncome).setText(
                getString(
                    R.string.text_label_parameter_coin, formatValue(
                        entry.toDouble()
                    )
                )
            )
            dialogView.findViewById<TextView>(R.id.lblCloseBoxEgress).setText(
                getString(
                    R.string.text_label_parameter_coin, formatValue(
                        egress.toDouble()
                    )
                )
            )


            val dialog = AlertDialog.Builder(this).apply {
                setTitle(RUtil.R_string(R.string.giro_banner_close_box))
                setView(dialogView)
                setCancelable(false)
                setPositiveButton(getString(R.string.giro_banner_close_box)) { dialog, which ->
                    dialog.dismiss()
                    showDialogProgress(getString(R.string.text_load_data))
                    giroViewModel.closeBox(valueInit, balance, entry, egress)

                }
                setNegativeButton(getString(R.string.text_btn_cancel)) { dialog, which ->

                    dialog.dismiss()
                }
            }.show()

            dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.secondaryText
                )
            )

    }

    private fun updateMenuList(data: List<MainMenuModel>) {

        if (DeviceUtil.isSalePoint()) {

            mainMenuAdapter = SalePointMenuAdapter(data)
            mainMenuAdapter?.setListener(this)
            rvMenu.adapter = mainMenuAdapter
            mainMenuAdapter?.setSelectedItem(2)


        } else {

            val mainMenuAdapter = MenuAdapter(data)
            mainMenuAdapter.setListener(this)
            rvMenu.apply {
                setHasFixedSize(true)
                layoutManager =
                    GridLayoutManager(context, StorageUtil.numberOfColumnsForEnvironment())
            }
            rvMenu.adapter = mainMenuAdapter
        }


    }

    private fun loadDefauldFragment() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMainMenu, GiroUsersFragment()).commit()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.home_menu_title_giros))

        if (!DeviceUtil.isSalePoint()) {
            scrollContainer.post {
                scrollContainer.fullScroll(View.FOCUS_UP);
            }
        }


    }

    override fun onClickMenu(type: MainMenuEnum?) {

        when (type) {
            MainMenuEnum.P_GIRO_SEND -> {

                giroSendFragment = GiroSendFragment()
                navigateToOption(giroSendFragment, MainMenuEnum.P_GIRO_SEND)

            }
            MainMenuEnum.P_GIRO_PAYMENT -> {

                giroPaymentFragment = GiroPaymentFragment()
                navigateToOption(giroPaymentFragment, MainMenuEnum.P_GIRO_PAYMENT)
            }
            MainMenuEnum.P_GIRO_CONSULT -> navigateToOption(
                GiroConsultFragment(),
                MainMenuEnum.P_GIRO_CONSULT
            )
            MainMenuEnum.P_GIRO_USERS -> {

                giroUsersFragment = GiroUsersFragment()
                navigateToOption(giroUsersFragment, MainMenuEnum.P_GIRO_USERS)
            }

            MainMenuEnum.P_GIRO_REPRINT -> navigateToOption(
                GiroReprintFragment(),
                MainMenuEnum.P_GIRO_REPRINT
            )
            MainMenuEnum.P_GIRO_MOVEMENT -> navigateToOption(
                GiroMovementFragment(),
                MainMenuEnum.P_GIRO_MOVEMENT
            )
            MainMenuEnum.P_GIRO_AUTHORIZATIONS -> navigateToOption(
                GiroAuthorizationsFragment(),
                MainMenuEnum.P_GIRO_AUTHORIZATIONS
            )
            MainMenuEnum.P_GIRO_NOTES -> navigateToOption(
                GiroModifyNotesFragment(),
                MainMenuEnum.P_GIRO_NOTES
            )
            MainMenuEnum.P_GIRO_REQUEST -> navigateToOption(
                GiroCheckRequestFragment(),
                MainMenuEnum.P_GIRO_REQUEST
            )
            MainMenuEnum.P_GIRO_CLOSE_BOX -> navigateToOption(
                GiroCloseBoxFragment(),
                MainMenuEnum.P_GIRO_CLOSE_BOX
            )

        }

    }

    fun navigateToOption(view: Fragment, type: MainMenuEnum) {

        if (DeviceUtil.isSalePoint())
            navigateToOptionSalePoint(view)
        else
            navigateToOptionSalePointDevice(type)

    }

    fun navigateToOptionSalePoint(view: Fragment) {

        supportFragmentManager.beginTransaction().replace(R.id.containerMainMenu, view).commit()

    }

    fun navigateToOptionSalePointCreateUser(
        typeDocument: String,
        document: String,
        operation: TypeCreateUser
    ) {

        val sendData = Bundle()
        sendData.putString(getString(R.string.giro_ext_type_document), typeDocument)
        sendData.putString(getString(R.string.giro_ext_document), document)
        sendData.putInt(getString(R.string.giro_ext_type_operation), operation.raw)
        mainMenuAdapter?.setSelectedItem(2)

        supportFragmentManager.beginTransaction()
            .hide(giroSendFragment)
            .hide(giroPaymentFragment)
            .commitNow()

        giroUsersFragment = GiroUsersFragment()

        supportFragmentManager.beginTransaction()
            .hide(giroSendFragment)
            .hide(giroPaymentFragment)
            .commitNow()


        if (giroUsersFragment.isAdded) {

            supportFragmentManager.beginTransaction()
                .show(giroUsersFragment.apply { arguments = sendData })
                .commitNow()


        } else {

            supportFragmentManager.beginTransaction()
                .add(R.id.containerMainMenu, giroUsersFragment.apply { arguments = sendData })
                .commit()
        }

    }

    fun navigateToOptionSalePointPayGiro(reference: String) {

        val sendData = Bundle()
        sendData.putString(getString(R.string.giro_ext_reference), reference)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMainMenu, giroPaymentFragment.apply { arguments = sendData })
            .commit()

    }

    fun navigateToOptionSalePointDevice(type: MainMenuEnum) {

        val intent = Intent(this, GiroActivity::class.java)
        intent.putExtra(RUtil.R_string(R.string.EXT_MENU_GIRO_ITEM), type)
        startActivity(intent)


    }

    fun navigateToOptionSalePointReturn(typeOperation: TypeCreateUser) {

        when (typeOperation) {

            TypeCreateUser.USER_CREATE_DEFAULT -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_create_third_success_title),
                    RUtil.R_string(R.string.giro_create_third_success_message)
                ) {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.containerMainMenu,GiroUsersFragment())
                        .commitNow()

                }

            }
            TypeCreateUser.USER_UPDATE_DEFAULT -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_update_third_success_title),
                    RUtil.R_string(R.string.giro_update_third_success_message)
                ) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.containerMainMenu,GiroUsersFragment())
                        .commitNow()
                }

            }
            TypeCreateUser.USER_SEND_UPDATE -> {

                showOkAlertDialog(
                    RUtil.R_string(R.string.giro_update_third_success_title),
                    RUtil.R_string(R.string.giro_update_third_success_message)
                ) {
                    mainMenuAdapter?.setSelectedItem(0)
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
                    mainMenuAdapter?.setSelectedItem(0)
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
                    mainMenuAdapter?.setSelectedItem(0)
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

                    mainMenuAdapter?.setSelectedItem(1)
                    supportFragmentManager.beginTransaction()
                        .hide(giroUsersFragment)
                        .commitNow()

                    supportFragmentManager.beginTransaction()
                        .show(giroPaymentFragment.apply {queryGiro() })
                        .commitNow()

                }

            }

        }

    }

    override fun onBackPressed() {

        if(!giroUsersFragment.isHidden){
            navigateToBackSalePoint(giroUsersFragment.typeOperation)

        }else{

            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {

        if(!giroUsersFragment.isHidden){
            navigateToBackSalePoint(giroUsersFragment.typeOperation)

        }else{

            finish()
        }

        return  false
    }

    fun navigateToBackSalePoint(typeOperation: TypeCreateUser){

        when(typeOperation){
            TypeCreateUser.USER_SEND_UPDATE,
            TypeCreateUser.USER_SEND_CREATE_ORIGEN,
            TypeCreateUser.USER_SEND_CREATE_DESTINATION->{
                mainMenuAdapter?.setSelectedItem(0)
                supportFragmentManager.beginTransaction()
                    .hide(giroUsersFragment)
                    .commitNow()

                supportFragmentManager.beginTransaction()
                    .show(giroSendFragment)
                    .commitNow()
            }
            TypeCreateUser.USER_PAY_ENROLL->{
                mainMenuAdapter?.setSelectedItem(0)
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
