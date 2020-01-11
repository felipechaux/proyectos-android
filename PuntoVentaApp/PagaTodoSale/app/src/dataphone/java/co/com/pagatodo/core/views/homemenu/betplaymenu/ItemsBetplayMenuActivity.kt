package co.com.pagatodo.core.views.homemenu.betplaymenu

import co.com.pagatodo.core.views.betplay.BetplayActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum

class ItemsBetplayMenuActivity: BetplayMenuActivity() {
    override fun onClickMenu(type: MainMenuEnum?) {
        when(type) {
            MainMenuEnum.P_B_RECARGAR -> { navigateToOption(BetplayActivity::class.java)}
        }
    }
}

