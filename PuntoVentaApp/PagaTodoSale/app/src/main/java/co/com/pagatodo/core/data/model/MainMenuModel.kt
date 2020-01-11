package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.views.homemenu.MainMenuEnum

class MainMenuModel {
    var id: Int? = 0
    var name: String? = null
    var menuType: MainMenuEnum? = null
    var isEnable = true
    var principal: Int? = 0
}