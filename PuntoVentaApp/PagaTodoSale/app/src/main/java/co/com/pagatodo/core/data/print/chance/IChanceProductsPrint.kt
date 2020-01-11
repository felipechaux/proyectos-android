package co.com.pagatodo.core.data.print.chance

import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus

/**
 * Interfaz usada para definir los métodos de la impresión de los productos de chance
 * esto con el fin de realizar una abstracción de estos métodos y que sin importar el tipo de impresora
 * se pueda llamar en cualquier lugar, de esta forma la impresión es transaparente del repositorio
 */
interface IChanceProductsPrint {
    /**
     * Método usado para realizar la impresión del producto chance
     */
    fun printChance(totalValueBetting: Double,
                    totalValuePaid: Double,
                    totalValueOverloaded: Double,
                    totalValueIva: Double,
                    raffleDate: String,
                    raffleHour: String,
                    lotteries: List<LotteryModel>,
                    chances: List<ChanceModel>,
                    iva: Int, sellerName: String,
                    uniqueSerial: String,
                    digitChecking: String,
                    stubs: String,
                    maxRows: Int,
                    isRetry: Boolean,
                    callback: (printerStatus: PrinterStatus) -> Unit?)
    /**
     * Método usado para realizar la impresión del producto paga millonario
     */
    fun printPaymillionaire(totalValueBetting: Double,
                            totalValuePaid: Double,
                            totalValueIva: Double,
                            raffleDate: String,
                            lotteries: List<LotteryModel>,
                            chances: List<ChanceModel>,
                            iva: Int,
                            sellerName: String,
                            uniqueSerial: String,
                            digitChecking: String,
                            prizePlan: List<String>,
                            selectedModeValue: ModeValueModel?,
                            stubs: String,
                            isRetry: Boolean,
                            callback: (printerStatus: PrinterStatus) -> Unit?)
    /**
     * Método usado para realizar la impresión del producto Maxi chance
     */
    fun printMaxiChance(printModel: BasePrintModel,
                        games: List<SuperchanceModel>,
                        isRetry: Boolean,
                        callback: (printerStatus: PrinterStatus) -> Unit?)
    /**
     * Método usado para realizar la impresión del producto Super chance
     */
    fun printSuperChance(printModel: BasePrintModel,
                         games: List<SuperchanceModel>,
                         isRetry: Boolean,
                         callback: (printerStatus: PrinterStatus) -> Unit?)
}