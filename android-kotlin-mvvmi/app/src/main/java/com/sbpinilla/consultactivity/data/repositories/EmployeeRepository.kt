package com.sbpinilla.consultactivity.data.repositories

import com.sbpinilla.consultactivity.data.database.entity.EmployeeEntityRoom
import com.sbpinilla.consultactivity.data.dto.EmployeeDTO
import com.sbpinilla.consultactivity.data.dto.request.RequestEmployeeDTO
import com.sbpinilla.consultactivity.network.ApiFactory
import io.reactivex.Observable

interface IEmployeeRepository {

    fun consultarEmployye(requestEmployeeDTO: RequestEmployeeDTO): Observable<List<EmployeeDTO>>?

    fun consultarEmployeeEntity(): Observable<List<EmployeeEntityRoom>>?


}

class EmployeeRepository : IEmployeeRepository {
    override fun consultarEmployeeEntity(): Observable<List<EmployeeEntityRoom>>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun consultarEmployye(requestEmployeeDTO: RequestEmployeeDTO): Observable<List<EmployeeDTO>>? {

        return ApiFactory.build()?.searchEmployees()
    }


}