package com.sbpinilla.consultactivity.data.repositories

import android.util.Log
import com.sbpinilla.consultactivity.AppApplication
import com.sbpinilla.consultactivity.data.database.DBHelperOperations
import com.sbpinilla.consultactivity.data.database.DBHelperResponse
import com.sbpinilla.consultactivity.data.database.entity.EmployeeEntityRoom
import io.reactivex.Observable
import java.lang.Exception

interface IEmployeeEntityRepository {

    fun getAllEmployeeEntity(): List<EmployeeEntityRoom>?
    fun deleteAllEmployeeEntity(): Observable<DBHelperResponse>
    fun saveEmployeeEntityInLocal(productLotteries: List<EmployeeEntityRoom>): Observable<DBHelperResponse>


}

class EmployeeEntityRepository:IEmployeeEntityRepository{
    override fun getAllEmployeeEntity(): List<EmployeeEntityRoom>? {

        val dbInstance = AppApplication.getDatabaseInstance()
        var data = listOf<EmployeeEntityRoom>()
        dbInstance?.employeeDao()?.getAll().let {
            it?.let { listEmployees ->
                data = listEmployees
            }
        }
        return data

       }

    override fun deleteAllEmployeeEntity(): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.deleteAllTable
        }
        try {
            AppApplication?.getDatabaseInstance()?.employeeDao()?.deleteAll()
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
       }

    override fun saveEmployeeEntityInLocal(employees: List<EmployeeEntityRoom>): Observable<DBHelperResponse> {


        Log.d("MAIN","REPOSITORY")

        val response = DBHelperResponse().apply {
            status = true
            totalRows = 0
            identifier = null
            operation = DBHelperOperations.insertRows
        }

        Log.d("MAIN","REPOSITORY 1")
        val dbInstance = AppApplication.getDatabaseInstance()

        Log.d("MAIN","REPOSITORY 2")
        try {
            Log.d("MAIN","REPOSITORY SAVE 1")
            dbInstance?.employeeDao()?.saveEmpleoyeeEntity(employees)
            Log.d("MAIN","REPOSITORY SAVE 2")

        }
        catch (e: Exception) {

            response.status = false
            Log.d("MAIN","REPOSITORY ERROR")

        }
        return Observable.just(response)
    }



}