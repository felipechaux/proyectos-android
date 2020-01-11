package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.DocumentDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import co.com.pagatodo.core.data.dto.response.GiroTypeRequestsMessagesDTO

class GiroExFingerPrintModel {

    var third: ThirdDTO? = null

    var notes: String? = null

    var requestsMessages: GiroTypeRequestsMessagesDTO? = null

    var listDocument: List<DocumentDTO>? = null
}