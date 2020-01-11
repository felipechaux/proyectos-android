package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class CollectionSnrDTO {

    @SerializedName("numMatricula")
    var enrollmentNumber: String? = null
    @SerializedName("valor")
    var value: String? = null
    @SerializedName("descripcionConcepto")
    var descriptionConcept: String? = null

}