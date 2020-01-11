package co.com.pagatodo.core.data.print.chance

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
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
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import co.com.pagatodo.core.data.print.PrintManager.Companion.createBitmap
import co.com.pagatodo.core.data.print.PrintManager.Companion.createCanvas
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.PrinterStatus

class ChanceProductsPrint: BasePrint(), IChanceProductsPrint {
    private val maxRowInChanceGame = 6

    @RequiresApi(Build.VERSION_CODES.M)
    override fun printChance(
        totalValueBetting: Double,
        totalValuePaid: Double,
        totalValueOverloaded: Double,
        totalValueIva: Double,
        raffleDate: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        iva: Int,
        sellerName: String,
        uniqueSerial: String,
        digitChecking: String,
        stubs: String,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        val stringLotteries = getLotteriesWithPrintFormat(lotteries)

        setFontStyle(paint, 2, true)
        canvas.drawText(getCurrentDate(),60F,100F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_stub_equal)}  $stubs${if(isRetry){
            RUtil.R_string(R.string.print_retry_icon) }else{""}}",0F,125F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_unique_serie_equal)} $uniqueSerial",0F,150F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)} $digitChecking",0F,175F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_upper)} $sellerName     M: ${getDeviceId()}",0F,200F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_raffle_date_equal)} $raffleDate",0F,225F, paint)

        val staticLayout = StaticLayout.Builder
            .obtain(stringLotteries, 0, stringLotteries.length, paint, canvas.width)
            .build()

        canvas.translate(0F, 225F)
        staticLayout.draw(canvas)

        val posYDinamic = (staticLayout.lineCount * 30) + 20F
        canvas.drawText(RUtil.R_string(R.string.print_header_chance_games),0F, posYDinamic, paint)
        val posYChanceGames = posYDinamic + 30
        setFontStyle(paint, 3, true)
        var posYBet = printChanceGamesAndGetLastPosY(chances, posYChanceGames, canvas, paint) + 25

        setFontStyle(paint, 2, true)
        canvas.drawText(
            RUtil.R_string(R.string.print_bet_equal) +
                "$${totalValueBetting.toInt()} Iva($iva):$${totalValueIva.toInt()}",0F,posYBet, paint)
        posYBet += 25
        canvas.drawText("${RUtil.R_string(R.string.print_pay_equal)}$${totalValuePaid.toInt()} ${RUtil.R_string(
            R.string.print_over_equal
        )}$${totalValueOverloaded.toInt()}",0F, posYBet, paint)
        var posYFooter = posYBet + 25

        if (lotteries.count() >= 10){
            posYFooter -= 5
        }

        canvas.drawText(RUtil.R_string(R.string.print_terms_of_contract),0F,posYFooter, paint)
        posYFooter += 25
        canvas.drawText(RUtil.R_string(R.string.print_phone),0F, posYFooter, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

    private fun printChanceGamesAndGetLastPosY(chances: List<ChanceModel>, posYChanceGames: Float, canvas: Canvas, paint: Paint): Float {
        var posY = posYChanceGames
        val xNumber = 0
        val xDirect = 85
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

            setFontStyle(paint, 3, true)
            canvas.drawText("${it.number?.padStart(4, '*')}",xNumber.toFloat(),posY, paint)

            setFontStyle(paint, 2, false)
            canvas.drawText(direct.padStart(5, ' '),xDirect.toFloat(),(posY - 3), paint)

            canvas.drawText(paw.padStart(5, ' '),xPaw.toFloat(),(posY - 3), paint)

            canvas.drawText(combined.padStart(5, ' '), xCombined.toFloat(),(posY - 3), paint)

            canvas.drawText(nail.padStart(5, ' '),xNail.toFloat(),(posY - 3), paint)

            posY += 25
        }
        return completeRowsOfChanceGamesAndReturnLastPosY(chances, posY, 20, canvas, paint)
    }

    private fun completeRowsOfChanceGamesAndReturnLastPosY(chances: List<ChanceModel>, initPosY: Float, incrementPosY: Int, canvas: Canvas, paint: Paint): Float {
        val rowCount = chances.count()
        var posY = initPosY
        if(rowCount < maxRowInChanceGame) {
            val rest = maxRowInChanceGame - rowCount
            setFontStyle(paint, 3, true)
            for (index in 1..rest) {
                canvas.drawText("****",0F, posY.toFloat(), paint)
                posY += incrementPosY
            }
        }
        return posY
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
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        val stringLotteries = getLotteriesWithPrintFormat(lotteries)
        val arrayNumbersString = getPaymillionaireChancesGamesWithPrintFormat(chances)
        val digits = chances.get(0).number?.trim()?.length ?: 0

        setFontStyle(paint, 2, true)
        canvas.drawText(getCurrentDate(),60F,100F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_stub_equal)}  $stubs${if(isRetry){
            RUtil.R_string(R.string.print_retry_icon)
        }else{""}}",0F, 125F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_unique_serie_equal)} $uniqueSerial",0F, 150F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)} $digitChecking",0F,175F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_upper)} $sellerName     M: ${getDeviceId()}",0F,200F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_raffle_date_equal)} $raffleDate",0F, 225F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_paymillionaire)} <$stringLotteries>",0F, 250F, paint)
        canvas.drawText("<${digits}C>",0F,275F, paint)

        var posYDinamic = 300
        if (lotteries.count() > 2){
            posYDinamic = 325
        }

        val incrementPosYNumberAndPrizePlan = 30
        var sideChaceNumbersPosY = posYDinamic
        canvas.drawText(RUtil.R_string(R.string.print_header_paymillionaire_numbers),0F,sideChaceNumbersPosY.toFloat(), paint)
        sideChaceNumbersPosY += incrementPosYNumberAndPrizePlan

        arrayNumbersString.forEach {
            setFontStyle(paint, 3, true)
            canvas.drawText(it, 0F, sideChaceNumbersPosY.toFloat(), paint)

            sideChaceNumbersPosY += incrementPosYNumberAndPrizePlan
        }

        setFontStyle(paint, 2, true)
        //add custom x,y position
        var initPosYPrizePlan = posYDinamic - 25
        val posXPrizePlan = 110
        prizePlan.forEach {
            canvas.drawText(it,posXPrizePlan.toFloat(), initPosYPrizePlan.toFloat(), paint)
            initPosYPrizePlan += incrementPosYNumberAndPrizePlan - 5
        }

        // Accumulated
        val initPosYAccumulated = initPosYPrizePlan + 75
        canvas.drawText("${RUtil.R_string(R.string.print_accumulated_equal)}$${selectedModeValue?.accumulated}",0F, initPosYAccumulated.toFloat(), paint)

        // Bet Values
        val incrementY = 20
        val initPosYBetValues = initPosYAccumulated + incrementY
        canvas.drawText("${RUtil.R_string(R.string.print_bet_equal)}$${totalValueBetting.toInt()}  Iva($iva%):$${totalValueIva.toInt()}  Nt:$${totalValuePaid.toInt()}", 0F, initPosYBetValues.toFloat(), paint)

        val footerPosY = initPosYBetValues + incrementY
        val incrementFooterPosY = 20
        canvas.drawText("${RUtil.R_string(R.string.print_terms_of_contract)}",0F,footerPosY.toFloat(), paint)
        canvas.drawText("${RUtil.R_string(R.string.print_phone)}",0F, (footerPosY + incrementFooterPosY).toFloat(), paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

    private fun getPaymillionaireChancesGamesWithPrintFormat(chances: List<ChanceModel>): List<String> {
        val response = arrayListOf<String>()
        chances.forEach {
            val number = it.number?.padStart(4, '*') ?: "****"
            response.add(number)
        }
        return completeRowsOfChanceGames(response, "****",5)
    }

    private fun completeRowsOfChanceGames(chances: MutableList<String>, rowsData: String, maxNumberRows: Int = 6): List<String> {
        if (chances.count() < maxNumberRows) {
            val rest = maxNumberRows - chances.count()
            for (index in 0 until rest) {
                chances.add(rowsData)
            }
        }
        return chances
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun printMaxiChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        val stringLotteries = getLotteriesWithPrintFormat(printModel.lotteries)
        val arrayChanceString = getMaxiChancesGamesWithPrintFormat(games)

        setFontStyle(paint, 2, true)
        val posYHeader = printHeader(printModel, canvas, paint, isRetry)

        val text = "${RUtil.R_string(R.string.print_lottery_equal)} $stringLotteries"
        val staticLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, canvas.width)
            .build()

        canvas.translate(0F, (posYHeader-25))
        staticLayout.draw(canvas)

        val posYDinamic = (staticLayout.lineCount * 30) + 20F
        canvas.drawText("                      ${RUtil.R_string(R.string.print_maxichance_name)}",0F,posYDinamic, paint)

        var posYHeaderChanceGames = posYDinamic + 25
        canvas.drawText(RUtil.R_string(R.string.print_header_chance_games),0F, posYHeaderChanceGames, paint)

        posYHeaderChanceGames += 25
        arrayChanceString.forEach {
            val array: List<String>
            if (!it.equals("****")){
                array = it.split("       ")
                val number = array.get(0)
                val values = array.get(1) + "       " + array.get(2) + "       " + array.get(3)+ "       " + array.get(4)
                var posX = 0F

                setFontStyle(paint, 3, true)
                canvas.drawText(number,posX,posYHeaderChanceGames, paint)

                posX += 100
                setFontStyle(paint, 2, false)
                canvas.drawText(values, posX, posYHeaderChanceGames, paint)

            }else{
                canvas.drawText(it,0F, posYHeaderChanceGames, paint)
            }

            posYHeaderChanceGames += 25
        }

        setFontStyle(paint, 2, true)
        var posYValueBetting = posYHeaderChanceGames + 20
        canvas.drawText("${RUtil.R_string(R.string.print_bet_equal)}$${printModel.totalValueBetting?.toInt()}  " +
                "Iva(${printModel.iva}):$${printModel.totalValueIva?.toInt()}", 0F, posYValueBetting, paint)
        posYValueBetting += 25
        canvas.drawText("${RUtil.R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()} " +
                " ${RUtil.R_string(R.string.print_over_equal)}$${printModel.totalValueOverloaded?.toInt()}",0F,posYValueBetting, paint)
        posYValueBetting += 30
        canvas.drawText("${RUtil.R_string(R.string.print_maxichance_name)}",0F,posYValueBetting, paint)

        var posYFooter = posYValueBetting + 20
        canvas.drawText("${RUtil.R_string(R.string.print_terms_of_contract)}",0F,posYFooter, paint)
        posYFooter += 25
        canvas.drawText("${RUtil.R_string(R.string.print_phone)}",0F,posYFooter, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

    private fun getMaxiChancesGamesWithPrintFormat(chances: List<SuperchanceModel>): List<String> {
        val response = arrayListOf<String>()
        chances.forEach {
            val number = it.number.padStart(4, '*')
            val direct = it.value.padStart(5, ' ')
            val zero = "0"
            val chanceString = "$number       $direct          $zero            $zero             $zero"
            response.add(chanceString)
        }
        return completeRowsOfChanceGames(response, "****",6)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun printSuperChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 2, true)
        val stringLotteries = getLotteriesWithPrintFormat(printModel.lotteries)
        val arrayChanceString = getSuperchanceGamesWithPrintFormat(games)

        val posYDinamic = printHeader(printModel, canvas, paint, isRetry)

        val text = "${RUtil.R_string(R.string.print_superchance_name)} < $stringLotteries >"
        val staticLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, canvas.width)
            .build()

        canvas.translate(0F, (posYDinamic-20))
        staticLayout.draw(canvas)

        val posYAfterSuperchanceName = (staticLayout.lineCount * 30) + 20F

        canvas.drawText("${RUtil.R_string(R.string.print_header_superchance_games)}",0f, posYAfterSuperchanceName, paint)

        var posYAfterHeaderSuperchanceGames = posYAfterSuperchanceName + 40
        arrayChanceString.forEach {
            val array: List<String>
            if (!it.equals("****")){
                array = it.split("       ")
                val number = array.get(0)
                val values = array.get(1) + "       " + array.get(2) + "       " + array.get(3)
                var posX = 0F

                setFontStyle(paint, 3, true)
                canvas.drawText(number,posX,posYAfterHeaderSuperchanceGames, paint)

                posX += 100
                setFontStyle(paint, 2, false)
                canvas.drawText(values,posX,(posYAfterHeaderSuperchanceGames), paint)

            }else{
                canvas.drawText(it,0F,posYAfterHeaderSuperchanceGames, paint)
            }
            posYAfterHeaderSuperchanceGames += 30
        }

        setFontStyle(paint, 2, true)
        var posYValueBetting = posYAfterHeaderSuperchanceGames + 5
        canvas.drawText("${RUtil.R_string(R.string.print_bet_equal)}$${printModel.totalValueBetting?.toInt()}  " +
                "Iva(${printModel.iva}):$${printModel.totalValueIva?.toInt()}",0F, posYValueBetting, paint)

        posYValueBetting += 20
        canvas.drawText("${RUtil.R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()} ${RUtil.R_string(
            R.string.print_over_equal
        )}$${printModel.totalValueOverloaded?.toInt()}",0F, posYValueBetting, paint)

        posYValueBetting += 20
        canvas.drawText("${RUtil.R_string(R.string.print_terms_of_contract)}",0F,posYValueBetting, paint)
        posYValueBetting += 20
        canvas.drawText("${RUtil.R_string(R.string.print_phone)}",0F,posYValueBetting, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

    private fun getSuperchanceGamesWithPrintFormat(chances: List<SuperchanceModel>): List<String> {
        val response = arrayListOf<String>()
        val fill = "*****"
        chances.forEach {
            val number = it.number.padStart(4, '*')
            val value = it.value.padStart(5, ' ')
            val chanceString = "$number       $value          $fill          $fill"
            response.add(chanceString)
        }
        return completeRowsOfChanceGames(response, "****",6)
    }

    private fun printHeader(printModel: BasePrintModel, canvas: Canvas, paint: Paint, isRetry:Boolean): Float {
        setFontStyle(paint, 2, true)
        canvas.drawText("${getCurrentDate()}",60F,100F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_stub_equal)}  ${printModel.stubs}${if(isRetry){
            RUtil.R_string(R.string.print_retry_icon)
        }else{""}}",0F, 125F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_unique_serie_equal)} ${printModel.uniqueSerial}",0F,150F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)} ${printModel.digitChecking}",0F,175F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_upper)} ${printModel.consultant}     M: ${getDeviceId()}",0F,200F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_raffle_date_equal)} ${printModel.raffleDate}",0F,225F, paint)

        return 250F
    }
}