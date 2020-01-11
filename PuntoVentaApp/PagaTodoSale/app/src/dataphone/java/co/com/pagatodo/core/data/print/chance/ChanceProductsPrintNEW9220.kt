package co.com.pagatodo.core.data.print.chance

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.BasePrint
import co.com.pagatodo.core.data.print.PrintManager
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreak
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreakHeaderPaper
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.pos.device.printer.PrintCanvas
import com.pos.device.printer.Printer

/**
 * Clase utilizada para realizar la impresión de los productos de chance solo en los datáfonos, está heredada de la interfaz IChanceProductsPrint
 * por ende se debe realizar la impresión por medio de estos productos
 */
class ChanceProductsPrintNEW9220: BasePrint(),
    IChanceProductsPrint {

    private val maxRowInChanceGame = 6

    /**
     * Método utilizado para completar con asteriscos el listado de productos de chance
     */
    private fun completeRowsOfChanceGames(chances: MutableList<String>, rowsData: String, maxNumberRows: Int = 6): List<String> {
        if (chances.count() < maxNumberRows) {
            val rest = maxNumberRows - chances.count()
            for (index in 0 until rest) {
                chances.add(rowsData)
            }
        }
        return chances
    }

    /**
     * Método utilizado para completar con asteriscos el listado de productos de chance y retornar la posición actual en Y
     */
    private fun completeRowsOfChanceGamesAndReturnLastPosY(chances: List<ChanceModel>, initPosY: Int, incrementPosY: Int, canvas: PrintCanvas, paint: Paint): Int {
        val rowCount = chances.count()
        var posY = initPosY
        if(rowCount < maxRowInChanceGame) {
            val rest = maxRowInChanceGame - rowCount
            setFontStyle(paint, 3, true)
            for (index in 1..rest) {
                canvas.setY(posY)
                canvas.drawText("****", paint)
                posY += incrementPosY
            }
        }
        return posY
    }

    /**
     * Método usado para obtener las apuestas realizadas en chance con el formato de impresión propuesto por el diseño
     */
    private fun getChancesGamesWithPrintFormat(chances: List<ChanceModel>): List<String> {
        var response = arrayListOf<String>()
        chances.forEach {
            val number = it.number?.padStart(4, '*')
            val direct = it.direct?.padStart(5, '*')
            val combined = it.combined?.padStart(5, '*')
            val paw = it.paw?.padStart(5, '*')
            val nail = it.nail?.padStart(5, '*')
            var chanceString = "$number         $direct         $combined         $paw        $nail"
            response.add(chanceString)
        }
        return completeRowsOfChanceGames(response, "****")
    }

    /**
     * Método usado para obtener las apuestas realizadas en maxichance con el formato de impresión propuesto por el diseño
     */
    private fun getMaxiChancesGamesWithPrintFormat(chances: List<SuperchanceModel>): List<String> {
        val response = arrayListOf<String>()
        chances.forEach {
            val number = it.number.padStart(4, '*')
            val direct = it.value.padStart(5, ' ')
            val zero = "0"
            val chanceString = "$number       $direct          $zero            $zero             $zero"
            response.add(chanceString)
        }
        return completeRowsOfChanceGames(response, "****")
    }

    /**
     * Método usado para obtener las apuestas realizadas en super chance con el formato de impresión propuesto por el diseño
     */
    private fun getSuperchanceGamesWithPrintFormat(chances: List<SuperchanceModel>): List<String> {
        val response = arrayListOf<String>()
        val fill = "*****"
        chances.forEach {
            val number = it.number.padStart(4, '*')
            val value = it.value.padStart(5, ' ')
            val chanceString = "$number       $value          $fill          $fill"
            response.add(chanceString)
        }
        return completeRowsOfChanceGames(response, "****", 5)
    }

    /**
     * Método usado para obtener las apuestas realizadas en pagamillonario con el formato de impresión propuesto por el diseño
     */
    private fun getPaymillionaireChancesGamesWithPrintFormat(chances: List<ChanceModel>): List<String> {
        val response = arrayListOf<String>()
        chances.forEach {
            val number = it.number?.padStart(4, '*') ?: "****"
            response.add(number)
        }
        return response
    }

    /**
     * Método usado para realizar la impresión en datáfono del producto superchance en datáfono
     */
    override fun printSuperChance(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        // Inicialización del canvas propuesta en la libreria de datáfono para realizar la impresión
        val canvas = PrintCanvas()
        val paint = TextPaint()
        // Espacio para el header en la impresión
        addLineBreakHeaderPaper(canvas, paint)

        // Asignación del tamaño y tipo de fuente que llevará la impresión
        setFontStyle(paint, 2, true)
        // Obtención de las loterías en un string con el formato de impresión
        val stringLotteries = getSuperChanceLotteriesWithPrintFormat(printModel.lotteries)
        val arrayChanceString = getSuperchanceGamesWithPrintFormat(games)

        printHeader(printModel, canvas, paint, isRetry)
        val posYDinamic = 215
        canvas.setX(0)
        canvas.setY(posYDinamic)
        canvas.drawText("${R_string(R.string.print_superchance_name)} < $stringLotteries >", paint)

        var posYAfterSuperchanceName = posYDinamic + 45
        if((printModel.lotteries?.count() ?: 0) <= 2){
            posYAfterSuperchanceName -= 20
        }

        canvas.setY(posYAfterSuperchanceName)
        canvas.drawText("${R_string(R.string.print_header_superchance_games)}", paint)

        var posYAfterHeaderSuperchanceGames = posYAfterSuperchanceName + 30
        arrayChanceString.forEach {
            val array: List<String>
            if (!it.equals("****")){
                array = it.split("       ")
                val number = array.get(0)
                val values = array.get(1) + "       " + array.get(2) + "       " + array.get(3)
                var posX = 0

                canvas.setY(posYAfterHeaderSuperchanceGames)
                canvas.setX(posX)
                setFontStyle(paint, 3, true)
                canvas.drawText(number, paint)

                posX += 100
                canvas.setX(posX)
                canvas.setY(posYAfterHeaderSuperchanceGames + 10)
                setFontStyle(paint, 2, false)
                canvas.drawText( values, paint)

            }else{
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textSize = 32f
                canvas.setY(posYAfterHeaderSuperchanceGames)
                canvas.drawText(it, paint)
            }
            posYAfterHeaderSuperchanceGames += 30
        }

        setFontStyle(paint, 2, true)
        var posYValueBetting = posYAfterHeaderSuperchanceGames + 5
        canvas.drawText("${R_string(R.string.print_bet_equal)}$${printModel.totalValueBetting?.toInt()}  Iva(${printModel.iva}%):$${printModel.totalValueIva?.toInt()}", paint)
        canvas.drawText("${R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()} ${R_string(R.string.print_over_equal)}$${printModel.totalValueOverloaded?.toInt()}", paint)

        posYValueBetting += 25
        canvas.drawText("${R_string(R.string.print_terms_of_contract)}", paint)
        posYValueBetting += 25
        canvas.drawText("${R_string(R.string.print_phone)}", paint)
        PrintManager.addLineBreak(canvas, paint, 2)
        PrintManager.print(canvas, callback)
    }

    override fun printMaxiChance(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val canvas = PrintCanvas()
        val paint = Paint()
        addLineBreakHeaderPaper(canvas, paint)
        val stringLotteries = getLotteriesWithPrintFormat(printModel.lotteries)
        val arrayChanceString = getMaxiChancesGamesWithPrintFormat(games)

        setFontStyle(paint, 2, true)
        printHeader(printModel, canvas, paint, isRetry)
        canvas.drawText("${R_string(R.string.print_lottery_equal)} $stringLotteries", paint)

        val posYDinamic = 250
        canvas.setX(0)
        canvas.drawText("                      ${R_string(R.string.print_maxichance_name)}", paint)
        canvas.drawText(R_string(R.string.print_header_maxi_chance_games), paint)

        var posYHeaderChanceGames = posYDinamic + 24
        arrayChanceString.forEach {
            val array: List<String>
            if (!it.equals("****")){
                array = it.split("       ")
                val number = array.get(0)
                val values = array.get(1) + "       " + array.get(2) + "       " + array.get(3)+ "       " + array.get(4)
                var posX = 0

                canvas.setY(posYHeaderChanceGames)
                canvas.setX(posX)
                setFontStyle(paint, 3, true)
                canvas.drawText(number, paint)

                posX += 100
                canvas.setY(posYHeaderChanceGames + 5)
                canvas.setX(posX)
                setFontStyle(paint, 2, false)
                canvas.drawText(values, paint)

            }else{
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textSize = 32f
                canvas.setY(posYHeaderChanceGames)
                canvas.drawText(it, paint)
            }

            posYHeaderChanceGames += 25
        }

        setFontStyle(paint, 2, true)
        var posYValueBetting = posYHeaderChanceGames + 20
        canvas.setY(posYValueBetting)
        canvas.drawText("${R_string(R.string.print_bet_equal)}$${printModel.totalValueBetting?.toInt()}  Iva(${printModel.iva}%):$${printModel.totalValueIva?.toInt()}", paint)
        posYValueBetting += 25
        canvas.setY(posYValueBetting)
        canvas.drawText("${R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()}  ${R_string(R.string.print_over_equal)}$${printModel.totalValueOverloaded?.toInt()}", paint)
        posYValueBetting += 35
        canvas.setY(posYValueBetting)
        canvas.drawText("${R_string(R.string.print_maxichance_name)}", paint)

        var posYFooter = posYValueBetting + 20
        canvas.setY(posYFooter)
        canvas.drawText("${R_string(R.string.print_terms_of_contract)}", paint)
        posYFooter += 25
        canvas.setY(posYFooter)
        canvas.drawText("${R_string(R.string.print_phone)}", paint)
        PrintManager.addLineBreak(canvas, paint,2)
        PrintManager.print(canvas, callback)
    }

    override fun printPaymillionaire(
        totalValueBetting: Double,
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
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        when(PrintManager.checkPrinterStatus()) {
            Printer.PRINTER_OK -> {
                val stringLotteries = getLotteriesWithPrintFormat(lotteries)
                val arrayNumbersString = getPaymillionaireChancesGamesWithPrintFormat(chances)
                val digits = chances.get(0).number?.trim()?.length ?: 0

                val canvas = PrintCanvas()
                val paint = Paint()
                addLineBreakHeaderPaper(canvas, paint)
                setFontStyle(paint, 2, true)
                canvas.drawText("               ${getCurrentDate()}", paint)
                canvas.drawText("${R_string(R.string.print_stub_equal)}  $stubs${if(isRetry){
                    R_string(R.string.print_retry_icon)}else{""}}", paint)
                canvas.drawText("${R_string(R.string.print_unique_serie_equal)} $uniqueSerial", paint)
                canvas.drawText("${R_string(R.string.print_check_equal)} $digitChecking", paint)
                canvas.drawText("${R_string(R.string.print_seller_equal_upper)} $sellerName     M: ${getDeviceId()}", paint)
                canvas.drawText("${R_string(R.string.print_raffle_date_equal)} $raffleDate", paint)
                canvas.drawText("${R_string(R.string.print_paymillionaire)} <$stringLotteries>", paint)
                canvas.drawText("<${digits}C>", paint)

                var posYDinamic = 270
                if (lotteries.count() > 2){
                    posYDinamic = 300
                }

                val incrementPosYNumberAndPrizePlan = 30
                var sideChaceNumbersPosY = posYDinamic
                canvas.setY(sideChaceNumbersPosY)
                canvas.drawText(RUtil.R_string(R.string.print_header_paymillionaire_numbers), paint)
                sideChaceNumbersPosY += incrementPosYNumberAndPrizePlan

                setFontStyle(paint, 3, true)
                arrayNumbersString.forEach {
                    canvas.setY(sideChaceNumbersPosY)
                    canvas.drawText(it, paint)
                    sideChaceNumbersPosY += incrementPosYNumberAndPrizePlan
                }

                setFontStyle(paint, 2, true)
                //add custom x,y position
                var initPosYPrizePlan = posYDinamic - 40
                val posXPrizePlan = 110
                canvas.setY(initPosYPrizePlan)
                prizePlan.forEach {
                    canvas.setX(posXPrizePlan)
                    canvas.setY(initPosYPrizePlan)
                    canvas.drawText(it, paint)
                    initPosYPrizePlan += incrementPosYNumberAndPrizePlan
                }

                // Accumulated
                val initPosYAccumulated = initPosYPrizePlan + 80
                canvas.setY(initPosYAccumulated)
                canvas.drawText("${R_string(R.string.print_accumulated_equal)}$${selectedModeValue?.accumulated}", paint)

                // Bet Values
                val incrementY = 20
                val initPosYBetValues = initPosYAccumulated + incrementY
                canvas.setY(initPosYBetValues)
                canvas.drawText("${RUtil.R_string(R.string.print_bet_equal)}$${totalValueBetting.toInt()}  Iva($iva%):$${totalValueIva.toInt()}  Nt:$${totalValuePaid.toInt()}", paint)

                val footerPosY = initPosYBetValues + incrementY
                val incrementFooterPosY = 20
                canvas.setY(footerPosY)
                canvas.drawText("${RUtil.R_string(R.string.print_terms_of_contract)}", paint)
                canvas.setY(footerPosY + incrementFooterPosY)
                canvas.drawText("${RUtil.R_string(R.string.print_phone)}", paint)
                addLineBreak(canvas, paint,2)
                PrintManager.print(canvas, callback)
            }
            else -> {
                callback(PrinterStatus.BUSY)
            }
        }
    }

    private fun printChanceGamesAndGetLastPosY(chances: List<ChanceModel>, posYChanceGames: Int, canvas: PrintCanvas, paint: Paint): Int {
        var posY = posYChanceGames
        val xNumber = 0
        val xDirect = 75
        val xPaw = xDirect + 80
        val xCombined = xPaw + 75
        val xNail = xCombined + 75

        chances.forEach {

            var direct = "0"
            it.direct?.trim()?.let { direct = if(it.isEmpty()) "0" else it }

            var paw = "0"
            it.paw?.trim()?.let { paw = if(it.isEmpty()) "0" else it }

            var combined = "0"
            it.combined?.trim()?.let { combined = if(it.isEmpty()) "0" else it }

            var nail = "0"
            it.nail?.trim()?.let { nail = if(it.isEmpty()) "0" else it }

            canvas.setY(posY)
            canvas.setX(xNumber)
            setFontStyle(paint, 3, true)
            canvas.drawText("${it.number?.padStart(4, '*')}", paint)

            setFontStyle(paint, 2, false)
            canvas.setY(posY + 5)
            canvas.setX(xDirect)
            canvas.drawText("${direct.padStart(5, ' ')}", paint)

            canvas.setY(posY + 5)
            canvas.setX(xPaw)
            canvas.drawText("${paw.padStart(5, ' ')}", paint)

            canvas.setY(posY + 5)
            canvas.setX(xCombined)
            canvas.drawText("${combined.padStart(5, ' ')}", paint)

            canvas.setY(posY + 5)
            canvas.setX(xNail)
            canvas.drawText("${nail.padStart(5, ' ')}", paint)

            posY += 30
        }
        return completeRowsOfChanceGamesAndReturnLastPosY(chances, posY, 20, canvas, paint)
    }

    /**
     * Método utlizado para realizar la impresión del producto chance
     */
    override fun printChance(totalValueBetting: Double, totalValuePaid: Double, totalValueOverloaded: Double, totalValueIva: Double, raffleDate: String,raffleHour:String, lotteries: List<LotteryModel>,
                             chances: List<ChanceModel>, iva: Int, sellerName: String, uniqueSerial: String, digitChecking: String, stubs: String,maxRows: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        // Validación del estado de la impresora
        when(PrintManager.checkPrinterStatus()) {
            Printer.PRINTER_OK -> {
                // Obtención de las loterías en un string con el formato de impresión
                val stringLotteries = getLotteriesWithPrintFormat(lotteries)

                // Inicialización del canvas para realizar la impresión
                val canvas = PrintCanvas()
                val paint = Paint()

                // Espacio asignado para el header del tiquete
                addLineBreakHeaderPaper(canvas, paint)

                // Asignación del tamaño y tipo de fuente de la impresión
                setFontStyle(paint, 2, true)
                // Asignación y ajuste de los textos que se van a imprimir
                canvas.drawText("               ${getCurrentDate()}", paint)
                canvas.drawText("${R_string(R.string.print_stub_equal)}  $stubs${if(isRetry){
                    R_string(R.string.print_retry_icon)}else{""}}", paint)
                canvas.drawText("${R_string(R.string.print_unique_serie_equal)} $uniqueSerial", paint)
                canvas.drawText("${R_string(R.string.print_check_equal)} $digitChecking", paint)
                canvas.drawText("${R_string(R.string.print_seller_equal_upper)} $sellerName     M: ${getDeviceId()}", paint)
                canvas.drawText("${R_string(R.string.print_raffle_date_equal)} $raffleDate", paint)
                canvas.drawText("$stringLotteries", paint)

                var posYDinamic = 230
                if (lotteries.count() > 4){
                    posYDinamic += 20
                }

                if (lotteries.count() > 8){
                    posYDinamic += 20
                }

                // Ajuste de la posición en Y de las siguientes líneas para imprimir,
                // este valor debe cambiar cada vez que se vaya a imprimir una nueva línea
                canvas.setY(posYDinamic)
                canvas.drawText(R_string(R.string.print_header_chance_games), paint)
                val posYChanceGames = posYDinamic + 25
                setFontStyle(paint, 3, true)
                var posYBet = printChanceGamesAndGetLastPosY(chances, posYChanceGames, canvas, paint) + 20

                setFontStyle(paint, 2, true)
                canvas.setY(posYBet)
                canvas.drawText("${R_string(R.string.print_bet_equal)}$${totalValueBetting.toInt()} Iva($iva):$${totalValueIva.toInt()}", paint)
                posYBet += 25
                canvas.setY(posYBet)
                canvas.drawText("${R_string(R.string.print_pay_equal)}$${totalValuePaid.toInt()} ${R_string(R.string.print_over_equal)}$${totalValueOverloaded.toInt()}", paint)
                var posYFooter = posYBet + 25

                if (lotteries.count() >= 10){
                    posYFooter -= 5
                }

                canvas.setY(posYFooter)
                canvas.drawText("${R_string(R.string.print_terms_of_contract)}", paint)
                posYFooter += 25
                canvas.setY(posYFooter)
                canvas.drawText("${R_string(R.string.print_phone)}", paint)

                var lines = if (lotteries.count() > 4){
                    3
                }else{
                    5
                }

                if (chances.count() >= 3) {
                    lines -= 1
                }

                // Impresión de una línea en blanco
                addLineBreak(canvas, paint, lines)
                // Se envía el canvas a la impresora
                PrintManager.print(canvas, callback)
            }
        }
    }
}