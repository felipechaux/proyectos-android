package com.sbpinilla.consultactivity.data.database.entity

import androidx.room.*
import com.sbpinilla.consultactivity.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity
data class EmployeeEntityRoom (

    @androidx.room.PrimaryKey val id: String,

    @ColumnInfo
    var employeeName: String? = null,

    @ColumnInfo
    var employeeSalary: String? = null,

    @ColumnInfo
    var employeeAge: String? = null,

    @ColumnInfo
    var profileImage: String? = null
)

@Dao
abstract class EmployeeEntityRoomDAO:BaseDAO<EmployeeEntityRoom>{

    @Insert
    abstract fun saveEmpleoyeeEntity(items: List<EmployeeEntityRoom>)


    @Query("SELECT * FROM EmployeeEntityRoom")
    abstract fun getAll(): List<EmployeeEntityRoom>

    @Query("delete from EmployeeEntityRoom")
    abstract fun deleteAll()

}
