package co.com.pagatodo.core.data.repositories

import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.ModalityEntityRoom
import co.com.pagatodo.core.di.DaggerModalityComponent
import co.com.pagatodo.core.di.ModalityModule
import io.reactivex.Observable
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class ModalityRepository: IModalityRepository {

    init {
        DaggerModalityComponent.builder().modalityModule(ModalityModule()).build().inject(this)
    }

    override fun saveModalitiesInLocalRoom(modalities: List<ModalityEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRows
        }

        try {
            PagaTodoApplication.getDatabaseInstance()?.modalityDao()?.insertAll(modalities)
        }
        catch (e: Exception) {
            response.status = false
            val exceptionMessage = "Failed transaction exception: ${e.message} [${ModalityEntityRoom::class.simpleName}]"
            Log.d("Exception", exceptionMessage)
        }

        return Observable.just(response)
    }
}