package co.com.pagatodo.core.network

import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.RequestVirtualWalletActivatePinDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.ResponseActivatePinDTO
import co.com.pagatodo.core.data.dto.response.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interfaz usada para definir los metodos de cada servicio, en ellos se le asigna una etiqueta con el tipo de llamado al servicio
 * y tambien el endpoint perteneciente a ese servicio, en el metodo se asigna como parametro de entrada el request como DTO
 * y como variable de retorno un observable que contiene el DTO de respuesta.
 */
interface PagaTodoService {

    @POST("seguridad/autenticarUsuario")
    fun login(@Body requestLoginDTO: RequestLoginDTO): Observable<AuthDTO>

    @POST("seguridad/cerrarSesion")
    fun logout(@Body requestLogoutDTO: RequestLogoutDTO): Observable<ResponseGenericDTO>

    @POST("seguridad/biometriaValidar")
    fun authFingerPrintSeller(@Body requestAuthFingerPrintSellerDTO: RequestAuthFingerPrintSellerDTO): Observable<BaseResponseDTO>

    @POST("seguridad/tokenFisicoValidar")
    fun authToken(@Body requestAuthTokenDTO: RequestAuthTokenDTO): Observable<BaseResponseDTO>

    @POST("administracion/consultarParametrosGenerales")
    fun generalParameters(@Body requestGeneralParametersDTO: RequestGeneralParametersDTO
    ): Observable<GeneralParametersDTO>

    @POST("recarga/realizarRecarga")
    fun dispatchRecharge(@Body requestRechargeDTO: RequestRechargeDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseRechargeDTO>

    @POST("recarga/consultarRecarga")
    fun historyRecharge(@Body requestRechargeDTO: RequestRechargeHistoryDTO
    ): Observable<ResponseRechargeHistoryDTO>

    @POST("administracion/consultarCuadreActual")
    fun consultCurrentBox(@Body requestCurrentBoxDTO: RequestCurrentBoxDTO
    ): Observable<ResponseCurrentBoxDTO>

    @POST("juegosventaazar/generarNumeroAleatorio")
    fun randomNumber(@Body requestRandomNumberDTO: RequestRandomNumberDTO
    ): Observable<RandomNumberDTO>

    // Definicion del tipo de envio y el endpoint perteneciente a dicho servicio
    @POST("juegosventaazar/chance/realizarVenta")
    /**
     * Metodo utilizado para realizar el llamado del servicio de pago de chance,
     * este metodo retorna un observable que contiene la respuesta retornada por el servicio
     * @param requestChanceDTO DTO perteneciente a la solicitud que se va a enviar al servicio
     */
    fun payChance(@Body requestChanceDTO: RequestChanceDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseChanceDTO>

    @POST("seguridad/cambiarClave")
    fun resetPassword(@Body requestChanceDTO: RequestResetPasswordDTO
    ): Observable<ResponseResetPasswordDTO>

    @POST("administracion/consultarCartera")
    fun getWallet(@Body requestWalletDTO: RequestWalletDTO
    ): Observable<ResponseWalletDTO>

    @POST("administracion/consultarNumerosGanadores")
    fun getLotteryResult(@Body requestDTO: RequestLotteryResultDTO
    ): Observable<ResponseLotteryResultDTO>

    @POST("administracion/consultaMenu")
    fun getMenus(@Body requestDTO: RequestUtilDTO): Observable<ResponseMenuDTO>

    /*
    *
    * Raffles Querys
    *
    * **/

    @POST("rifas/generarNumeroAleatorio")
    fun getRandomNumberRaffle(@Body requestRaffleRandomNumberDTO: RequestRaffleRandomNumberDTO
    ): Observable<ResponseRaffleRandomNumberDTO>

    @POST("rifas/consultarDisponibilidadNumero")
    fun getRaffleAvailable(@Body requestRaffleAvailableDTO: RequestRaffleAvailableDTO
    ): Observable<ResponseRaffleAvailableDTO>

    @POST("rifas/realizarVenta")
    fun registerRaffle(@Body requestRaffleSendDTO: RequestRaffleSendDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseRaffleSendDTO>

    @POST("rifas/traeRocupado")
    fun raffleAvailableRange(@Body requestRaffleAvailableRangeDTO: RequestRaffleAvailableRangeDTO): Observable<ResponseRaffleAvailableRangeDTO>

    @POST("pagomillonario/consultarParametros")
    fun getPaymillionaireParameters(@Body requestDTO: RequestPaymillionaireParametersDTO
    ): Observable<ResponsePaymillionaireParametersDTO>

    @POST("pagomillonario/realizarVenta")
    fun payPaymillionaire(@Body requestDTO: RequestChanceDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseChanceDTO>

    @POST("betPlay/registrarRecaudo")
    fun payRechargeBetplay(@Body requestRechargeBetplayDTO: RequestRechargeBetplayDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseBetplayRechargeDTO>

    @POST("betPlay/consultarCedula")
    fun isDocumentValidBetplay(@Body requestRechargeBetplayDTO: RequestRechargeBetplayDTO
    ): Observable<ResponseDocumentConsultDTO>

    @POST("betPlay/consultarReimprimir")
    fun betplayReprint(@Body requestRechargeBetplayDTO: RequestBetplayReprintDTO
    ): Observable<ResponseBetplayRechargeDTO>

    @POST("betPlay/pinApuestaRapida")
    fun betplayQuickBet(@Body request: RequestPinQuickBetDTO
    ): Observable<ResponsePinQuickBetDTO>

    @POST("betPlay/generarPin")
    fun collectBetplay(@Body request: RequestGeneratePinDTO
    ): Observable<ResponseGeneratePinDTO>

    @POST("betPlay/registrarEgreso")
    fun checkOutBetplay(@Body request: RequestBetPlayCheckOut): Observable<ResponseBetPlayCheckOutDTO>

    @POST("astro/realizarVenta")
    fun paySuperAstro(@Body requestRechargeSuperAstroDTO: RequestSuperAstroDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseChanceDTO>

    @POST("loteriaFisica/consultarNumero")
    fun checkPhysicalLotteryNumber(@Body requestCheckNumberLotteryDTO: RequestCheckNumberLotteryDTO
    ): Observable<ResponseCheckNumberLotteryDTO>

    @POST("loteriaVirtual/consultarNumero")
    fun checkVirtualLotteryNumber(@Body requestCheckNumberLotteryDTO: RequestCheckNumberLotteryDTO
    ): Observable<ResponseCheckNumberLotteryDTO>

    @POST("loteriaVirtual/consultaResultado")
    fun checkVirtualLotteryResult(@Body requestCheckResultLotteryDTO: RequestCheckResultLotteryDTO
    ): Observable<ResponseCheckResultLotteryDTO>

    @POST("loteriaFisica/realizarVenta")
    fun payPhysicalLottery(@Body requestLotteryResultOperationDTO: RequestLotteryResultOperationDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponsePayPhysicalLotteryDTO>

    @POST("loteriaVirtual/realizarVenta")
    fun payVirtualLottery(@Body requestLotteryResultOperationDTO: RequestLotteryResultOperationDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponsePayPhysicalLotteryDTO>

    @POST("loteriaVirtual/generarNumeroAleatorio")
    fun generateRandomVirtualLottery(@Body requestRandomVirtualLottery: RequestRandomVirtualLotteryDTO
    ): Observable<ResponseCheckNumberLotteryDTO>

    /*
    *
    * Sporting Querys
    *
    * **/
    @POST("deportivas/consultarParametros")
    fun getSportingParameters(@Body requestGeneralParametersDTO: RequestGeneralParametersDTO
    ): Observable<ResponseSportingsDTO>

    @POST("deportivas/vender")
    fun sellSportinBet(@Body requestSellSportingBetDTO: RequestSellSportingBetDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseSellSportingBetDTO>


    /*
    *
    * Baloto Querys
    *
    * **/

    @POST("baloto/consultarResultados")
    fun getBalotoResult(@Body requestDTO: RequestBalotoParametersDTO
    ): Observable<ResponseBalotoResultsDTO>

    @POST("baloto/consultarParametros")
    fun getParametersBaloto(@Body requestDTO: RequestBalotoParametersDTO
    ): Observable<ResponseBalotoParametersDTO>

    @POST("baloto/realizarApuesta")
    fun sendBalotoBet(@Body requestDTO: RequestBalotoSendBetDTO, @Header("isRetry") isRetry: Boolean = true
    ): Observable<ResponseBalotoSendBetDTO>

    @POST("util/requestUtil")
    fun updateStub(@Body requestDTO: RequestUtilDTO): Observable<ResponseUpdateStubDTO>

    @POST("baloto/consultarEstadoTiquete")
    fun checkStateTicket(@Body requestDTO: RequestBalotoCheckTicketDTO
    ): Observable<ResponseBalotoCheckTicketDTO>

    @POST("baloto/cobrarTiquete")
    fun chargeTicket(@Body requestDTO: RequestChargeTicketDTO
    ): Observable<ResponseChargeTicketDTO>

    /*
    *
    * QueryWinners Querys
    *
    * **/
    @POST("premios/consultartiquetepremiado")
    fun queryWinners(@Body requestDTO: RequestQueryWinnersDTO
    ): Observable<ResponseQueryWinnersDTO>

    /*
    *
    * Virtual Wallet Querys
    *
    * **/
    @POST("mbvpin/consultarPin")
    fun queryPinVirtualWallet(@Body requestDTO: RequestQueryPinWalletDTO
    ): Observable<ResponseQueryPinDTO>

    @POST("mbvpin/activarPin")
    fun activatePinVirtualWallet(@Body requestDTO: RequestVirtualWalletActivatePinDTO
    ): Observable<ResponseActivatePinDTO>

    /*
    *
    * Colpensiones BEPS Querys
    *
    * **/
    @POST("recaudobeps/realizarRecaudo")
    fun makeColpensionesCollection(@Body requestDTO: RequestColpensionesBepsMakeCollectionDTO
    ): Observable<ResponseColpensionesBepsMakeCollectionDTO>

    @POST("recaudobeps/consultarRecaudo")
    fun queryColpensionesCollection(@Body requestDTO: RequestColpensionesBepsQueryCollectionDTO
    ): Observable<ResponseColpensionesBepsQueryCollectionDTO>


    /*
    *
    *
    * Pay Bill Querys
    *
    * */
    @POST("pagofacturas/consultarServiciosFactura")
    fun payBillGetAgreement(@Body requestDTO: RequestPayBillGetAgreementsDTO
    ): Observable<ResponsePayBillGetAgreementsDTO>

    @POST("pagofacturas/consultarFactura")
    fun payBillGetBill(@Body requestDTO: RequestGetBillDTO
    ): Observable<ResponseGetBillDTO>

    @POST("pagofacturas/pagarFactura")
    fun payBill(@Body requestDTO: RequestPayBillDTO
    ): Observable<ResponsePayBillDTO>

    @POST("pagofacturas/consultarFacturaImpresion")
    fun payBillRePrint(@Body requestDTO: RequestPayBillRePrintDTO
    ): Observable<ResponsePayBillRePrintDTO>
    /*
    *
    *
    * PAYU Querys
    *
    * */
    @POST("payu/consultarPago")
    fun payuGetPayment(@Body requestDTO: RequestGetPayuDTO
    ): Observable<ResponseGetPayuDTO>

    @POST("payu/recaudarPayU")
    fun collectPayu(@Body requestDTO: RequestPayuCollectingDTO
    ): Observable<ResponsePayuCollectingDTO>

    @POST("payu/consultarReimpresion")
    fun payuReprint(@Body requestDTO: RequestPayuReprintDTO
    ): Observable<ResponsePayuReprintDTO>

    /*
    *
    * Virtual SOAT Querys
    *
    * **/

    @POST("soatvirtual/consultarSoatVirtual")
    fun queryVirtualSoat(@Body requestDTO: RequestQueryVirtualSoatDTO): Observable<ResponseQueryVirtualSoatDTO>

    @POST("soatvirtual/emitirPoliza")
    fun issuePolicy(@Body requestDTO: RequestIssuePolicyDTO): Observable<ResponseIssuePolicyDTO>

    @POST("soatvirtual/confirmarPoliza")
    fun policyConfirm(@Body resquestDTO: RequestPolicyConfirmDTO):Observable<BaseResponseDTO>


    /*
    *
    * GIROS Querys
    *
    * **/

    @POST("giros/consultarParametros")
    fun queryParameterGiro(@Body requestDTO: RequestGiroParameterDTO): Observable<ResponseGiroParameterDTO>

    @POST("giros/consultarParametros")
    fun queryParameterElderly(@Body requestDTO: RequestElderlyParameterDTO): Observable<ResponseElderlyParameterDTO>

    @POST("util/consultarCiudades")
    fun queryCities(@Body requestDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>

    @POST("giros/consultarAgencias")
    fun queryAgencies(@Body requestDTO: RequestQueryAgencyDTO): Observable<ResponseQueryAgencyDTO>

    @POST("giros/consultarTercero")
    fun queryThird(@Body requestDTO: RequestQueryThirdDTO): Observable<ResponseQueryThirdDTO>

    @POST("giros/calcularConceptos")
    fun calculateConcepts(@Body requestDTO: RequestGirosCalculateConceptsDTO): Observable<ResponseGirosCalculateConceptsDTO>

    @POST("giros/consultarDatosUsuario")
    fun getGiroUser(@Body requestDTO: RequestGiroUserDTO): Observable<ResponseGiroUserDTO>

    @POST("giros/iniciarSesion")
    fun girosLogin(@Body requestDTO: RequestGiroLoginDTO): Observable<ResponseGiroLoginDTO>

    @POST("giros/captarGiro")
    fun captureGiro(@Body requestDTO: RequestGiroCaptureDTO): Observable<ResponseGiroCaptureDTO>

    @POST("giros/crearTercero")
    fun createThird(@Body requestDTO: RequestThirdCreateDTO): Observable<ResponseThirdCreateDTO>

    @POST("giros/actualizarTercero")
    fun updateThird(@Body requestDTO: RequestThirdCreateDTO): Observable<BaseResponseDTO>

    @POST("giros/consultarGiro")
    fun queryGiro(@Body requestDTO: RequestQueryGiroDTO): Observable<ResponseQueryGiroDTO>

    @POST("giros/pagarGiro")
    fun paymentGiro(@Body requestDTO: RequestPaymentGiroDTO): Observable<ResponsePaymentGiroDTO>

    @POST("giros/enviarComprobante")
    fun sendVoucher(@Body requestDTO: RequestGiroSendVoucherDTO): Observable<ResponseGiroSendVoucherDTO>

    @POST("giros/consultarGiroGriterio")
    fun giroCriteria(@Body requestDTO: RequestGiroCriteriaDTO): Observable<ResponseGiroCriteriaDTO>

    @POST("giros/consultarMovimientos")
    fun giroConsultMovement(@Body requestDTO: RequestGiroMovementDTO): Observable<ResponseGiroMovementDTO>

    @POST("giros/autenticarUsuarioEnrolado")
    fun authFingerPrintUser(@Body requestDTO: RequestAuthFingerPrintUserDTO): Observable<BaseResponseDTO>

    @POST("giros/giroReimpresion")
    fun giroRePrint(@Body requestDTO: RequestGiroRePrintDTO): Observable<ResponseGiroRePrintDTO>

    @POST("giros/exonerarHuella")
    fun exonerateFingerPrint(@Body requestDTO: RequestGiroExFingerPrintDTO): Observable<BaseResponseDTO>

    @POST("giros/solicitudAutorizacion")
    fun giroAuthorization(@Body requestDTO: RequestGiroAuthorizationDTO): Observable<BaseResponseDTO>

    @POST("giros/modificarNotas")
    fun modifyNotes(@Body requestDTODTO: RequestGiroModifyNotesDTO): Observable<BaseResponseDTO>

    @POST("giros/consultarSolicitudes")
    fun giroCheckRequest(@Body requestDTO: RequestGiroCheckRequestDTO): Observable<ResponseGiroCheckRequestDTO>

    @POST("giros/xxxx")
    fun openBox(@Body requestDTO: RequestGiroOpenBoxDTO): Observable<ResponseGiroOpenBoxDTO>

    @POST("giros/cerrarCaja")
    fun closeBox(@Body requestDTO: RequestGiroCloseBoxDTO): Observable<ResponseGiroCloseBoxDTO>


    /*
     *
     * SITP Collection Querys
     *
     * **/

    @POST("recaudoSitp/recargaTarjeta")
    fun cardRecharge(@Body requestDTO: RequestCardRechargeDTO): Observable<ResponseCardRechargeDTO>

    @POST("recaudoSitp/consultarDatosIniciales")
    fun queryInitDate(@Body requestDTO: RequestQueryInitDateDTO): Observable<ResponseQueryInitDateDTO>

    @POST("recaudoSitp/consultarIventario")
    fun queryInventory(@Body requestDTO: RequestQueryInventoryDTO): Observable<ResponseQueryInventoryDTO>

    @POST("recaudoSitp/consultarTarjetaListasNegras")
    fun queryCardBlack(@Body requestDTO: RequestQueryCardBlackDTO): Observable<ResponseQueryCardBlackDTO>



    /*
    *
    * SNR (Súper notariado y registro)
    *
    * **/

    @POST("snr/conceptosSnr")
    fun querySnrConcepts(@Body requestDTO: RequestQuerySnrConceptsDTO
    ): Observable<ResponseQuerySnrConceptsDTO>

    @POST("snr/consultarOficinasSnr")
    fun querySnrOffices(@Body requestDTO: RequestQuerySnrGetOfficesDTO
    ): Observable<ResponseQuerySnrGetOfficesDTO>

    @POST("snr/consultarParametriaSnr")
    fun querySnrGetParameters(@Body requestDTO: RequestQuerySnrGetParametersDTO
    ): Observable<ResponseQuerySnrGetParametersDTO>

    @POST("snr/realizarRecaudo")
    fun makeSnrCollections(@Body requestDTO: RequestQuerySnrMakeCollectionsDTO
    ): Observable<ResponseQuerySnrMakeCollectionsDTO>

    /*
    *
    * Colsubsidio
    *
    * **/

    @POST("recaudoColsubsidio/consultarDatosIniciales")
    fun queryColsubsidioGetInitialData(@Body requestDTO: RequestColsubsidioGetInitialDataDTO
    ): Observable<ResponseColsubsidioGetInitialDataDTO>

    @POST("recaudoColsubsidio/consultarMediosPago")
    fun queryColsubsidioGetPaymentMethods(@Body requestDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioGetPaymentMethodsDTO>

    @POST("recaudoColsubsidio/consultarProductos")
    fun queryColsubsidioGetProducts(@Body requestDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioGetProductsDTO>

    @POST("recaudoColsubsidio/consultarProductosTopes")
    fun queryColsubsidioGetBumperProducts(@Body requestDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioBumperProductsDTO>

    @POST("recaudoColsubsidio/recaudarObligacion")
    fun queryColsubsidioCollectObligation(@Body requestDTO: RequestColsubsidioCollectObligationDTO
    ): Observable<ResponseColsubsidioCollectObligationDTO>

    /*
    *
    * Registraduría
    *
    * **/

    @POST("registraduria/consultarrecaudo")
    fun queryRegistraduriaGetCollection(@Body requestDTO: RequestRegistraduriaGetCollectionDTO
    ): Observable<ResponseRegistraduriaGetCollectionDTO>

    @POST("registraduria/consultarservicios")
    fun queryRegistraduriaGetServices(@Body requestDTO: RequestRegistraduriaGetServiceDTO
    ): Observable<ResponseRegistraduriaGetServiceDTO>

    @POST("registraduria/recaudar")
    fun queryRegistraduriaCollection(@Body requestDTO: RequestRegistraduriaCollectionDTO
    ): Observable<ResponseRegistraduriaCollectionDTO>

    /*
    *
    * Adulto Mayor
    *
    * **/

    @POST("adultomayor/consultarReimpresion")
    fun elderlyQueryGetReprint(@Body requestDTO: RequestElderlyGetReprintDTO
    ): Observable<ResponseElderlyGetReprintDTO>

    @POST("adultomayor/isTerminalAutorizada")
    fun elderlyQueryAuthorizedTerminal(@Body requestDTO: RequestElderlyAuthorizedTerminalDTO
    ): Observable<ResponseElderlyAuthorizedTerminalDTO>

    @POST("adultomayor/dedosSenalados")
    fun elderlyPointedFingers(@Body requestDTO: RequestPointedFingersDTO): Observable<ResponsePointedFingersDTO>

    @POST("adultomayor/consultarCedula")
    fun elderlyQueryId(@Body requestDTO: RequestElderlyQueryIdDTO): Observable<ResponseElderlyQueryIdDTO>

    @POST("adultomayor/registrarHuella")
    fun elderlySaveFingerPrint(@Body requestDTO: RequestElderlySaveFingerPrintDTO): Observable<ResponseElderlySaveFingerPrintDTO>

    @POST("adultomayor/consultarSubsidio")
    fun elderlyQuerySubsidy(@Body requestDTO: RequestElderlyQuerySubsidyDTO): Observable<ResponseElderlyQuerySubsidyDTO>

    @POST("adultomayor/autenticarHuella")
    fun elderlyQueryAuthenticateFootprint(@Body requestDTO: RequestElderlyAuthenticateFootprintDTO): Observable<ResponseElderlyAuthenticateFootprintDTO>

    @POST("adultomayor/guardarCedula")
    fun elderlySaveId(@Body requestDTO: RequestElderlySaveIdDTO): Observable<ResponseElderlySaveIdDTO>

    @POST("adultomayor/realizarPagoSubsidio")
    fun elderlyQueryMakeSubsidyPayment(@Body requestDTO: RequestElderlyMakeSubsidyPaymentDTO): Observable<ResponseElderlyMakeSubsidyPaymentDTO>

    /*
    *
    * BBVA
    *
    * **/


    @POST("bbva/deposito")
    fun accountDeposit(@Body requestDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>

    @POST("bbva/realizarRecaudoFactura")
    fun billPayment(@Body requestDTO: RequestBbvaBillPaymentDTO): Observable<ResponseBbvaBillPaymentDTO>

    @POST("bbva/pagoObligacion")
    fun paymentObligations(@Body requestDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>

    @POST("bbva/realizarRetiro")
    fun withdrawalAndBalance(@Body requestDTO: RequestBbvaDTO): Observable<ResponseBbvaDTO>

    @POST("bbva/xxx")
    fun reprintTransaction(@Body requestDTO: RequestBbvaReprintTransactionDTO): Observable<ResponseBbvaReprintTransactionDTO>

    @POST("bbva/xxx")
    fun closeBox(@Body requestDTO: RequestBbvaCloseBoxDTO): Observable<ResponseBbvaCloseBoxDTO>

    @POST("bbva/consultarParametros")
    fun queryParameters(@Body requestDTO: RequestQueryParameterDTO): Observable<ResponseQueryParameterDTO>

    @POST ("bbva/validarFactura")
    fun validateBill(@Body requestBbvaDTOAndPay: RequestBbvaValidateAndPayBillDTO): Observable<ResponseBbvaValidateBillDTO>





}
