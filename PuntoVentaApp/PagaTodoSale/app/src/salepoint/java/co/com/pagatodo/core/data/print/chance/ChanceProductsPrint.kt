package co.com.pagatodo.core. data.print.chance

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.BasePrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.PrinterStatusInfo
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter

class ChanceProductsPrint: BasePrint(), IChanceProductsPrint {

    private var maxRows = 0
    val stringBuilder = StringBuilder()
    var posPrinter = POSPrinter()

    override fun printChance(
        totalValueBetting: Double,
        totalValuePaid: Double,
        totalValueOverloaded: Double,
        totalValueIva: Double,
        raffleDate: String,
        raffleHour: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        iva: Int,
        sellerName: String,
        uniqueSerial: String,
        digitChecking: String,
        stubs: String,
        maxRows: Int,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        stringBuilder.clear()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                val stringLotteries = getLotteriesWithPrintFormat(lotteries)
                val municipalityCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_municipality_code))
                val officeCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_office_code))
                val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))

                stringBuilder.append(stringLotteries)
                stringBuilder.append("\n${R_string(R.string.print_unique_serie_equal_pdv)} $uniqueSerial")
                stringBuilder.append("\n${getMaskedSellerCode()} ${stubs.replace("\\s".toRegex(), "")} $municipalityCode $officeCode $codePointSale ${if(isRetry){R_string(R.string.print_retry_icon)}else{""}}")
                stringBuilder.append("\n"+ "$raffleDate $raffleHour"
                        +" ${R_string(R.string.print_raffle_date_equal_pdv)} $raffleDate")
                stringBuilder.append("\n${R_string(R.string.print_check_equal_pdv)} $digitChecking  M: ${getDeviceId()}")
                stringBuilder.append("\nNUMERO\tDIRECTO\tCOMBI\tPATA\tUNA")

                chances.forEach{
                    val number = it.number?.trim()
                    val direct ="$"+getChanceValues(it.direct)
                    val combined ="$"+getChanceValues(it.combined)
                    val paw ="$"+getChanceValues(it.paw)
                    val nail ="$"+getChanceValues(it.nail)

                    stringBuilder.append("\n${number}\t${direct}\t${combined}\t${paw}\t${nail}")
                }

                val rowCounter = chances.count()
                completeRows(maxRows, rowCounter)

                stringBuilder.append("\nNet:$${totalValueBetting.toInt()} IVA:$${totalValueIva.toInt()} Tot:$${totalValuePaid.toInt()}")
                stringBuilder.append("\n${R_string(R.string.print_over_equal_enc)}$${totalValueOverloaded.toInt()} \n${R_string(R.string.print_terms_of_contract)} ")
                stringBuilder.append("\n${R_string(R.string.print_phone_pdv)}")
                if(lotteries.count()<=5){
                    stringBuilder.append("\n")
                }

                print(stringBuilder.toString(), callback)
            }
            else -> {}
        }
    }

    override fun printSuperChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        stringBuilder.clear()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                val stringLotteries = getLotteriesWithPrintFormat(printModel.lotteries)
                val municipalityCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_municipality_code))
                val officeCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_office_code))
                val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))
                val dateData = printModel.raffleDate!!.split("|")

                stringBuilder.append(stringLotteries)
                stringBuilder.append("\n${R_string(R.string.print_unique_serie_equal_pdv)} ${printModel.uniqueSerial}")
                stringBuilder.append("\n${getMaskedSellerCode()} ${printModel.stubs?.replace("\\s".toRegex(), "")} $municipalityCode $officeCode $codePointSale ${if(isRetry){R_string(R.string.print_retry_icon)}else{""}}")
                stringBuilder.append("\n${dateData[0]} ${dateData[1]} " +
                        "${R_string(R.string.print_raffle_date_equal_pdv)} ${dateData[2]}")
                stringBuilder.append("\n${R_string(R.string.print_check_equal_pdv)} ${printModel.digitChecking}  M: ${getDeviceId()}")
                stringBuilder.append("\nNUMERO\tDIRECTO\tCOMBI\tPATA\tUNA")

                maxRows = 5
                getChancePrintingFormat(games, true)

                stringBuilder.append("\nApuesta:$${printModel.totalValueBetting?.toInt()} IVA(${printModel.iva}%): $${printModel.totalValueIva?.toInt()}")
                stringBuilder.append("\nPagado:$${printModel.totalValuePaid?.toInt()} ${R_string(R.string.print_over_equal)}\$${printModel.totalValueOverloaded?.toInt()}")
                stringBuilder.append("\n${R_string(R.string.print_terms_of_contract)}")
                stringBuilder.append("\n${R_string(R.string.print_phone_pdv)}")

                if(printModel.lotteries!!.size <=6){
                    stringBuilder.append("\n")
                }

                print(stringBuilder.toString(), callback)
            }
            else -> {}
        }
    }

    override fun printMaxiChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        stringBuilder.clear()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                val stringLotteries = getLotteriesWithPrintFormat(printModel.lotteries)
                val municipalityCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_municipality_code))
                val officeCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_office_code))
                val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))


                val dateData = printModel.raffleDate!!.split("|")

                stringBuilder.append("${R_string(R.string.print_maxichance_name)} $stringLotteries")
                stringBuilder.append("\n${R_string(R.string.print_unique_serie_equal_pdv)} ${printModel.uniqueSerial}")
                stringBuilder.append("\n${getMaskedSellerCode()} ${printModel.stubs?.replace("\\s".toRegex(), "")} $municipalityCode $officeCode $codePointSale ${if(isRetry){R_string(R.string.print_retry_icon)}else{""}}")
                stringBuilder.append("\n${dateData[0]} ${dateData[1]} "+
                        "${R_string(R.string.print_raffle_date_equal_pdv)} ${dateData[2]}")
                stringBuilder.append("\n${R_string(R.string.print_check_equal)} ${printModel.digitChecking}  M: ${getDeviceId()}")
                stringBuilder.append("\nNUMERO\tDIRECTO\tCOMBI\tPATA\tUNA")

                maxRows = 4
                getChancePrintingFormat(games, false)

                stringBuilder.append("\nApuesta:$${printModel.totalValueBetting?.toInt()} IVA(${printModel.iva}%): $${printModel.totalValueIva?.toInt()}")
                stringBuilder.append("\nPagado:$${printModel.totalValuePaid?.toInt()} ${R_string(R.string.print_over_equal)}\$${printModel.totalValueOverloaded?.toInt()}")
                stringBuilder.append("\n${R_string(R.string.print_terms_of_contract)}")
                stringBuilder.append("\n${R_string(R.string.print_phone_pdv)}")
                stringBuilder.append("\n\n")

                print(stringBuilder.toString(), callback)
            }
            else -> {}
        }
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
        stringBuilder.clear()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                val stringLotteries = getLotteriesWithPrintFormat(lotteries)
                val municipalityCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_municipality_code))
                val officeCode = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_office_code))
                val digits = chances[0].number?.trim()?.length ?: 0
                val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))

                val dateData = raffleDate.split("|")

                stringBuilder.append("${R_string(R.string.print_paymillionaire)} [${digits}C] <$stringLotteries>")
                stringBuilder.append("\n${dateData[0]} ${dateData[1]} " +
                        "${R_string(R.string.print_raffle_date_equal_pdv)} ${dateData[2]}")
                stringBuilder.append("\n${getMaskedSellerCode()} ${stubs.replace("\\s".toRegex(), "")} $municipalityCode $officeCode $codePointSale ${if(isRetry){R_string(R.string.print_retry_icon)} else{""}}")
                stringBuilder.append("\n${R_string(R.string.print_unique_serie_equal_pdv)} $uniqueSerial")
                stringBuilder.append("\n${R_string(R.string.print_check_equal_pdv)} $digitChecking  M: ${getDeviceId()}")
                stringBuilder.append("\n${R_string(R.string.print_header_paymillionaire_numbers_pdv)} \t  ${prizePlan[0]}")

                var index = 0
                var number = ""
                var prizePlanText = ""

                while (index < chances.size){
                    number = chances[index].number!!
                    if(index == (prizePlan.size-1))
                        stringBuilder.append("\n$number \t  ${R_string(R.string.print_accumulated_equal)} ${selectedModeValue?.accumulated}")
                    else{
                        prizePlanText = prizePlan[index+1]
                        stringBuilder.append("\n$number \t  $prizePlanText")
                    }
                    index++
                }

                stringBuilder.append("\nApuesta: $${totalValueBetting.toInt()} IVA($iva%): $${totalValueIva.toInt()} Neto:$${totalValuePaid.toInt()}")
                stringBuilder.append("\n${R_string(R.string.print_terms_of_contract)}")
                stringBuilder.append("\n${R_string(R.string.print_phone_pdv)}")
                stringBuilder.append("\n          ")
                stringBuilder.append("\n          ")

                print(stringBuilder.toString(), callback)
            }
            else -> {}
        }
    }

    private fun print(data: String, callback: (printerStatus: PrinterStatus) -> Unit? = {}){
        val target = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
        if (target.isNotEmpty()){
            EpsonPrinter().print(stringBuilder.toString(), callback)
        }else{
            posPrinter.print(stringBuilder.toString(), callback)
        }
    }

    private fun getChanceValues(value: String?): String {
        value?.let {
            return if (it.isEmpty()) "0" else it
        } ?: run {
            return "0"
        }
    }

    private fun getChancePrintingFormat(game: List<SuperchanceModel>, isSuperChance: Boolean){
        game.forEachIndexed { index, it ->
            var number = "*"
            it.number.trim().let { number = if (it.length == 3) number.plus(it) else it}

            val value = it.value.trim()

            if(isSuperChance && index == 0)
                stringBuilder.append("\n${number}\t$${value}\t${R_string(R.string.print_superchance_name).toUpperCase()}")
            else if(isSuperChance)
                stringBuilder.append("\n${number}\t$${value}")
            else if(!isSuperChance)
                stringBuilder.append("\n${number}\t$${value}\t$0\t$0\t$0")
        }

        val rowCounter = game.count()
        completeRows(maxRows, rowCounter)
    }

    private fun completeRows(maxRows: Int, count: Int){
        val rest = maxRows - count
        for(index in 1..rest)
            stringBuilder.append("\n****")
    }
}