package co.com.pagatodo.core.views.homemenu.betplaymenu

import co.com.pagatodo.core.views.betplay.*
import co.com.pagatodo.core.views.homemenu.MainMenuEnum

class ItemsBetplayMenuActivity: BetplayMenuActivity() {
    override fun onClickMenu(type: MainMenuEnum?) {
        when(type) {
            MainMenuEnum.P_B_RECARGAR -> { navigateToOption(BetplayActivity::class.java)}
            MainMenuEnum.P_B_REIMPRIMIR -> { navigateToOption(BetplayReprintActivity::class.java)}
            MainMenuEnum.P_B_GENERAR_PIN -> { navigateToOption(BetplayGeneratePinActivity::class.java)}
            MainMenuEnum.P_B_COBRO_RETIRO -> { navigateToOption(BetplayCollectActivity::class.java)}
            MainMenuEnum.P_B_APUESTA_RAPIDA -> { navigateToOption(BetplayQuickBetActivity::class.java) }
        }
    }
}

