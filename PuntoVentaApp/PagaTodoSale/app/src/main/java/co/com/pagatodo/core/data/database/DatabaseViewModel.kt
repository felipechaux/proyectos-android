package co.com.pagatodo.core.data.database

import io.reactivex.subjects.BehaviorSubject

class DatabaseViewModel {

    enum class DatabaseEvents {
        LOTTERIES_MASTER_ADDESD,
        CHANCE_LOTTERIES_ADDED,
        MODALITIES_ADDED,
        PROMOTIONAL_ADDED,
        MENUS_ADDED
    }

    companion object {
        var database = BehaviorSubject.create<DatabaseEvents>()
    }
}