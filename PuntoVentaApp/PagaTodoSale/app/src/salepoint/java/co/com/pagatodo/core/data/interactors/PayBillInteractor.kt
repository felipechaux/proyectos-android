package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.RequestGetBillDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillRePrintDTO
import co.com.pagatodo.core.data.dto.request.ServiceSelectedDTO
import co.com.pagatodo.core.data.dto.response.ResponseAlcanosDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.print.PayBillPrintModel
import co.com.pagatodo.core.data.model.request.PayBillRequestBillModel
import co.com.pagatodo.core.data.model.response.PayBillResponseBillModel
import co.com.pagatodo.core.data.model.response.PayBillResponseModel
import co.com.pagatodo.core.data.model.response.PayBillResponseRePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IPayBillRepository
import co.com.pagatodo.core.di.DaggerPayBillComponent
import co.com.pagatodo.core.di.PayBillModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.resetValueFormat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface IPayBillInteractor {
    fun getAgreements(): Observable<PayBillResponseAgreementsModel>?
    fun getBill(serviceId: String, barCode: String): Observable<PayBillResponseBillModel>?
    fun payBill(requestPayBill: PayBillRequestBillModel): Observable<PayBillResponseModel>?
    fun print(printModel: PayBillPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun rePrintPayBill(accountNumber: String, collectionDate: String, teamVol: String, macAddress: String): Observable<PayBillResponseRePrintModel>?
}

class PayBillInteractor: IPayBillInteractor {

    @Inject
    lateinit var repository: IPayBillRepository

    init {
        DaggerPayBillComponent.builder()
            .payBillModule(PayBillModule())
            .build().inject(this)
    }

    override fun getAgreements(): Observable<PayBillResponseAgreementsModel>? {
        return repository.getAgreements()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayBillResponseAgreementsModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionTime = it.transactionHour
                    message = it.message
                    agreements = convertAgreementsDTOToModel(it.agreements)
                }
                Observable.just(response)
            }
    }

    override fun getBill(serviceId: String, barCode: String): Observable<PayBillResponseBillModel>? {
        val request = RequestGetBillDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = R_string(R.string.code_product_paybill)
            login = R_string(R.string.login_parameter)
            servicioSeleccionado = ServiceSelectedDTO().apply {
                companyIdService = serviceId
                this.barCode = barCode
            }
        }

        return repository.getBill(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayBillResponseBillModel().apply{
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionTime = it.transactionHour
                    transactionDate = it.transactionDate
                    message = it.message
                    bill = converBillDTOToModel(it.bill)
                }
                Observable.just(response)
            }
    }

    override fun payBill(requestPayBill: PayBillRequestBillModel): Observable<PayBillResponseModel>? {
        val request = RequestPayBillDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            login = getPreference(R_string(R.string.shared_key_seller_code))
            teamVol = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = R_string(R.string.code_product_paybill)
            bill = BillDTO().apply {
                serviceId = requestPayBill.serviceId
                expirationDate = requestPayBill.expirationDate
                billValue = requestPayBill.valueToPay
                valueToPay = requestPayBill.valueToPay
                userName = requestPayBill.userName
                flatIdBill = requestPayBill.flatIdBill
                isBillExpirated = requestPayBill.isBillExpirated
                billStatus = requestPayBill.billStatus
                accountNumber = requestPayBill.accountNumber
                payReference = requestPayBill.payReference
                barCode = requestPayBill.barCode
                billNumber = requestPayBill.billNumber
                companyId = requestPayBill.companyId
                billService = BillServiceDTO().apply {
                    serviceId = requestPayBill.serviceId
                    this.serviceName = requestPayBill.serviceName
                    patialPayValue = requestPayBill.valueToPay?.resetValueFormat()?.toInt()?:0
                    barCode = requestPayBill.flatIdBill
                    loadType = requestPayBill.loadType
                    isEntry = requestPayBill.isEntry
                    totalExpiredDays = requestPayBill.totalExpiredDays?:0

                }

            }
        }
        return repository.payBill(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayBillResponseModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    payBill = convertPayBillDTOToModel(it.payBill ?: PayBillDTO())
                }
                Observable.just(response)
            }
    }

    override fun print(
        printModel: PayBillPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        repository.print(printModel, isRetry, callback)
    }

    override fun rePrintPayBill(
        accountNumber: String,
        collectionDate: String,
        teamVol: String,
        macAddress: String
    ): Observable<PayBillResponseRePrintModel>? {
        val request = RequestPayBillRePrintDTO().apply {
            login = R_string(R.string.login_parameter)
            this.accountNumber = accountNumber
            this.collectionDate = collectionDate
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            this.teamVol = teamVol
            this.macAddress = macAddress
        }
        return repository.rePrint(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayBillResponseRePrintModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                }
                Observable.just(response)
            }
    }

    private fun convertPayBillDTOToModel(payBillDTO: PayBillDTO): PayBillModel? {
        return PayBillModel().apply {
            isSuccess = payBillDTO.isSuccess
            lstErrors = payBillDTO.lstErrors
            user = payBillDTO.user
            cityCode = payBillDTO.cityCode
            oficeCode = payBillDTO.oficeCode
            sellerCode = payBillDTO.sellerCode
            dateHour = payBillDTO.dateHour
            serviceName = payBillDTO.serviceName
            lstBills = convertListBillDTOToModel(payBillDTO.lstBills)
        }
    }

    private fun convertListBillDTOToModel(billsDTO: List<BillDTO>?): List<BillModel>? {
        val billList = mutableListOf<BillModel>()
        billsDTO?.forEach {
            billList.add(BillModel().apply {
                serviceId = it.serviceId
                nameService = ""
                userCode = ""
                payReference = it.payReference
                barCode = it.barCode
                expirationDate = it.expirationDate
                billValue = it.billValue
                valueToPay = it.valueToPay
                isBillExpirated = it.isBillExpirated
                userName = it.userName
                billNumber = it.billNumber
                description = it.description
                billStatus = it.billStatus
                accountNumber = it.accountNumber
                companyId = it.companyId
                companyNit = it.companyNit
                codeSeqBill = it.codeSeqBill
                collectionDate = it.collectionDate
                collectionHour = it.collectionHour
                voucherNumber = it.voucherNumber
                macAddress = it.macAddress
                teamVol = it.teamVol
                codePointSale = it.codePointSale
                sellerNumberDocument = it.sellerNumberDocument
                terminal = it.terminal
                flatIdBill = it.flatIdBill
                billService = convertBillServiceDTOToModel(it.billService ?: BillServiceDTO())
                alcanos = convertAlcanosDTOToModel(it.alcanos)
            })
        }
        return billList.toList()
    }

    private fun convertBillServiceDTOToModel(billService: BillServiceDTO): BillServiceModel? {
        return BillServiceModel().apply {
            companyIdService = billService.companyIdService
            serviceId = billService.serviceId
            serviceName = billService.serviceName
            serviceDescription = billService.serviceDescription
            serviceImage = billService.serviceImage
            regularExpression = billService.regularExpression
            isSelected = billService.isSelected
            isPartialPayment = billService.isPartialPayment
            isPercentage = billService.isPercentage
            isMinValue = billService.isMinValue
            isAcceptedBarCode = billService.isAcceptedBarCode
            isAcceptedPreviousLoadFac = billService.isAcceptedPreviousLoadFac
            isParameterizeSpecialClass = billService.isParameterizeSpecialClass
            patialPayValue = billService.patialPayValue.toString()
            totalExpiredDays = billService.totalExpiredDays
            formatIdBarcode = billService.formatIdBarcode
            nameBarCode = billService.nameBarCode
            specialClass = billService.specialClass
            fileLoadClass = billService.fileLoadClass
            generalFileClass = billService.generalFileClass
            status = billService.status
            loadType = billService.loadType
            printFragment = billService.printFragment
            agreementCode = billService.agreementCode
            barCode = billService.nameBarCode
            selectedCity = billService.selectedCity
            isEntry = billService.isEntry
        }
    }

    private fun convertBillModelToDTO(billModel: BillModel): BillDTO{
        return BillDTO().apply {
            payReference = billModel.payReference
            expirationDate = billModel.expirationDate
            billNumber = billModel.billNumber
            billValue = billModel.billValue
            valueToPay = billModel.valueToPay
            barCode = billModel.barCode
            voucherNumber = billModel.voucherNumber
            macAddress = billModel.macAddress
            teamVol = billModel.teamVol
            codePointSale = billModel.codePointSale
            terminal = billModel.terminal
        }
    }

    private fun converBillDTOToModel(billDTO: BillDTO?): BillModel? {
        return BillModel().apply {
            serviceId = billDTO?.serviceId
            nameService = billDTO?.nameService
            payReference = billDTO?.payReference
            barCode = billDTO?.barCode
            expirationDate = billDTO?.expirationDate
            billValue = billDTO?.billValue
            valueToPay = billDTO?.valueToPay
            isBillExpirated = billDTO?.isBillExpirated
            userName = billDTO?.userName
            billNumber = billDTO?.billNumber
            description = billDTO?.description
            billStatus = billDTO?.billStatus
            accountNumber = billDTO?.accountNumber
            companyId = billDTO?.companyId
            companyNit = billDTO?.companyNit
            codeSeqBill = billDTO?.codeSeqBill
            collectionDate = billDTO?.collectionDate
            collectionHour = billDTO?.collectionHour
            voucherNumber = billDTO?.voucherNumber
            macAddress = billDTO?.macAddress
            teamVol = billDTO?.teamVol
            codePointSale = billDTO?.codePointSale
            sellerNumberDocument = billDTO?.sellerNumberDocument
            terminal = billDTO?.terminal
            flatIdBill = billDTO?.flatIdBill
            billService = convertBillServiceDTOToModel(billDTO?.billService ?: BillServiceDTO())
            alcanos = convertAlcanosDTOToModel(billDTO?.alcanos)
        }
    }

    private fun convertAlcanosDTOToModel(alcanosDTO: ResponseAlcanosDTO?): AlcanosModel? {
        return AlcanosModel().apply {
            code = alcanosDTO?.code
            message = alcanosDTO?.message
            isSuccess = alcanosDTO?.isSuccess
        }
    }

    private fun convertAgreementsDTOToModel(agreements: List<AgreementDTO>?): List<AgreementModel>? {
        val listAgreements = mutableListOf<AgreementModel>()
        agreements?.forEach {
            listAgreements.add(AgreementModel().apply {
                agreementId = it.agreementId
                description = convertDescriptionDTOToModel(it.description)
            })
        }
        return listAgreements.toList()
    }

    private fun convertDescriptionDTOToModel(description: DescriptionAgreementDTO?): DescriptionAgreementModel? {
        return DescriptionAgreementModel().apply {
            idCompanyService = description?.idCompanyService
            nameService = description?.nameService
            sizeService = description?.sizeService
            isRequired = description?.isRequired
            indexService = description?.indexService
            barcode = description?.barcode
            startLabel = description?.startLabel
            company = convertCompanyAgreementDTOToModel(description?.company)
            serviceId = description?.serviceId
            loadTypeService = description?.loadTypeService
            macAddress = description?.macAddress
            userType = description?.userType
            volEquip = description?.volEquip
        }
    }

    private fun convertCompanyAgreementDTOToModel(company: CompanyAgreementDTO?): CompanyAgreementModel? {
        return CompanyAgreementModel().apply {
            companyId = company?.companyId
            companyName = company?.companyName
            companyDescription = company?.companyDescription
            companyDocument = company?.companyDocument
            companyAddress = company?.companyAddress
            companyPhone = company?.companyPhone
            companyFax = company?.companyFax
            affectBox = company?.affectBox
        }
    }
}