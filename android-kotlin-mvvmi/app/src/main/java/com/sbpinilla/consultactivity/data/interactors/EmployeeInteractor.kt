package com.sbpinilla.consultactivity.data.interactors

import android.util.Log
import androidx.room.DatabaseView
import com.sbpinilla.consultactivity.data.database.DatabaseViewModel
import com.sbpinilla.consultactivity.data.database.entity.EmployeeEntityRoom
import com.sbpinilla.consultactivity.data.dto.EmployeeDTO
import com.sbpinilla.consultactivity.data.dto.request.RequestEmployeeDTO
import com.sbpinilla.consultactivity.data.repositories.IEmployeeEntityRepository
import com.sbpinilla.consultactivity.data.repositories.IEmployeeRepository
import com.sbpinilla.consultactivity.di.DaggerEmployeeComponent
import com.sbpinilla.consultactivity.di.EmployeeEntityModule
import com.sbpinilla.consultactivity.di.EmployeeModule
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


interface IEmployeeInteractor {

    fun consultarEmployee(requestEmployeeDTO: RequestEmployeeDTO): Observable<List<EmployeeDTO>>?
    fun saveEmployeeEntityInLocal(employeesDTO: List<EmployeeDTO>)
}

class EmployeeInteractor : IEmployeeInteractor {


    @Inject
    lateinit var employeeRepository: IEmployeeRepository

    @Inject
    lateinit var employeeEntityRepository: IEmployeeEntityRepository

    init {
        DaggerEmployeeComponent.builder()
            .employeeModule(EmployeeModule())
            .build()
            .inject(this)
    }

    constructor()

    constructor(employeeRepository: IEmployeeRepository) {
        this.employeeRepository = employeeRepository
    }


    override fun consultarEmployee(requestEmployeeDTO: RequestEmployeeDTO): Observable<List<EmployeeDTO>>? {

        return employeeRepository
            ?.consultarEmployye(requestEmployeeDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)

            }

    }

    override fun saveEmployeeEntityInLocal(employeesDTO: List<EmployeeDTO>) {


        Log.d("MAIN","INTERACTOR")
        employeeEntityRepository.deleteAllEmployeeEntity()

        val employeesEntity = arrayListOf<EmployeeEntityRoom>()

        employeesDTO.forEach {
            val employeeDTO = it

            val employeeEntity = EmployeeEntityRoom(
                employeeDTO.id!!,
                "",
                "",
                "",
                ""
            )

            employeesEntity.add(employeeEntity)

        }

        if (employeesEntity.size > 0) {

            Log.d("MAIN","INTERACTOR EMPLOYEE MORE ZERO")
            employeeEntityRepository.saveEmployeeEntityInLocal(employeesEntity.filter { it.id.toInt() < 89300})
            DatabaseViewModel.database.onNext(DatabaseViewModel.DatabaseEvents.EMPLOYEE_CREATE)
        }

    }


}