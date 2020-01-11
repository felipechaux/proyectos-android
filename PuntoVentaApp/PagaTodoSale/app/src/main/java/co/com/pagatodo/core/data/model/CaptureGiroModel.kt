package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.response.ResponseThirdDTO

class CaptureGiroModel {

    var value: Int? = null
    var includesFreight: Boolean? = null
    var clientGiro: ClientGiroDTO? = null
    var clientDestination: QueryThirdDTO? = null
    var concepts: List<GiroConceptDTO>? = null
    var agency: AgencyDTO? = null
    var clientDestinationFull: ResponseThirdDTO? = null
    var clientOriginFull: ResponseThirdDTO? = null
    var unusualOperations: String? = null
    var unusualOperationsCode: String? = null
    var description: String? = null
}