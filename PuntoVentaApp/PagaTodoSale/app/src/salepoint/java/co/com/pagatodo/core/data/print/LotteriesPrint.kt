package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter

class LotteriesPrint: BasePrint(), ILotteriesPrint {
    override fun printFractionsAvailable(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        printFractions(printModel, model, isRetry){
           callback(it)
        }
    }

    var numberFraction = 0

    override fun print(printModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        if (numberFraction < printModel.numbers?.size ?: 0){
            printFraction(printModel, numberFraction, isRetry){
                numberFraction += 1
                print(printModel, isRetry,callback)
            }
        }else{
            callback(PrinterStatus.OK)
        }
    }

    private fun printFraction(printModel: LotteriesPrintModel, numberFraction: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?){
        val stringBuilder = StringBuilder()
        val posPrinter = POSPrinter()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                val municipalityCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_municipality_code))
                val officeCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_office_code))
                val currentFraction = (printModel.numbers ?: arrayListOf())[numberFraction]
                val stub = "${currentFraction.serie1}${currentFraction.serie2}"
                val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))

                stringBuilder.append(printModel.lotteryName)
                stringBuilder.append("\n${getMaskedSellerCode()} $stub $municipalityCode $officeCode $codePointSale ${if(isRetry){
                    R_string(R.string.print_retry_icon)}else{""}}")
                stringBuilder.append("\n${getCurrentDate(DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH)}")
                stringBuilder.append("\n${R_string(R.string.print_check_equal_pdv)} ${printModel.digitChecked}")
                stringBuilder.append("\n${R_string(R.string.print_lottery_pdv)} ${printModel.draw}")
                stringBuilder.append("\nNUMERO \tSERIE \tFRACCION \tVALOR")
                stringBuilder.append("\n${currentFraction.number} \t${currentFraction.serie} \t${currentFraction.fraction} de ${printModel.fraction} \t\t$${formatValue((printModel.fractionValue ?: "").toDouble())}")

                stringBuilder.append("\n${R_string(R.string.print_lottery_date_pdv)} ${printModel.drawDate}")
                stringBuilder.append("\n${R_string(R.string.print_time_draw)}: ${printModel.drawHour}")

                val value = printModel.prize?.replace(".","")
                stringBuilder.append("\n${R_string(R.string.text_label_award)} $${formatValue(value!!.toDouble())}")

                stringBuilder.append("\n${R_string(R.string.print_guarded_super_health)}")
                stringBuilder.append("\n${R_string(R.string.print_awards_plan_valid_pdv)}")
                stringBuilder.append("\n${R_string(R.string.print_lottery_fraction_value)}\t\$${formatValue((printModel.fractionValue ?: "").toDouble())}")
                stringBuilder.append("\n\n\n")

                val target = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
                if (target.isNotEmpty()){
                    EpsonPrinter().print(stringBuilder.toString(), callback)
                }else{
                    posPrinter.print(stringBuilder.toString(), callback)
                }
            }
            else -> {}
        }
    }


    private fun printFractions(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?){
        val userCode : String = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
        val terminalCode: String = DeviceUtil.getSerialNumber()?:""
        val stringBuilder = StringBuilder()
        val posPrinter = POSPrinter()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                stringBuilder.append("PLANILLA ASIGNACION DE LOTERIAS")
                stringBuilder.append("\n Fecha: ${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}")
                stringBuilder.append("\n ${R_string(R.string.print_seller_equal_upper)} ${userCode}   ${R_string(R.string.print_machine_label)}${terminalCode}")
                stringBuilder.append("\n ${R_string(R.string.text_lottery)} ${model.fullName}")
                stringBuilder.append("\n ${R_string(R.string.print_lottery_pdv)} ${model.draw}")
                stringBuilder.append("\n ${R_string(R.string.print_lottery_price_fraction)} ${"$"}${formatValue(model.fractionValue!!.toDouble())}")
                stringBuilder.append("\n\t\tFRACCION \t\t\t\t\t\tSERIE \t\t\t\t\t\t\t\t\tNUMERO")
                printModel.tickets?.forEach{

                    stringBuilder.append("\n\t\t\t\t\t\t\t\t\t\t${it.fraction} \t\t\t\t\t\t\t\t\t\t\t\t\t\t${it.serie} \t\t\t\t\t\t\t\t\t\t\t\t\t${it.number}")
                }

                val sumFractionSlopes = printModel.tickets?.map { it.fraction?.toInt() }!!.sumBy { it!! }
                stringBuilder.append("\n\n\n ${R_string(R.string.print_fractions_slopes)} ${sumFractionSlopes}")

                val linePositionsToCenter = arrayOf(0)

                posPrinter.printWithAlignment(stringBuilder.toString(),linePositionsToCenter, callback)
            }
            else -> {}
        }
    }

}