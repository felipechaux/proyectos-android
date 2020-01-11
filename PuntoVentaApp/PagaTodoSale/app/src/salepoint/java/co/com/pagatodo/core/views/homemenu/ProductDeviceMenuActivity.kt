package co.com.pagatodo.core.views.homemenu

import co.com.pagatodo.core.views.deviceprinters.DevicePrintersActivity
import co.com.pagatodo.core.views.homemenu.deviceinformationmenu.DeviceMenuActivity

class ProductDeviceMenuActivity: DeviceMenuActivity() {
    override fun onClickMenu(type: MainMenuEnum?) {
        super.onClickMenu(type)
        when(type) {
            MainMenuEnum.DI_IMPRESORAS_DEL_DISPOSITIVO -> navigateToOptionWithExtra(DevicePrintersActivity::class.java)
        }
    }
}