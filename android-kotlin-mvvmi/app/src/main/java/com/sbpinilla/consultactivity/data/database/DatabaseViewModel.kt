package com.sbpinilla.consultactivity.data.database

import io.reactivex.subjects.BehaviorSubject

class DatabaseViewModel {

    enum class DatabaseEvents {
        EMPLOYEE_DELETE,
        EMPLOYEE_CREATE
    }

    companion object {
        var database = BehaviorSubject.create<DatabaseEvents>()
    }
}