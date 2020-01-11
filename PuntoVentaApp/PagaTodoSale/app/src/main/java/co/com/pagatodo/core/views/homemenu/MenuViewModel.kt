package co.com.pagatodo.core.views.homemenu

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IAuthInteractor
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.di.AuthModule
import co.com.pagatodo.core.di.DaggerAuthComponent
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

enum class MainMenuEnum:Serializable {
    M_PRODUCTOS_SERVICIOS, M_CONSULTAS_Y_PREMIOS, M_ASESOR,
    M_NOTIFICACIONES, M_CONTACTO, M_INFO_DISPOSITIVO, M_CERRAR_SESSION,

    // Home menu
    P_JUEGOS_CHANCE, P_RECARGAS, P_BETPLAY, P_SUPER_ASTRO, P_BALOTO, P_RIFAS, P_LAS_DEPORTIVAS,
    P_LOTERIA_VIRTUAL, P_LOTERIA_FISICA, P_BEPS_COLPENSIONES, P_RECARGA_APP_PGT, P_PAGO_DE_FACTURAS,
    P_PAYU, P_REGISTRADURIA, P_SOAT_VIRTUAL,P_GIROS,P_SITP, P_SNR, P_COLSUBSIDIO, P_BBVA,


    // Juegos Chance
    P_JC_CHANCE_TRADICIONAL, P_JC_MAXICHANCE, P_JC_SUPER_CHANCE , P_JC_PAGA_MILLONARIO, CHANCE_REPETIR_APUESTA,

    // Betplay
    P_B_RECARGAR, P_B_REIMPRIMIR,P_B_GENERAR_PIN, P_B_COBRO_RETIRO, P_B_APUESTA_RAPIDA,

    // Superastro
    SUPERASTRO_NUEVA_APUESTA, SUPERASTRO_REPETIR_APUESTA,

    // Baloto
    P_B_NUEVA_APUESTA, P_B_PAGO_PREMIO,

    // Las Deportivas
    P_LD_MEGAGOL, P_LD_LIGA14,

    // Consultas
    CP_LOTERIAS_RESULTADOS,CP_SORTEOS_LOTERIAS_RESULTADOS, CP_PAGA_MILLONARIO_ACUMULADO,
    CP_BALOTO_RESULTADOS, CP_BALOTO_ACUMULADO, CP_BALOTO_GANADORES,
    CP_CONSULTAR_GANADORES, CP_RECAGAS_CONSULTAS, CP_PAGA_MILLONARIO_RESULTADOS,

    // Colpensiones
    P_BC_RECAUDO, P_BC_REIMPRESION,

    // Baloto
    CP_BG_GANADORES_BALOTO, CP_BG_GANADORES_BALOTO_REVANCHA,
    PAGO_FACTURA, REIMPRIMIR_FACTURA,

    LOTERIAS_RESULTADOS, PAGA_MILLONARIO_ACUMULADO,
    BALOTO_RESULTADOS, BALOTO_ACUMULADO, BALOTO_GANADORES,
    CONSULTAR_GANADORES, RECARGAS_CONSULTAS,

    // Asesor
    CONSULTAR_PREMIOS, AS_CUADRE_ACTUAL, AS_REPORTE_VENTAS, AS_CAMBIO_CLAVE, AS_CARTERA,

    // Dispositivo
    DI_INFORMACION_DATAFONO, DI_IMPRESORAS_DEL_DISPOSITIVO,

    // PAYU
    P_PAYU_RECAUDO, P_PAYU_REIMPRESION,

    // Colsubsidio
    P_COLSUBSIDIO_RECAUDO,P_COLSUBSIDIO_RETIRO,

    // REGISTRADURIA
    P_REGISTRADURIA_RECAUDO, P_REGISTRADURIA_REIMPRESION,

    //GIROS
    P_GIRO_SEND, P_GIRO_PAYMENT, P_GIRO_CONSULT, P_GIRO_USERS, P_GIRO_MOVEMENT, P_GIRO_AUTHORIZATIONS,
    P_GIRO_NOTES,P_GIRO_REQUEST,P_GIRO_CLOSE_BOX,P_GIRO_REPRINT,

    //SITP
    P_SITP_RECARGAR,
    P_SITP_VENDER_TARJETA,

    // Adaulto mayor
    P_ADULTO_MAYOR,
    P_ADULTO_MAYOR_ENROLL,
    P_ADULTO_MAYOR_PAY,
    P_ADULTO_REPRINT,

    // BBVA
    P_BBVA_BILL_PAYMENT, P_BBVA_DEPOSITS_BBVA, P_BBVA_PAYMENT_OF_OBLIGATIONS,
    P_BBVA_WITHDRAWAL_AND_BALANCE_INQUIRY, P_BBVA_REPRINT_LAST_TRANSACTION,P_BBVA_CLOSE_THE_BOX,

    // Loterias fisicas
    P_AVAILABLE_LOTTERY, P_SALE_LOTTERY,

    REGRESAR
}

@Singleton
class MenuViewModel: ViewModel() {
    lateinit var menusLiveData:  MutableLiveData<List<MainMenuModel>>
    lateinit var informationLiveData: MutableLiveData<String>

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
    }

    @Inject
    lateinit var authInteractor: IAuthInteractor

    init {
        DaggerAuthComponent.builder().authModule(AuthModule()).build().inject(this)
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        if(!::menusLiveData.isInitialized) {
            menusLiveData = MutableLiveData()
        }

        if(!::informationLiveData.isInitialized) {
            informationLiveData = MutableLiveData()
        }
    }

    fun loadMenu() {
        menusLiveData.value = getMenu().filter { it.isEnable }
    }

    fun loadMainMenu() {
        menusLiveData.value = getMainMenu().filter { it.isEnable }
    }

    fun loadChanceMenu() {
        menusLiveData.value = getChanceMenu().filter { it.isEnable }
    }

    fun loadBetplayMenu() {
        menusLiveData.value = getBetplayMenu().filter { it.isEnable }
    }

    fun loadSuperAstroMenu(){
        menusLiveData.value = getSuperAstroMenu().filter { it.isEnable }
    }

    fun loadBalotoMenu() {
        menusLiveData.value = getBalotoMenu().filter { it.isEnable }
    }

    fun loadBalotoWinnersMenu() {
        menusLiveData.value = getBalotoWinnersMenu().filter { it.isEnable }
    }

    fun loadInformationToShowAfterStubs() {
        informationLiveData.value = getInformationToShowAfterStubs()
    }

    fun loadSportingMenu() {
        menusLiveData.value = getSportingMenu().filter { it.isEnable }
    }

    fun loadColsubsidioMenu(){
        menusLiveData.value = getColsubsidioMenu().filter { it.isEnable }
    }

    fun loadConsults(){
        menusLiveData.value = getConsultsMenu().filter { it.isEnable }
    }

    fun loadAdvisor(){
        menusLiveData.value = getAdvisorMenu().filter { it.isEnable }
    }

    fun loadDeviceInfo(){
        menusLiveData.value = getDeviceMenu().filter { it.isEnable }
    }

    fun loadColpensionesBepsMenu() {
        menusLiveData.value = getColpensionesBepsMenu().filter { it.isEnable }
    }

    fun loadPayuMenu() {
        menusLiveData.value = getPayuMenu()
    }

    fun loadRegistraduriaMenu() {
        menusLiveData.value = getRegistraduriaMenu()
    }

    fun loadGiroMenu(){
        menusLiveData.value = getGiroMenu().filter { it.isEnable }
    }

    fun loadBbvaMenu(){
        menusLiveData.value = getBbvaMenu().filter { it.isEnable }
    }

    fun loadElderlyMenu(){
        menusLiveData.value = getElderlyMenu().filter { it.isEnable }
    }

    fun loadSitpMenu(){
        menusLiveData.value = getSitpMenu().filter { it.isEnable }
    }

    fun loadLotteryMenu(){
        menusLiveData.value = getLotteryMenu().filter { it.isEnable }
    }

    @SuppressLint("CheckResult")
    fun logout() {
        authInteractor.logout()?.subscribe ({
            if(it.isSuccess) {
                CURRENT_CHANCE_BET = null
                CURRENT_SUPERASTRO_BET = null
                lastTransactionStatusObject = null
                singleLiveEvent.value = MenuViewModel.ViewEvent.ResponseSuccess(it.message.toString())
            } else {
                singleLiveEvent.value = MenuViewModel.ViewEvent.ResponseError(it.message.toString())
            }
        }, {
            singleLiveEvent.value = MenuViewModel.ViewEvent.ResponseError(it.message.toString())
        })
    }
}

private fun getInformationToShowAfterStubs(): String {
    return SharedPreferencesUtil.getPreference(R_string(R.string.sp_welcome_param_service))
}

private fun getMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_PRODUCTOS_SERVICIOS
        id = type.ordinal
        name = R_string(R.string.main_menu_title_products)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.M_PRODUCTOS_SERVICIOS.name)
        principal = 1
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_CONSULTAS_Y_PREMIOS
        id = type.ordinal
        name = R_string(R.string.main_menu_title_consults)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.M_CONSULTAS_Y_PREMIOS.name)
        principal = 1
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_ASESOR
        id = type.ordinal
        name = R_string(R.string.main_menu_title_adviser)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.M_ASESOR.name)
        principal = 1
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_NOTIFICACIONES
        id = type.ordinal
        name = R_string(R.string.main_menu_title_notifications)
        menuType = type
        principal = 1
        isEnable = false
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_CONTACTO
        id = type.ordinal
        name = R_string(R.string.main_menu_title_contact)
        menuType = type
        principal = 1
        isEnable = false
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_INFO_DISPOSITIVO
        id = type.ordinal
        name = R_string(R.string.main_menu_title_device)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.M_INFO_DISPOSITIVO.name)
        principal = 1
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.M_CERRAR_SESSION
        id = type.ordinal
        name = R_string(R.string.main_menu_title_logout)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.M_CERRAR_SESSION.name)
        principal = 1
    })

    return menus
}

private fun getMainMenu(): List<MainMenuModel> {

    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_JUEGOS_CHANCE
        id = type.ordinal
        name = R_string(R.string.home_menu_chance_games)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_JUEGOS_CHANCE.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_RECARGAS
        id = type.ordinal
        name = R_string(R.string.home_menu_title_recharge)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_RECARGAS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BETPLAY
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery_betplay)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BETPLAY.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_SUPER_ASTRO
        id = type.ordinal
        name = R_string(R.string.home_menu_title_superastro)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SUPER_ASTRO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BALOTO
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery_baloto)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BALOTO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_RIFAS
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery_raffles)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_RIFAS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_LAS_DEPORTIVAS
        id = type.ordinal
        name = R_string(R.string.home_menu_title_sportings)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_LAS_DEPORTIVAS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_LOTERIA_VIRTUAL
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery_virtual)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_LOTERIA_VIRTUAL.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_LOTERIA_FISICA
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_LOTERIA_FISICA.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_GIROS
        id = type.ordinal
        name = R_string(R.string.home_menu_title_giros)
        menuType = type
        isEnable =  CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIROS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_SOAT_VIRTUAL
        id = type.ordinal
        name = R_string(R.string.virtual_soat_title)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SOAT_VIRTUAL.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_RECARGA_APP_PGT
        id = type.ordinal
        name = R_string(R.string.home_menu_title_virtual_wallet)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_RECARGA_APP_PGT.name)
    })

    if (DeviceUtil.isSalePoint())
        addMainMenuForSalepoint(menus)

    return menus
}

fun getChanceMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_JC_MAXICHANCE
        id = type.ordinal
        name = R_string(R.string.home_menu_promotionals_title_maxichance)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_JC_MAXICHANCE.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_JC_PAGA_MILLONARIO
        id = type.ordinal
        name = R_string(R.string.home_menu_chance_paymillionaire)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_JC_PAGA_MILLONARIO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_JC_SUPER_CHANCE
        id = type.ordinal
        name = R_string(R.string.home_menu_promotionals_title_superchance)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_JC_SUPER_CHANCE.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_JC_CHANCE_TRADICIONAL
        id = type.ordinal
        name = R_string(R.string.home_menu_chance_traditional)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_JC_CHANCE_TRADICIONAL.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CHANCE_REPETIR_APUESTA
        id = type.ordinal
        name = R_string(R.string.home_menu_chance_title_bett_replay)
        menuType = type
        isEnable = false
    })

    return menus
}

fun getBetplayMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_RECARGAR
        id = type.ordinal
        name = R_string(R.string.home_menu_betplay_title_recharge)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_RECARGAR.name)
    })

    if(DeviceUtil.isSalePoint())
        addBetplayMenuForSalepoint(menus)

    return menus
}

fun addBetplayMenuForSalepoint(menu : MutableList<MainMenuModel>){

    menu.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_REIMPRIMIR
        id = type.ordinal
        name = R_string(R.string.home_menu_betplay_title_reprint)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_REIMPRIMIR.name)
    })

    menu.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_COBRO_RETIRO
        id = type.ordinal
        name = R_string(R.string.home_menu_betplay_title_collect_take_out)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_COBRO_RETIRO.name)
    })

    menu.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_GENERAR_PIN
        id = type.ordinal
        name = R_string(R.string.home_menu_betplay_title_generate_pin)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_GENERAR_PIN.name)
    })

    menu.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_APUESTA_RAPIDA
        id = type.ordinal
        name = R_string(R.string.home_menu_betplay_title_fast_bet)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_APUESTA_RAPIDA.name)
    })

}

fun getSuperAstroMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.SUPERASTRO_NUEVA_APUESTA
        id = type.ordinal
        name = R_string(R.string.menu_super_astro_new_bet)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.SUPERASTRO_NUEVA_APUESTA.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.SUPERASTRO_REPETIR_APUESTA
        id = type.ordinal
        name = R_string(R.string.menu_super_astro_replay_bet)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.SUPERASTRO_REPETIR_APUESTA.name)
    })

    return menus
}

fun getBalotoMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_NUEVA_APUESTA
        id = type.ordinal
        name = R_string(R.string.home_menu_chance_title_bett_new)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_NUEVA_APUESTA.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_B_PAGO_PREMIO
        id = type.ordinal
        name = R_string(R.string.home_menu_chance_title_award_baloto)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_B_PAGO_PREMIO.name)
    })

    return menus
}

fun getConsultsMenu(): List<MainMenuModel>{
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_SORTEOS_LOTERIAS_RESULTADOS
        id = type.ordinal
        name = R_string(R.string.menu_query_lotteries_resulta_sorteos)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_SORTEOS_LOTERIAS_RESULTADOS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_LOTERIAS_RESULTADOS
        id = type.ordinal
        name = R_string(R.string.menu_query_lotteries_resulta_secos)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_LOTERIAS_RESULTADOS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_PAGA_MILLONARIO_ACUMULADO
        id = type.ordinal
        name = R_string(R.string.menu_query_accumulated_millionarie)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_PAGA_MILLONARIO_ACUMULADO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_BALOTO_RESULTADOS
        id = type.ordinal
        name = R_string(R.string.menu_query_baloto_results)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_BALOTO_RESULTADOS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_BALOTO_ACUMULADO
        id = type.ordinal
        name = R_string(R.string.menu_query_baloto_accumulated)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_BALOTO_ACUMULADO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_BALOTO_GANADORES
        id = type.ordinal
        name = R_string(R.string.baloto_winners_menu_title)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_BALOTO_GANADORES.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_CONSULTAR_GANADORES
        id = type.ordinal
        name = R_string(R.string.menu_query_winners)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_CONSULTAR_GANADORES.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_RECAGAS_CONSULTAS
        id = type.ordinal
        name = R_string(R.string.menu_query_recharge_queries)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_RECAGAS_CONSULTAS.name)
    })

    return menus
}

fun getBalotoWinnersMenu(): List<MainMenuModel>{
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_BALOTO_GANADORES
        id = type.ordinal
        name = R_string(R.string.menu_query_baloto_winners_op)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_BALOTO_GANADORES.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.CP_BG_GANADORES_BALOTO_REVANCHA
        id = type.ordinal
        name = R_string(R.string.menu_query_baloto_revenge_winners)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.CP_BG_GANADORES_BALOTO_REVANCHA.name)
    })

    return menus
}

fun getAdvisorMenu(): List<MainMenuModel>{
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.AS_CUADRE_ACTUAL
        id = type.ordinal
        name = R_string(R.string.menu_advisor_current_box)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.AS_CUADRE_ACTUAL.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.AS_REPORTE_VENTAS
        id = type.ordinal
        name = R_string(R.string.menu_advisor_sale_report)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.AS_REPORTE_VENTAS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.AS_CAMBIO_CLAVE
        id = type.ordinal
        name = R_string(R.string.menu_advisor_password_change)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.AS_CAMBIO_CLAVE.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.AS_CARTERA
        id = type.ordinal
        name = R_string(R.string.menu_advisor_wallet)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.AS_CARTERA.name)
    })

    return menus
}

fun getDeviceMenu(): List<MainMenuModel>{
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.DI_INFORMACION_DATAFONO
        id = type.ordinal
        if(DeviceUtil.isSalePoint() || DeviceUtil.isSmartPhone())
            name = R_string(R.string.menu_device_info_pos)
        else
            name = R_string(R.string.menu_device_info)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.DI_INFORMACION_DATAFONO.name)
    })

    if(DeviceUtil.isSalePoint())
        addDeviceMenuForSalepoint(menus)

    return menus
}

fun addDeviceMenuForSalepoint(menu : MutableList<MainMenuModel>){
    menu.add(MainMenuModel().apply {
        val type = MainMenuEnum.DI_IMPRESORAS_DEL_DISPOSITIVO
        id = type.ordinal
        name = R_string(R.string.menu_device_printers)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.DI_IMPRESORAS_DEL_DISPOSITIVO.name)
    })
}

private fun addMainMenuForSalepoint(menus : MutableList<MainMenuModel>){

    if (DeviceUtil.isSalePoint()){

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_BBVA
            id = type.ordinal
            name = R_string(R.string.home_menu_title_bbva)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_COLSUBSIDIO
            id = type.ordinal
            name = R_string(R.string.home_menu_title_colsubsidio)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_COLSUBSIDIO.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_SITP
            id = type.ordinal
            name = R_string(R.string.home_menu_title_sitp)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SITP.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_SNR
            id = type.ordinal
            name = R_string(R.string.home_menu_title_snr)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SNR.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_ADULTO_MAYOR
            id = type.ordinal
            name = R_string(R.string.home_menu_title_elderly)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_ADULTO_MAYOR.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_BEPS_COLPENSIONES
            id = type.ordinal
            name = R_string(R.string.home_menu_title_colpensiones_beps)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BEPS_COLPENSIONES.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_PAGO_DE_FACTURAS
            id = type.ordinal
            name = R_string(R.string.pay_bills)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_PAGO_DE_FACTURAS.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_PAYU
            id = type.ordinal
            name = R_string(R.string.home_menu_title_pay_u_collecting)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_PAYU.name)
        })

        menus.add(MainMenuModel().apply {
            val type = MainMenuEnum.P_REGISTRADURIA
            id = type.ordinal
            name = R_string(R.string.home_menu_title_registraduria_collection)
            menuType = type
            isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_REGISTRADURIA.name)
        })

    }
}

fun getSportingMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_LD_MEGAGOL
        id = type.ordinal
        name = R_string(R.string.item_menu_megagol)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_LD_MEGAGOL.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_LD_LIGA14
        id = type.ordinal
        name = R_string(R.string.item_menu_g14)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_LD_LIGA14.name)
    })
    return menus
}

fun getColsubsidioMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_COLSUBSIDIO_RECAUDO
        id = type.ordinal
        name = R_string(R.string.item_menu_colsubsidio_collection)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_COLSUBSIDIO_RECAUDO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_COLSUBSIDIO_RETIRO
        id = type.ordinal
        name = R_string(R.string.item_menu_colsubsidio_retirement)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_COLSUBSIDIO_RETIRO.name)
    })
    return menus
}

fun getColpensionesBepsMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BC_RECAUDO
        id = type.ordinal
        name = R_string(R.string.item_menu_colpensiones_beps_collection)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BC_RECAUDO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BC_REIMPRESION
        id = type.ordinal
        name = R_string(R.string.item_menu_colpensiones_beps_reprint)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BC_REIMPRESION.name)
    })
    return menus
}

fun MenuViewModel.getPayBillsMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.PAGO_FACTURA
        id = type.ordinal
        name = R_string(R.string.pay_bills)
        menuType = type
        isEnable = true
    })

    /*menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.REIMPRIMIR_FACTURA
        id = type.ordinal
        name = R_string(R.string.item_menu_reprint_bill)
        menuType = type
        isEnable = false
    })*/
    return menus
}

fun getPayuMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_PAYU_RECAUDO
        id = type.ordinal
        name = R_string(R.string.item_menu_colpensiones_beps_collection)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_PAYU_RECAUDO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_PAYU_REIMPRESION
        id = type.ordinal
        name = R_string(R.string.item_menu_colpensiones_beps_reprint)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_PAYU_REIMPRESION.name)
    })
    return menus
}

fun getRegistraduriaMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_REGISTRADURIA_RECAUDO
        id = type.ordinal
        name = R_string(R.string.item_menu_registraduria_collection)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_REGISTRADURIA_RECAUDO.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_REGISTRADURIA_REIMPRESION
        id = type.ordinal
        name = R_string(R.string.item_menu_registraduria_reprint)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_REGISTRADURIA_REIMPRESION.name)
    })
    return menus
}

 fun getGiroMenu(): List<MainMenuModel> {

     val menus = mutableListOf<MainMenuModel>()

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_SEND
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_send)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_SEND.name)
     })

    /* menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_PAYMENT
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_payment)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_PAYMENT.name)
     })*/

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_CONSULT
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_payment)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_CONSULT.name)
     })

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_USERS
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_users)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_USERS.name)
     })

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_REPRINT
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_reprint)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_REPRINT.name)
     })

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_MOVEMENT
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_movement)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_MOVEMENT.name)
     })

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_AUTHORIZATIONS
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_authorizations)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_AUTHORIZATIONS.name)
     })

     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_REQUEST
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_request)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_REQUEST.name)
     })


     menus.add(MainMenuModel().apply {
         val type = MainMenuEnum.P_GIRO_NOTES
         id = type.ordinal
         name = R_string(R.string.item_menu_giro_modify_notes)
         menuType = type
         isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_GIRO_NOTES.name)
     })

     return  menus
}

fun getBbvaMenu(): List<MainMenuModel> {

    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BBVA_BILL_PAYMENT
        id = type.ordinal
        name = R_string(R.string.item_menu_bbva_bill_payment)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA_BILL_PAYMENT.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BBVA_DEPOSITS_BBVA
        id = type.ordinal
        name = R_string(R.string.item_menu_bbva_deposits)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA_DEPOSITS_BBVA.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BBVA_PAYMENT_OF_OBLIGATIONS
        id = type.ordinal
        name = R_string(R.string.item_menu_bbva_payment_of_obligations)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA_PAYMENT_OF_OBLIGATIONS.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BBVA_WITHDRAWAL_AND_BALANCE_INQUIRY
        id = type.ordinal
        name = R_string(R.string.item_menu_bbva_withdrawal)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA_WITHDRAWAL_AND_BALANCE_INQUIRY.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BBVA_REPRINT_LAST_TRANSACTION
        id = type.ordinal
        name = R_string(R.string.item_menu_bbva_reprint_last_transaction)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA_REPRINT_LAST_TRANSACTION.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_BBVA_CLOSE_THE_BOX
        id = type.ordinal
        name = R_string(R.string.item_menu_bbva_close_the_box)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_BBVA_CLOSE_THE_BOX.name)
    })

    return  menus
}

fun getElderlyMenu(): List<MainMenuModel> {

    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_ADULTO_MAYOR_PAY
        id = type.ordinal
        name = R_string(R.string.elderly_pay_title)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_ADULTO_MAYOR_PAY.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_ADULTO_MAYOR_ENROLL
        id = type.ordinal
        name = R_string(R.string.elderly_enroll_title)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_ADULTO_MAYOR_ENROLL.name)
    })

    menus.add(MainMenuModel().apply {
        val type = MainMenuEnum.P_ADULTO_REPRINT
        id = type.ordinal
        name = R_string(R.string.elderly_enroll_reprint_title)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_ADULTO_REPRINT.name)
    })

    return  menus
}

fun getSitpMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type =MainMenuEnum.P_SITP_RECARGAR
        id = type.ordinal
        name = R_string(R.string.sitp_recharge_recharge)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SITP_RECARGAR.name)
    })

    menus.add(MainMenuModel().apply {
        val type =MainMenuEnum.P_SITP_VENDER_TARJETA
        id = type.ordinal
        name = R_string(R.string.sitp_recharge_sell_card)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SITP_VENDER_TARJETA.name)
    })

    return menus
}

fun getLotteryMenu(): List<MainMenuModel> {
    val menus = mutableListOf<MainMenuModel>()

    menus.add(MainMenuModel().apply {
        val type =MainMenuEnum.P_AVAILABLE_LOTTERY
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery_available)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_AVAILABLE_LOTTERY.name)
    })

    menus.add(MainMenuModel().apply {
        val type =MainMenuEnum.P_SALE_LOTTERY
        id = type.ordinal
        name = R_string(R.string.home_menu_title_lottery_sale)
        menuType = type
        isEnable = CurrencyUtils.validateMenuStatus(MainMenuEnum.P_SALE_LOTTERY.name)
    })

    return menus
}