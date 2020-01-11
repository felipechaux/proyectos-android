package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.ModalityEntityRoom
import io.reactivex.Observable

interface IModalityRepository {
    fun saveModalitiesInLocalRoom(modalities: List<ModalityEntityRoom>): Observable<DBHelperResponse>
}