package co.com.pagatodo.core.views.homemenu.deviceinformationmenu

import android.os.Bundle
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.information.InformationDeviceActivity

class ProductDeviceMenuActivity : DeviceMenuActivity(){

    override fun onClickMenu(type: MainMenuEnum?) {
        when(type) {
            MainMenuEnum.DI_INFORMACION_DATAFONO -> navigateToOptionWithExtra(InformationDeviceActivity::class.java, Bundle().apply {
                putBoolean(RUtil.R_string(R.string.bundle_in_session), true)
            })
        }
    }
}