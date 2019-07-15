package com.sbpinilla.consultactivity.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sbpinilla.consultactivity.data.database.entity.EmployeeEntityRoom
import com.sbpinilla.consultactivity.data.database.entity.EmployeeEntityRoomDAO

@Database(entities = [EmployeeEntityRoom::class],version = 1)

abstract class AppDatabase:RoomDatabase(){
abstract fun employeeDao():EmployeeEntityRoomDAO
}