package co.com.pagatodo.core.views.homemenu

import co.com.pagatodo.core.views.homemenu.bbvamenu.BbvaMenuActivity
import co.com.pagatodo.core.views.homemenu.colpensionesbepsmenu.ColpensionesBepsMenuActivity
import co.com.pagatodo.core.views.homemenu.colsubsidiomenu.ColsubsidioMenuActivity
import co.com.pagatodo.core.views.homemenu.elderlymenu.ElderlyMenuActivity
import co.com.pagatodo.core.views.paybills.PayBillActivity
import co.com.pagatodo.core.views.homemenu.payumenu.PayuMenuActivity
import co.com.pagatodo.core.views.homemenu.registraduriamenu.RegistraduriaMenuActivity
import co.com.pagatodo.core.views.homemenu.sitpmenu.SitpMenuActivity
import co.com.pagatodo.core.views.snr.SnrActivity

class ProductsHomeMenuActivity: HomeMenuActivity() {

    override fun onClickMenu(type: MainMenuEnum?) {
        super.onClickMenu(type)
        when(type) {
            MainMenuEnum.P_BEPS_COLPENSIONES -> navigateToOption(ColpensionesBepsMenuActivity::class.java)
            MainMenuEnum.P_PAGO_DE_FACTURAS -> navigateToOption(PayBillActivity::class.java)
            MainMenuEnum.P_PAYU -> navigateToOption(PayuMenuActivity::class.java)
            MainMenuEnum.P_REGISTRADURIA -> navigateToOption(RegistraduriaMenuActivity::class.java)
            MainMenuEnum.P_SITP -> navigateToOption(SitpMenuActivity::class.java)
            MainMenuEnum.P_SNR -> navigateToOption(SnrActivity::class.java)
            MainMenuEnum.P_ADULTO_MAYOR -> navigateToOption(ElderlyMenuActivity::class.java)
            MainMenuEnum.P_BBVA -> navigateToOption(BbvaMenuActivity::class.java)
            MainMenuEnum.P_COLSUBSIDIO -> navigateToOption(ColsubsidioMenuActivity::class.java)
        }
    }

}